package cz.studioart.app

import cz.studioart.godaddy.api.GoDaddyAPI
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import cz.studioart.godaddy.response.DomainCheckResult
import cz.studioart.godaddy.response.formatSeconds
import cz.studioart.godaddy.response.measureDuration
import cz.studioart.godaddy.response.buildTable
import cz.studioart.godaddy.response.yesOrNo
import java.util.concurrent.Executors

@Component
class GoDaddyRunner(private val goDaddyAPI: GoDaddyAPI) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(GoDaddyRunner::class.java)
    private val virtualThreadDispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    override fun run(vararg args: String?) {
        checkAvailableDomains()
    }

    private fun checkAvailableDomains() {
        println("")
        logger.info("👀 Starting domain availability checks via GoDaddy")

        val domains = DomainsToCheck.list
        if (domains.isEmpty()) {
            logger.error("⛔️ No domains to check.")
            return
        }

        val rows = mutableListOf<DomainCheckResult>()
        val startTime = System.currentTimeMillis()

        runBlocking(virtualThreadDispatcher) {
            val jobs = domains.map { domain ->
                async {
                    try {
                        val (isAvailable, duration) = measureDuration {
                            goDaddyAPI.isDomainAvailable(domain)
                        }
                        if (isAvailable is Boolean) {
                            logger.info("$domain is ${yesOrNo(isAvailable, "available ⚠️", "not available")} to buy (${formatSeconds(duration)})")
                            rows.add(DomainCheckResult(domain, isAvailable, duration))
                        }
                    } catch (e: Exception) {
                        logger.error("An error occurred while checking domain $domain availability", e.message)
                        coroutineContext.cancel()
                    }
                }
            }
            jobs.awaitAll()
        }

        val endTime = System.currentTimeMillis()
        val durationSeconds = (endTime - startTime) / 1000.0

        if (rows.size > 0) {
            logger.info("\r\r" + buildTable(rows).toString())
        }
        logger.info("👀 Whole list lookup took about ${formatSeconds(durationSeconds)}. \r\r")
        println("")
    }
}
