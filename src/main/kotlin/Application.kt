import persistence.InMemoryDatabase
import domain.*
import services.UrlShortenerService

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val db = InMemoryDatabase.make()
        val service = UrlShortenerService.make(db)

//        TODO("checkout endpoint libraries")

        println("Running kotlin application")

//        var opt = Option(1)
//        opt = None
//
//        when (opt) {
//            is Some -> println("Got some ${opt.value}")
//            None -> println("Got none")
//        }

    }
}