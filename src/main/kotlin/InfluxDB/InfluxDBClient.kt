package InfluxDB

import InfluxDB.Measurements.HttpSenderMeasurement
import com.influxdb.annotations.Measurement
import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.uchuhimo.konf.source.yaml
import com.influxdb.client.InfluxDBClient as InfluxDBClientJava

class InfluxDBClient {

    private var influxDBClientJava: InfluxDBClientJava?=null
    private var influxDBClientKotlin: InfluxDBClientKotlin?=null

    private val config = com.uchuhimo.konf.Config { addSpec(InfluxDBConf) }.from.yaml.file("./config.yml")

    private val url: String?
    private val token: String
    private val org: String
    private val bucket: String

    val enabled: Boolean

    init {
        url=config[InfluxDBConf.url]
        token=config[InfluxDBConf.token]
        org=config[InfluxDBConf.org]
        bucket=config[InfluxDBConf.bucket]

        enabled = if (url!=null) {
            influxDBClientKotlin = InfluxDBClientKotlinFactory.create(url, token.toCharArray(), org, bucket)
            influxDBClientJava = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket)
            true
        }
        else {
            false
        }
    }

    fun insert(measurement: Any){
        influxDBClientJava?.writeApi.use { writeApi ->
            writeApi?.writeMeasurement<Any>(WritePrecision.MS, measurement)
        }
    }

}