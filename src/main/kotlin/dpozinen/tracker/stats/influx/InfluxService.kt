package dpozinen.tracker.stats.influx

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.WriteKotlinApi
import dpozinen.tracker.stats.domain.DataPoint
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging.logger
import org.springframework.stereotype.Service

@Service
class InfluxService(private val writeKotlinApi: WriteKotlinApi) {

    private val log = logger {}

    fun write(stats: List<DataPoint>) {
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