package dpozinen.tracker.stats.influx

import com.influxdb.client.domain.Bucket
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InfluxConfig {

    @Bean
    fun influxDbClient(
        @Value("\${influx.org}") org: String,
        @Value("\${influx.url}") url: String,
        @Value("\${influx.token}") token: String,
        @Value("\${influx.bucket}") bucket: String,
    ): InfluxDBClientKotlin {
        return InfluxDBClientKotlinFactory.create(url, token.toCharArray(), org, bucket)
    }

    @Bean
    fun writeKotlinApi(client: InfluxDBClientKotlin) =
        client.getWriteKotlinApi()

    @Bean
    fun readKotlinApi(client: InfluxDBClientKotlin) =
        client.getQueryKotlinApi()

}