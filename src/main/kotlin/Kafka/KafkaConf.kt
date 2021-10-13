package Kafka

import com.uchuhimo.konf.ConfigSpec

class KafkaConf {
    companion object : ConfigSpec("kafka") {
        val server by optional<String>("localhost:9092")
        val group_id by optional<String>("default")
        val duration_ms by optional(100L)
        val username by optional<String?>(null)
        val password by optional<String?>(null)
        val sasl_mechanisms by optional<String?>(null) //SCRAM-SHA-512
        val security_protocol by optional<String?>(null) // sasl_plaintext
    }
}
