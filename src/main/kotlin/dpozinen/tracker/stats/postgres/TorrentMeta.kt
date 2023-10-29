package dpozinen.tracker.stats.postgres

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant

@Entity(name = "torrent")
class TorrentMeta(
    @Id
    val id: String,
    val name: String,
    val size: Long,
    val dateAdded: Instant,
    val uploaded: Long
)