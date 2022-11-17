package dpozinen.tracker.stats.kafka

import dpozinen.tracker.stats.domain.DataPoint
import dpozinen.tracker.stats.influx.InfluxService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class StatsKafkaListener(private val influxService: InfluxService) {

    @KafkaListener(topics = ["stats"])
    fun receive(stats: List<DataPoint>) {
        influxService.write(stats)
    }

}