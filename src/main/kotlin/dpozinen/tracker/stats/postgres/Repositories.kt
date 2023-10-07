package dpozinen.tracker.stats.postgres

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TorrentRepository : JpaRepository<TorrentMeta, String>

@Repository
interface StatRepository : JpaRepository<Stat, Long>