package cz.studioart.godaddy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class GoDaddyConfig(
    @Value("\${godaddy.api.apiKey}") val apiKey: String,
    @Value("\${godaddy.api.apiSecret}") val apiSecret: String,
    @Value("\${godaddy.api.isProduction}") val isProduction: Boolean
) {
    private val logger = LoggerFactory.getLogger(GoDaddyConfig::class.java)

    fun verify() {
        if (apiKey === "xxx") {
            logger.error("GoDaddy API key is missing!")
        }
        if (apiSecret === "xxx") {
            logger.error("GoDaddy API secret is missing!")
        }
    }
}
