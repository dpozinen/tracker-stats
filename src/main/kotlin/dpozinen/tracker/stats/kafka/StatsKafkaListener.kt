package dpozinen.tracker.stats.kafka

import dpozinen.tracker.stats.domain.DataPoint
import dpozinen.tracker.stats.influx.InfluxService
import dpozinen.tracker.stats.postgres.PostgresService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

class StatsKafkaListener(
    private val influxService: InfluxService,
    private val postgres: PostgresService
) {

    @KafkaListener(topics = ["stats"])
    fun receive(stats: List<DataPoint>) {
        influxService.write(stats)
        postgres.write(stats)
    }

}