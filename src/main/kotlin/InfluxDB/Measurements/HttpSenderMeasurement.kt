package InfluxDB.Measurements

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.time.Instant
@Measurement(name = "senders")
class HttpSenderMeasurement(
    @Column(timestamp = true) val time: Instant?,
    @Column val value: Int,
    @Column(tag = true) val user: Int?,
    @Column(tag = true) val code: Int?,
    @Column(tag = true) val error: Boolean,
//    @Column(tag = true) val location: String?,
) {

}