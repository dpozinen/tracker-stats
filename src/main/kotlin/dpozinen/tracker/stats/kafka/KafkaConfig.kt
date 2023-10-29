package dpozinen.tracker.stats.kafka

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dpozinen.tracker.stats.domain.DataPoint
import dpozinen.tracker.stats.influx.InfluxService
import dpozinen.tracker.stats.postgres.PostgresService
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer


@EnableKafka
@Configuration
open class KafkaConfig {

    @Bean
    open fun consumerFactory(@Value("\${spring.kafka.bootstrap-servers}") kafkaAddress: String) =
        DefaultKafkaConsumerFactory(
            mapOf<String, Any>(
                BOOTSTRAP_SERVERS_CONFIG to kafkaAddress,
                GROUP_ID_CONFIG to "jormungandr",
            ),
            StringDeserializer(),
            jsonDeserializer()
        )

    private fun jsonDeserializer(): JsonDeserializer<List<DataPoint>> {
        val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        val type = mapper.typeFactory.constructParametricType(MutableList::class.java, DataPoint::class.java)

        return JsonDeserializer<List<DataPoint>>(type, mapper, false)
    }

    @Bean
    open fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, List<DataPoint>>) =
        ConcurrentKafkaListenerContainerFactory<String, List<DataPoint>>().also { it.consumerFactory = consumerFactory }

    @Bean
    open fun statsKafkaListener(
        influxService: InfluxService,
        postgres: PostgresService
    ) = StatsKafkaListener(influxService, postgres)

}