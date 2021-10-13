package InfluxDB

import com.uchuhimo.konf.ConfigSpec

class InfluxDBConf {
    companion object : ConfigSpec("influxdb") {
        val url by optional<String?>(null) //"http://localhost:8086"
        val token by optional<String>("my-token")
        val org by optional<String>("my-org")
        val bucket by optional<String>("my-bucket")
    }
}
