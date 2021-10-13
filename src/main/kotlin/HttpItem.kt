import Enums.Method
import HttpSender.OkHttpRequest
import InfluxDB.InfluxDBClient
import InfluxDB.Measurements.HttpSenderMeasurement
import Models.Item
import mu.KotlinLogging
import okhttp3.Response
import java.time.Instant

class HttpItem {

    private val okHttpRequest = OkHttpRequest()
    private val influxDBClient = InfluxDBClient()

    private val logger = KotlinLogging.logger {}

    fun run(item: Item, payload: String): Boolean {

        val url = item.url
        val method = item.method
        val query = item.query
//        item.
        var basicAuth: Pair<String, String>? = null
        val headers: MutableMap<String, String> = mutableMapOf()
        item.auth?.let {
            if (it.user!=null && it.pass!=null) {
                basicAuth = it.user to it.pass
            } else {
                it.bearer?.let {
                    headers["Authorization"] = "Bearer $it"
                }
            }
        }

        okHttpRequest.get(url, method, query, payload, headers, basicAuth ).let {
            insertInfluxDB(it, item.user)
            if (it != null) {
                val code = it.code
//                logger.info { "code $code" }
                if (method==Method.PUT) {
                   if (code==200 || code == 201 || code == 204) return true
                } else  {
                    if (code==200 ) return true
                }
            }
        }
        return false
    }

//    private  fun insertInfluxDB(responce: Response?, item_user: Int) =

    private fun insertInfluxDB(responce: Response?, item_user: Int): Unit {
        val measurement: HttpSenderMeasurement
        val instant = Instant.now()
        if (responce != null) {

            val code = responce.code
            logger.debug { "code $code" }
            measurement = HttpSenderMeasurement(instant, 1, item_user, code, false)

        } else {
            logger.debug { "responce == null" }
            measurement = HttpSenderMeasurement(instant, 0, item_user, null, true)
        }
        influxDBClient.insert(measurement)
    }
}