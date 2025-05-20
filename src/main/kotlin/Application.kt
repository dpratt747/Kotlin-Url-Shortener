import com.fasterxml.jackson.databind.JsonNode
import endpoints.ShortenerEndpoints
import endpoints.ShortenerEndpoints.Companion.asJson
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import persistence.InMemoryDatabase
import services.UrlShortenerService

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val db = InMemoryDatabase.make()
        val service = UrlShortenerService.make(db)
        val routes = ShortenerEndpoints.make(service)

        println("Running kotlin application")

        val descriptionPath = "/openapi.json"
        val renderer = OpenApi3(ApiInfo("Url Shortener Service", "1.0"))

        val http = routes(
            routes.endpoints(renderer, descriptionPath),
            "/docs" bind swaggerUiLite { // http://localhost:8080/docs
                url = descriptionPath
                pageTitle = "Url Shortener - Swagger Doc"
                persistAuthorization = true
            }
        )

        http.asServer(
            SunHttp(8080)
        ).start()

//        var opt = Option(1)
//        opt = None
//
//        when (opt) {
//            is Some -> println("Got some ${opt.value}")
//            None -> println("Got none")
//        }

    }
}