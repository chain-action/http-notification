package Utils

import mu.KotlinLogging
import org.json.JSONObject

class TmpKafkaPayload {
    private val logger = KotlinLogging.logger {}

    private val regex = Regex(pattern = "^([a-z0-9]+) (.+?) (.+?)$")

    fun getPair(str: String): Triple<String, String, String>? {
        regex.matchEntire(str)?.let {
//            val strJson = it.groupValues[1]
            try {
                return Triple(it.groupValues[1], it.groupValues[2], it.groupValues[3])
            } catch (e: Exception) {
                logger.error { "Exception $e" }
            }
        }
        return null
    }
}