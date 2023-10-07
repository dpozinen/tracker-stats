package dpozinen.tracker.stats.postgres

import dpozinen.tracker.stats.domain.DataPoint
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class PostgresService(
    private val torrentRepository: TorrentRepository,
    private val statRepository: StatRepository
) {
    private val log = KotlinLogging.logger {}

    private val cache: MutableSet<String> = ConcurrentHashMap.newKeySet()

    @PostConstruct
    fun initCache() {
        cache.addAll(torrentRepository.findAll().map { it.id })
    }

    fun write(dataPoints: List<DataPoint>) {
        addNewTorrents(dataPoints.filter { cache.add(it.torrentId) })

        statRepository.saveAll(dataPoints.map {
            Stat(null, it.torrentId, it.upSpeed, it.downSpeed, it.uploaded, it.downloaded, it.timestamp)
        })
    }

    private fun addNewTorrents(dataPoints: List<DataPoint>) {
        dataPoints.map {
            TorrentMeta(it.torrentId, it.name, it.size, it.dateAdded)
        }.let { torrents ->
            log.info { "Added new torrents ${torrents.map { it.name }}" }
            torrentRepository.saveAll(torrents)
        }
    }

}