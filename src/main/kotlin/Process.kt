import Config.App
import Db.DbItems
import Enums.EnumNotifyStatus
import Kafka.KafkaConsumer
import Models.Item
import Redis.Redis
import Redis.RedisConf
import Utils.TmpKafkaPayload
import com.uchuhimo.konf.source.yaml
import kotlinx.coroutines.*
import mu.KotlinLogging

class Process {

    private val logger = KotlinLogging.logger {}

    //    private val configHttpServerYaml = com.uchuhimo.konf.Config { addSpec(HttpServer) }.from.yaml.file("./config.yml")
    private val configAppYaml = com.uchuhimo.konf.Config { addSpec(App) }.from.yaml.file("./config.yml")
    private val configRedisYaml = com.uchuhimo.konf.Config { addSpec(RedisConf) }.from.yaml.file("./config.yml")


    private val jedis = Redis(configRedisYaml).jedis
    private val notifyStatus = NotifyStatus(jedis)
    private val httpItem = HttpItem()
    private val dbItems = DbItems()
    private val tmpKafkaPayload = TmpKafkaPayload()
    private val retryDB = RetryDB(configAppYaml[App.coeff_retry])
    private val issetProcess = mutableMapOf<String, Boolean>()

    private val maxRepeat = configAppYaml[App.max_repeat]
    private val maxDelayMillis: Long = configAppYaml[App.max_delay]
    private val offsetDB = OffsetDB(configAppYaml[App.kafka_topic])

    private val consumer = KafkaConsumer()

    fun consume(loop: Boolean = true) = runBlocking {
//    fun consume(loop: Boolean= true) = GlobalScope.async<Unit> {
        val maxRepeat = if (loop) null else 1
        val job = launch {


//        GlobalScope.launch {
            println("run($loop)")
            /*    launch {
                    delay(1000)
                    logger.info { "launch { delay(1000) }" }
                }*/
//                            }
            consumer.run(configAppYaml[App.kafka_topic], {

                logger.debug { "it.value(): ${it.value()}" }
                val offset = it.offset()

                val payloadObj = it.value().let {
                    if (it == null) {
                        null
                    } else {
                        tmpKafkaPayload.getPair(it)
                    }
                }

                val key = it.key().let {
                    if (it == null) {
                        logger.warn { "key is null" }
                        return@run true
//                        val faker = Faker()
//                        faker.name().username()
                    } else {
                        it
                    }
                }

                if (payloadObj != null && it.value() != null) {
                    val uid = payloadObj.first
                    val txtPayload = it.value()!!

                    val item = dbItems.get(uid)
                    if (item != null) {
//                        logger.info { "KafkaConsumer().run: $it" }
//                        val hash = retryDB.getHash(key, uid)
                        val hash = key

//                        logger.info { "hash $hash" }

                        val minOffset = offsetDB.minEnd()
//                        logger.info { "minOffset $minOffset, offset $offset" }
                        if (minOffset == offset) {
                            logger.info { "OK offset $offset" }
                            notifyStatus.clean(hash)
                            offsetDB.clearEnd(offset)
                            return@run true
                        }

                        offsetDB.add(hash, offset)

                        val notify = notifyStatus.get(hash)
                        logger.debug { "notify $notify" }
                        if (notify == EnumNotifyStatus.END || notify == EnumNotifyStatus.SUCCESS) {
                            logger.info { "Recovery OK notify $notify, hash=$hash" } //44f3bd27f72440141a74
                            offsetDB.end(hash)
                            notifyStatus.clean(hash)
                            logger.info { "end Recovery OK notify $notify" }
                        } else if (notify == EnumNotifyStatus.PROCCESS) {
                            logger.info { "Skipping notify $notify" }
                            return@run false
                        } else if (notify == EnumNotifyStatus.NOTFOUND) {
                            logger.info { "NOTFOUND notify $notify" }
                            notifyStatus.set(hash, EnumNotifyStatus.PROCCESS)
//                            notifyStatus.set(hash, EnumNotifyStatus.ERROR)
//                            logger.info { "runGlobalScope($hash, $item, $txtPayload)" }
//                            launch {
                            runGlobalScope(hash, item, txtPayload)
//                            }
                            return@run false
                        } else if (notify == EnumNotifyStatus.ERROR) {
                            logger.info { "notify $notify" }
//                            notifyStatus.set(hash, EnumNotifyStatus.PROCCESS)
                            if (issetProcess.contains(hash)) {
                                logger.info { "is running process $hash" }
                            } else {
                                logger.warn { "renew process $hash" }
//                                logger.info { "renew runGlobalScope($hash, $item, $txtPayload)" }
//                                launch {
                                runGlobalScope(hash, item, txtPayload)
//                                }
                            }
                            return@run false
                        } else {
                            logger.error { "Notify: $notify" }
                            return@run false
                        }
                    } else {
                        logger.warn { "notfind key $uid" }
                        return@run true
                    }
                } else {
                    logger.warn { "payload is null" }
                    return@run true
                }
                return@run false
            }, maxRepeat)
//        }
        }
        job.join()
//        logger.warn { "!! End consume()" }
    }

    //    private fun runGlobalScope(hash: String, item: Item, txtPayload: String) {
//    private suspend fun runGlobalScope(hash: String, item: Item, txtPayload: String) = coroutineScope {= GlobalScope.launch
    private fun runGlobalScope(hash: String, item: Item, txtPayload: String)/* = GlobalScope.launch*/ {
//        logger.info { "start runGlobalScope($hash: ..." }
        issetProcess.put(hash, true)
        GlobalScope.launch {
            val jedis = Redis(configRedisYaml).jedis
            val notifyStatus = NotifyStatus(jedis)
            var delayMillis = 0L
            var repeat = -1
            while (true) {
                repeat++
                delayMillis = retryDB.getDelayMillis(hash, true)/*.let { delayMillis ->*/

                if (delayMillis > maxDelayMillis || repeat > maxRepeat) {
                    notifyStatus.set(hash, EnumNotifyStatus.END)
                    offsetDB.end(hash)
                    logger.warn { "break maxDelayMillis=$maxDelayMillis, maxRepeat=$maxRepeat" }
                    break
                }
                logger.info { "Repeat №$repeat, delay ${delayMillis / 1000.0} second" }
                if (delayMillis != 0L) delay(delayMillis)
                val status = runHttpItem(item, txtPayload)
                if (status) {
                    notifyStatus.set(hash, EnumNotifyStatus.SUCCESS)
                    offsetDB.end(hash)
                    break
                } else {
                    notifyStatus.set(hash, EnumNotifyStatus.ERROR)
                }

            }
            issetProcess.remove(hash)
            retryDB.reset(hash)
            jedis.close()
            logger.debug { "end runGlobalScope($hash, ${item.url})" }
        }
    }

    private fun runHttpItem(item: Item, txtPayload: String): Boolean {
        val result = httpItem.run(item, txtPayload)
        if (result) {
            logger.info { "Success httpItem.run(${item.uid})" }
            return true
        } else {
            logger.warn { "Failed httpItem.run(${item.uid}, url: ${item.url})" }
        }
        return false
    }
}
