package dpozinen.tracker.stats.kafka

import dpozinen.tracker.stats.domain.DataPoint
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory


@EnableKafka
@Configuration
class KafkaConfig {

    @Bean
    fun consumerFactory(@Value("\${kafka.address}") kafkaAddress: String) =
        DefaultKafkaConsumerFactory<String, String>(
            mapOf<String, Any>(
                BOOTSTRAP_SERVERS_CONFIG to kafkaAddress,
                GROUP_ID_CONFIG to "jormungandr",
                KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            )
        )

    @Bean
    fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, String>) =
        ConcurrentKafkaListenerContainerFactory<String, String>().also { it.consumerFactory = consumerFactory }

}