package cz.studioart.godaddy.api

data class Contact(
    val address1: String,
    val city: String,
    val country: String,
    val postalCode: String,
    val state: String,
    val email: String,
    val nameFirst: String,
    val nameLast: String,
    val phone: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "addressMailing" to mapOf(
                "address1" to address1,
                "city" to city,
                "country" to country,
                "postalCode" to postalCode,
                "state" to state
            ),
            "email" to email,
            "nameFirst" to nameFirst,
            "nameLast" to nameLast,
            "phone" to phone
        )
    }
}