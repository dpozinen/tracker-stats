package dpozinen.tracker.stats.influx

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.time.Instant
import java.time.LocalDate

@Measurement(name = "torrent")
data class TorrentMeasurement(
    @Column(tag = true)
    val torrentId: String,

    @Column(tag = true)
    val name: String,
    @Column(tag = true)
    val size: Long,
    @Column(tag = true)
    val dateAdded: Instant,

    @Column
    val upSpeed: Long,
    @Column
    val downSpeed: Long,
    @Column
    val uploaded: Long,
    @Column
    val downloaded: Long,

    @Column(timestamp = true)
    val timestamp: Instant
)