package dpozinen.tracker.stats.postgres

import dpozinen.tracker.stats.domain.DataPoint
import jakarta.annotation.PostConstruct
import mu.KotlinLogging.logger
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
@ConditionalOnProperty("tracker-stats.postgres.enabled", havingValue = "true", matchIfMissing = true)
class DefaultPostgresService(private val torrentRepository: TorrentRepository) : PostgresService {
    private val log = logger {}

    private val cache: MutableSet<String> = ConcurrentHashMap.newKeySet()

    @PostConstruct
    fun initCache() {
        cache.addAll(torrentRepository.findAll().map { it.id })
    }

    override fun write(dataPoints: List<DataPoint>) {
        dataPoints.filter { cache.add(it.torrentId) }
            .forEach { log.info { "Added new torrent ${it.name}" } }

        dataPoints.map {
            TorrentMeta(it.torrentId, it.name, it.size, it.dateAdded, it.uploaded)
        }.let { torrentRepository.saveAll(it) }
    }

}

class NoopPostgresService: PostgresService {
    override fun write(dataPoints: List<DataPoint>) {}
}

interface PostgresService {
    fun write(dataPoints: List<DataPoint>)
}
