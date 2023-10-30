package dpozinen.tracker.stats

import com.influxdb.client.kotlin.QueryKotlinApi
import dpozinen.tracker.stats.domain.DataPoint
import dpozinen.tracker.stats.postgres.TorrentRepository
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Index.atIndex
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.InfluxDBContainer
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.time.Instant.now
import java.time.temporal.ChronoUnit

@SpringBootTest(classes = [TrackerStatsApplication::class, TestConfig::class])
@ActiveProfiles("test")
class TrackerStatsApplicationTests {

    @Value("\${kafka.topic}")
    private lateinit var topic: String

    @Autowired
    private lateinit var torrentRepository: TorrentRepository

    @Autowired
    private lateinit var influx: QueryKotlinApi

    @Value("\${influx.org}")
    private lateinit var org: String

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Test
    fun `should save stats`() {
        val now = now()
        val dataPoint = DataPoint(
            torrentId = "822a16435d53f9f712f747eef53d693cfeb52768",
            name = "The Good Shepherd (2006) (1080p BluRay x265 Panda).mkv",
            size = 123,
            dateAdded = now,
            upSpeed = 1,
            downSpeed = 2,
            uploaded = 3,
            downloaded = 4,
            timestamp = now,
        )

        await().atMost(Duration.ofSeconds(60)).untilAsserted {
            kafkaTemplate.send(topic, listOf(dataPoint)).get()

            assertThat(torrentRepository.findAll())
                .hasSize(1)
                .satisfies({
                    assertThat(it.id).isEqualTo("822a16435d53f9f712f747eef53d693cfeb52768")
                    assertThat(it.name).isEqualTo("The Good Shepherd (2006) (1080p BluRay x265 Panda).mkv")
                    assertThat(it.size).isEqualTo(123)
                    assertThat(it.dateAdded.truncatedTo(ChronoUnit.SECONDS)).isEqualTo(now.truncatedTo(ChronoUnit.SECONDS))
                    assertThat(it.uploaded).isEqualTo(3)
                }, atIndex(0))
        }

        fun assertTorrentId(values: Map<String, Any>) {
            assertThat(values).extractingByKey("torrentId")
                .isEqualTo("822a16435d53f9f712f747eef53d693cfeb52768")
        }

        runBlocking {
            influx.query("""from(bucket: "torrents") |> range(start: -1d)""", org)
                .consumeEach { record ->
                    assertThat(record).satisfiesAnyOf({
                        assertThat(it.field).isEqualTo("downloaded")
                        assertThat(it.value).isEqualTo(4L)
                        assertTorrentId(it.values)
                    }, {
                        assertThat(it.field).isEqualTo("uploaded")
                        assertThat(it.value).isEqualTo(3L)
                        assertTorrentId(it.values)
                    }, {
                        assertThat(it.field).isEqualTo("downSpeed")
                        assertThat(it.value).isEqualTo(2L)
                        assertTorrentId(it.values)
                    }, {
                        assertThat(it.field).isEqualTo("upSpeed")
                        assertThat(it.value).isEqualTo(1L)
                        assertTorrentId(it.values)
                    })
                }
        }
    }

}

@Configuration
open class TestConfig {

    @Bean(destroyMethod = "stop")
    open fun influx(
        @Value("\${influx.bucket}") bucket: String,
        @Value("\${influx.token}") token: String,
        @Value("\${influx.org}") org: String,
        environment: ConfigurableEnvironment
    ): InfluxDBContainer<out InfluxDBContainer<*>>? {
        val influx = InfluxDBContainer(DockerImageName.parse("influxdb:2.0.7"))
            .withBucket(bucket)
            .withAdminToken(token)
            .withOrganization(org)
        influx.start()

        environment.propertySources.addFirst(
            MapPropertySource(
                "influxInfo",
                mapOf("embedded.influx.url" to influx.url)
            )
        )

        return influx
    }
}