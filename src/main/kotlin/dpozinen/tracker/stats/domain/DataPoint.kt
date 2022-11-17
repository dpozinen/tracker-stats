package dpozinen.tracker.stats.domain

import java.time.Instant

class DataPoint(
    val torrentId: String,
    val name: String,
    val size: Long,
    val dateAdded: Instant,

    val upSpeed: Long,
    val downSpeed: Long,
    val uploaded: Long,
    val downloaded: Long,
    val timestamp: Instant
)