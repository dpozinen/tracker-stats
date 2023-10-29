package dpozinen.tracker.stats.kafka

import dpozinen.tracker.stats.domain.DataPoint
import dpozinen.tracker.stats.influx.InfluxService
import dpozinen.tracker.stats.postgres.PostgresService
import mu.KotlinLogging
import mu.KotlinLogging.logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

class StatsKafkaListener(
    private val influxService: InfluxService,
    private val postgres: PostgresService
) {
    private val log = logger {}

    @KafkaListener(topics = ["stats"])
    fun receive(stats: List<DataPoint>) {
        log.debug { "Received datapoints $stats" }
        influxService.write(stats)
        postgres.write(stats)
    }

}