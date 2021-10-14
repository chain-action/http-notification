package Kafka

import com.uchuhimo.konf.source.yaml
import mu.KotlinLogging
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.security.scram.ScramCredential
import org.apache.kafka.common.security.scram.ScramLoginModule
import java.io.IOException
import java.time.Duration
import java.util.*


class KafkaConsumer(configYaml: com.uchuhimo.konf.Config?=null) {


    private val logger = KotlinLogging.logger {}

    private val broker: String
    private val group_id: String
    private val duration_ms: Long
    private val configKafkaYaml = configYaml ?:  com.uchuhimo.konf.Config { addSpec(KafkaConf) }.from.yaml.file("./config.yml")

    init {
        broker = configKafkaYaml[KafkaConf.server]
        group_id = configKafkaYaml[KafkaConf.group_id]
        duration_ms = configKafkaYaml[KafkaConf.duration_ms]
    }

    private fun create(brokers: String): Consumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["group.id"] = group_id

        props["enable.auto.commit"] = "false"
        props["auto.offset.reset"] = "earliest"
        props["max.poll.records"] = "1"
        props["auto.commit.interval.ms"] = "1000";
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java

        if (configKafkaYaml[KafkaConf.sasl_mechanisms]!=null && configKafkaYaml[KafkaConf.security_protocol]!=null ) {
//        props["sasl.username"] = configKafkaYaml[KafkaConf.username]
//        props["sasl.password"] = configKafkaYaml[KafkaConf.password]
            props[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = configKafkaYaml[KafkaConf.security_protocol]
            props[SaslConfigs.SASL_MECHANISM] = configKafkaYaml[KafkaConf.sasl_mechanisms]
            if (configKafkaYaml[KafkaConf.sasl_mechanisms] == "SCRAM-SHA-512") {
                props[SaslConfigs.SASL_JAAS_CONFIG] =
                    "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + configKafkaYaml[KafkaConf.username] + "\" password=\"" + configKafkaYaml[KafkaConf.password] + "\";"
            } else {
                props[SaslConfigs.SASL_JAAS_CONFIG] =
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + configKafkaYaml[KafkaConf.username] + "\" password=\"" + configKafkaYaml[KafkaConf.password] + "\";"
            }
        }

        return KafkaConsumer(props)
    }
/*
    fun close(){
        consumer.close()
    }
    */

    fun run(topic: String, callback: ((consumerRecord: ConsumerRecord<String,String>) -> Boolean), maxRepeat: Int?= null) {
        val consumer = create(broker)
        try {
            consumer.subscribe(listOf(topic))
        } catch (e: IOException) {
            logger.error { "Exception consumer.subscribe, $e" }
        }

        if (maxRepeat==null) {
            while (pool(consumer, callback)) {}
            logger.error { "Error consumer.pool()" }
            consumer.unsubscribe()
            consumer.close()
        } else {
            repeat(maxRepeat) {
                val result = pool(consumer, callback, Duration.ofMillis(duration_ms))
                if (!result) return@repeat
            }
            consumer.unsubscribe()
//            consumer.close()
        }
    }

    fun commitSync(consumer: Consumer<String, String>, offset: Long){
//        logger.info { "Success: ConsumerRecord=${it}" }
//        val partition0 = TopicPartition("foo", 0)
        val offsets = mapOf<TopicPartition?, OffsetAndMetadata?>(null to OffsetAndMetadata(offset))
        consumer.commitSync(offsets)
    }

    fun pool(consumer: Consumer<String, String>, callback: ((consumerRecord: ConsumerRecord<String,String>) -> Boolean), duration: Duration?=null): Boolean {
        val _duration = duration ?: Duration.ofMillis(duration_ms)
        val records: ConsumerRecords<String, String>?
        try {
//            records = consumer.poll(Duration.ofMillis(100))
            records = consumer.poll(_duration)
        } catch (e: Exception) {
            logger.error { "Exception consumer.poll() $e" }
            return false
        }
        if (!records.isEmpty) {
            records.iterator().forEach {
                logger.info { "Received -> ${it.topic()} [${it.partition()}], offset=${it.offset()}, key=${it.key()}" }
                callback(it).let { result ->
                    if (result) {
                        logger.debug { "Success: ConsumerRecord=${it}" }
                        consumer.commitSync()
                    } else {
                        logger.debug { "error: ConsumerRecord=${it}" }
                    }
                }
            }
        }
        return true
    }

}