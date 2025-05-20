import endpoints.ShortenerEndpoints
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.routing.*
import org.http4k.server.*
import persistence.InMemoryDatabase
import services.UrlShortenerService

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val db = InMemoryDatabase.make()
        val service = UrlShortenerService.make(db)
        val shortenerEndpoints = ShortenerEndpoints.make(service)

        val v1DescriptionPath = "/v1/openapi.json"
        val renderer = OpenApi3(
            ApiInfo("Url Shortener Service", "1.0"),
        )

        val http = routes(
            shortenerEndpoints.endpoints(renderer, v1DescriptionPath),
            "/v1/docs" bind swaggerUiLite { // http://localhost:8080/docs
                url = v1DescriptionPath
                pageTitle = "Url Shortener - Swagger Doc"
                persistAuthorization = true
            }
        )

        http.asServer(
            SunHttp(8080)
        ).start()

        println("Application is running")
    }
}