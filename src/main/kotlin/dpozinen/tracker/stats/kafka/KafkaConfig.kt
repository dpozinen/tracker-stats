package dpozinen.tracker.stats.kafka

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dpozinen.tracker.stats.domain.DataPoint
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer


@EnableKafka
@Configuration
class KafkaConfig {

    @Bean
    fun consumerFactory(@Value("\${kafka.address}") kafkaAddress: String) =
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
    fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, List<DataPoint>>) =
        ConcurrentKafkaListenerContainerFactory<String, List<DataPoint>>().also { it.consumerFactory = consumerFactory }

}