package endpoints

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.routing.RoutingHttpHandler
import services.UrlShortenerServiceAlg

interface ShortenerEndpointsAlg {
    fun endpoints(renderer: OpenApi3<JsonNode>, descriptionPath: String): RoutingHttpHandler
}

class ShortenerEndpoints private constructor(private val service: UrlShortenerServiceAlg): ShortenerEndpointsAlg {

    private val shortenEndpoint = "/shorten" meta {
        operationId = "Shorten"
        summary = "Submit a long URL and then get a short url you can use instead"
        returning(Status.OK)
    } bindContract Method.POST to { _: Request ->
        val jsonResponse = Jackson.string("Todo")
        Response(Status.OK)
            .body(jsonResponse.toString())
            .asJson()
    }

    override fun endpoints(renderer: OpenApi3<JsonNode>, descriptionPath: String): RoutingHttpHandler {
        return contract {
            routes += shortenEndpoint
            this.renderer = renderer
            this.descriptionPath = descriptionPath
        }
    }

    companion object {
        fun make(service: UrlShortenerServiceAlg): ShortenerEndpoints {
            return ShortenerEndpoints(service)
        }

        fun Response.asJson(): Response {
            return this.header("Content-Type", "application/json")
        }
    }
}