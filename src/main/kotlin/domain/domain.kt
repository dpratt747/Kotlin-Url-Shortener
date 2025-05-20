package domain

@JvmInline
value class ShortUrl(val value: String)

@JvmInline
value class LongUrl(val value: String)

/**
 * Requests
 */

data class ShortenEndpointRequest(
    val longUrl: LongUrl
)

data class ShortenEndpointResponse(
    val shortUrl: ShortUrl
)


/**
 * Responses
 */
