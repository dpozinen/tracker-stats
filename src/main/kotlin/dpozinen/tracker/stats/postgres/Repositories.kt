package dpozinen.tracker.stats.postgres

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnProperty("tracker-stats.postgres.enabled", havingValue = "true", matchIfMissing = true)
interface TorrentRepository : JpaRepository<TorrentMeta, String>
