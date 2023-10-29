package dpozinen.tracker.stats.postgres

import dpozinen.tracker.stats.domain.DataPoint
import jakarta.annotation.PostConstruct
import mu.KotlinLogging.logger
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class PostgresService(
    private val torrentRepository: TorrentRepository
) {
    private val log = logger {}

    private val cache: MutableSet<String> = ConcurrentHashMap.newKeySet()

    @PostConstruct
    fun initCache() {
        cache.addAll(torrentRepository.findAll().map { it.id })
    }

    fun write(dataPoints: List<DataPoint>) {
        dataPoints.filter { cache.add(it.torrentId) }
            .forEach { log.info { "Added new torrent ${it.name}" } }

        dataPoints.map {
            TorrentMeta(it.torrentId, it.name, it.size, it.dateAdded, it.uploaded)
        }.let { torrentRepository.saveAll(it) }
    }

}