package cz.studioart.godaddy.api

import cz.studioart.godaddy.config.GoDaddyConfig
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class GoDaddyAPI(config: GoDaddyConfig) {

    private val logger = LoggerFactory.getLogger(GoDaddyAPI::class.java)

    init {
        config.verify()
        Unirest.config()
            .defaultBaseUrl(if (config.isProduction) "https://api.godaddy.com" else "https://api.ote-godaddy.com")
            .setDefaultHeader("Authorization", "sso-key ${config.apiKey}:${config.apiSecret}")
            .setDefaultHeader("Accept", "application/json")
            .cookieSpec("standard")
    }

    /**
     * Sends a GET request to the specified endpoint with query parameters.
     */
    private fun get(endpoint: String, queryParams: Map<String, Any?> = emptyMap()): HttpResponse<JsonNode> {
        return Unirest.get(endpoint)
            .queryString(queryParams)
            .asJson()
    }

    /**
     * Sends a POST request to the specified endpoint with a JSON body.
     */
    private fun post(endpoint: String, body: Any): HttpResponse<JsonNode> {
        return Unirest.post(endpoint)
            .header("Content-Type", "application/json")
            .body(body)
            .asJson()
    }

    /**
     * Checks if a domain is available for registration.
     */
    fun isDomainAvailable(domain: String): Boolean? {
        val response = get("/v1/domains/available", mapOf("domain" to domain))
        if (response.status != 200) {
            logger.error("$domain - Error ${response.status} ${response.body}")
            return null
        }
        return response.body.`object`.optBoolean("available", false)
    }

    /**
     * Purchases a domain.
     */
    fun purchaseDomain(
        domain: String,
        adminContact: Contact,
        billingContact: Contact,
        registrantContact: Contact,
        techContact: Contact
    ): Boolean {
        val payload = mapOf(
            "consent" to mapOf(
                "agreedAt" to ZonedDateTime.now().toString(),
                "agreedBy" to "127.0.0.1",
                "agreementKeys" to listOf("DNRA")
            ),
            "contactAdmin" to adminContact.toMap(),
            "contactBilling" to billingContact.toMap(),
            "contactRegistrant" to registrantContact.toMap(),
            "contactTech" to techContact.toMap(),
            "domain" to domain,
            "nameServers" to listOf("ns1.example.com", "ns2.example.com"),
            "period" to 1,
            "privacy" to false,
            "renewAuto" to true
        )

        return try {
            val response = post("/v1/domains/purchase", payload)
            if (response.status == 200) {
                logger.info("Domain purchased successfully: $domain")
                true
            } else {
                logger.error("Failed to purchase domain: ${response.statusText} (Code: ${response.status})")
                false
            }
        } catch (e: Exception) {
            logger.error("An error occurred while purchasing the domain: $domain", e)
            false
        }
    }
}