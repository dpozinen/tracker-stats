package dpozinen.tracker.stats

import dpozinen.tracker.stats.domain.DataPoint
import dpozinen.tracker.stats.postgres.TorrentRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Index.atIndex
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import java.time.Duration
import java.time.Instant.now

@SpringBootTest
@ActiveProfiles("test")
class TrackerStatsApplicationTests {

    @Value("\${kafka.topic}")
    private lateinit var topic: String

    @Autowired
    private lateinit var torrentRepository: TorrentRepository

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

		kafkaTemplate.send(topic, listOf(dataPoint)).get()

        await().atMost(Duration.ofSeconds(20)).untilAsserted {
            assertThat(torrentRepository.findAll())
                .hasSize(1)
                .satisfies({
                    assertThat(it.id).isEqualTo("822a16435d53f9f712f747eef53d693cfeb52768")
                    assertThat(it.name).isEqualTo("The Good Shepherd (2006) (1080p BluRay x265 Panda).mkv")
                    assertThat(it.size).isEqualTo(123)
                    assertThat(it.dateAdded).isEqualTo(now)
                    assertThat(it.uploaded).isEqualTo(3)
                }, atIndex(0))
        }
    }

}
