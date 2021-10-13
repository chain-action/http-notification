package Kafka

import com.uchuhimo.konf.source.yaml
import mu.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import org.apache.kafka.clients.producer.ProducerRecord

class KafkaProducer {

    private var producer: Producer<String, String>
    private val logger = KotlinLogging.logger {}

    private val broker: String
    private val configKafkaYaml = com.uchuhimo.konf.Config { addSpec(KafkaConf) }.from.yaml.file("./config.yml")

    init {
        broker = configKafkaYaml[KafkaConf.server]
//        topic = configKafkaYaml[KafkaConf.topic]
        producer = create(broker)
    }

    private fun create(brokers: String): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["acks"] = "all";
        props["key.serializer"] = StringSerializer::class.java.canonicalName
        props["value.serializer"] = StringSerializer::class.java.canonicalName
        return KafkaProducer(props)
    }

    fun send(topic: String, value: String): Boolean{
        val record = ProducerRecord<String, String>(topic, value)
        val futureResult = producer.send(record)
        logger.info { futureResult.get() }
        return futureResult.isDone
    }


}