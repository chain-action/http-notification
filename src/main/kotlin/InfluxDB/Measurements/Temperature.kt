package InfluxDB.Measurements

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.time.Instant

@Measurement(name = "temperature")
private class Temperature {
    @Column(tag = true)
    var location: String? = null

    @Column(tag = true)
    var user: Int? = null

    @Column
    var value: Double? = null

    @Column(timestamp = true)
    var time: Instant? = null
}