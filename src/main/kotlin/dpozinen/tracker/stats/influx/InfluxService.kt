package dpozinen.tracker.stats.influx

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.WriteKotlinApi
import dpozinen.tracker.stats.domain.DataPoint
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging.logger

class DefaultInfluxService(private val writeKotlinApi: WriteKotlinApi): InfluxService {

    private val log = logger {}

    override fun write(stats: List<DataPoint>) {
        val measurements = stats.map { toMeasurement(it) }

        log.info { "Received measurements $measurements" }

        runBlocking {
            writeKotlinApi.writeMeasurements(measurements.asFlow(), WritePrecision.S)
        }
    }

    private fun toMeasurement(point: DataPoint) = TorrentMeasurement(
        point.torrentId,
        point.name,
        point.size,
        point.dateAdded,
        point.upSpeed,
        point.downSpeed,
        point.uploaded,
        point.downloaded,
        point.timestamp
    )

}

fun interface InfluxService {
    fun write(stats: List<DataPoint>)
}