package dpozinen.tracker.stats.postgres

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
@ConditionalOnProperty("tracker-stats.postgres.enabled", havingValue = "true", matchIfMissing = true)
interface TorrentRepository : JpaRepository<TorrentMeta, String>


@Configuration
open class PostgresConfig {

    @Bean
    @ConditionalOnProperty("tracker-stats.postgres.enabled", havingValue = "false")
    @ConditionalOnMissingBean(PostgresService::class)
    open fun postgresService(): PostgresService = NoopPostgresService()

    @Import(
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class
    )
    @Configuration
    @ConditionalOnProperty("tracker-stats.postgres.enabled", havingValue = "true", matchIfMissing = true)
    open class PostgresEnabledConfig
}