import Enums.EnumNotifyStatus
import Redis.Redis
import mu.KotlinLogging
import redis.clients.jedis.Jedis

class NotifyStatus(private val jedis: Jedis) {

    private val logger = KotlinLogging.logger {}

    private val namespace="ns:"


    private val cacheS: Long = 350000 // 1 day =  86400 second
    private val cacheExpire: Long = 3600

    fun get(hash: String): EnumNotifyStatus {
        val key = getKey(hash)
//        logger.info { "jedis.exists($key)" }
        if ( jedis.exists(key) ) {
            jedis.get(key)?.let {
                getEnum(it)?.let {
                    return it
                }
            }

        }
        return EnumNotifyStatus.NOTFOUND
    }

    private fun getKey(hash: String): String {
        return namespace+hash
    }

    fun set(hash: String, value: EnumNotifyStatus) = set(hash, value.name)

    private fun set(hash: String, value: String){
//        logger.info { "jedis.setex(getKey($hash), $cacheS, ${value})" }
        jedis.setex(getKey(hash), cacheS, value)
    }

    fun clean(hash: String){
        jedis.expire(getKey(hash), cacheExpire)
//        jedis.del(getKey(hash))
    }

    private fun getEnum(name: String): EnumNotifyStatus? {
        try {
            val enum = EnumNotifyStatus.valueOf(name)
            logger.debug { enum }
            return enum
        } catch ( e: Exception) {
            logger.error { "Exception $e" }
        }
        return null
    }

}