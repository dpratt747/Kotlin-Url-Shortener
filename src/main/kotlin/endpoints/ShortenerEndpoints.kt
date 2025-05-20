package endpoints

import arrow.core.None
import arrow.core.Some
import arrow.core.toOption
import com.fasterxml.jackson.databind.JsonNode
import domain.LongUrl
import domain.ShortUrl
import domain.ShortenEndpointRequest
import domain.ShortenEndpointResponse
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.path
import services.UrlShortenerServiceAlg

interface ShortenerEndpointsAlg {
    fun endpoints(renderer: OpenApi3<JsonNode>, descriptionPath: String): RoutingHttpHandler
}

class ShortenerEndpoints private constructor(private val service: UrlShortenerServiceAlg) : ShortenerEndpointsAlg {

    private val apiVersion = "v1"

    private val shortenEndpointRequestLens = Body.auto<ShortenEndpointRequest>().toLens()
    private val shortenEndpointResponseLens = Body.auto<ShortenEndpointResponse>().toLens()

    private val shortenEndpoint = "/$apiVersion/shorten" meta {
        summary = "Submit a long URL and then get a short url you can use instead"
        receiving(
            shortenEndpointRequestLens to ShortenEndpointRequest(
                LongUrl("https://api.example.com/v1/users/12345/orders?status=pending&include=details,shipping,tracking&limit=50&page=2")
            ),
            definitionId = "Example request body"
        )
        returning(
            Status.CREATED,
            shortenEndpointResponseLens to ShortenEndpointResponse(
                ShortUrl("http://localhost:8080/asd62")
            ),
        )
    } bindContract Method.POST to { request: Request ->
        val shortenEndpointRequest = shortenEndpointRequestLens(request)
        val shortUrl = service.storeLongUrlAndReturnShortUrl(shortenEndpointRequest.longUrl)
        val updatedShortUrl = ShortUrl("http://localhost:8080/${shortUrl.value}")

        val response = ShortenEndpointResponse(updatedShortUrl)

        Response(Status.CREATED)
            .with(shortenEndpointResponseLens of response)
            .asJson()
    }

    private val getAllEndpoint = "/$apiVersion/all" meta {
        operationId = "Get All"
        summary = "This request retrieves all shortened URLs"
        returning(Status.OK)
    } bindContract Method.GET to { _: Request ->
        val response: Map<LongUrl, ShortUrl> = service.getAll()
        val responseLens = Body.auto<Map<LongUrl, ShortUrl>>().toLens()
        Response(Status.OK)
            .with(responseLens of response)
            .asJson()
    }

    private val redirectToLongUrlEndpoint = "/{shortUrl}" meta {
        operationId = "Redirect to the stored long url"
        summary = "Redirects a shortened URL to its original destination"
        returning(Status.TEMPORARY_REDIRECT)
    } bindContract Method.GET to { request: Request ->

        println("-----------------------------")
        val urlPathOpt = request.path("shortUrl").toOption()
        
        val getLongUrl = urlPathOpt.flatMap { str ->
            service.getLongUrlWithShortUrl(ShortUrl(str))
        }

        when (getLongUrl) {
            is Some -> {
                Response(Status.TEMPORARY_REDIRECT).redirect(getLongUrl.value.value)
            }
            None -> Response(Status.BAD_REQUEST).body("Invalid or missing short URL")
        }
    }

    override fun endpoints(renderer: OpenApi3<JsonNode>, descriptionPath: String): RoutingHttpHandler {
        return contract {
            routes += listOf(
                shortenEndpoint,
                getAllEndpoint,
                redirectToLongUrlEndpoint
            )
            this.renderer = renderer
            this.descriptionPath = descriptionPath
        }
    }

    companion object {
        fun make(service: UrlShortenerServiceAlg): ShortenerEndpoints {
            return ShortenerEndpoints(service)
        }

        fun Response.redirect(url: String): Response {
            return this.header("Location", url)
        }

        fun Response.asJson(): Response {
            return this.header("Content-Type", "application/json")
        }
    }
}