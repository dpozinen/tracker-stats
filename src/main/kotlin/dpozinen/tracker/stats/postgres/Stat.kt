package dpozinen.tracker.stats.postgres

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.Instant

@Entity(name = "torrent_stat")
class Stat(
    @Id
    @GeneratedValue
    val id: Long?,
    val torrentId: String,
    val upSpeed: Long,
    val downSpeed: Long,
    val uploaded: Long,
    val downloaded: Long,
    val timestamp: Instant
)