package dpozinen.tracker.stats

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
open class TrackerStatsApplication

fun main(args: Array<String>) {
	runApplication<TrackerStatsApplication>(*args)
}
