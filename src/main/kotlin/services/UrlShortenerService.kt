package services

import domain.LongUrl
import domain.ShortUrl
import persistence.DBAlg
import kotlin.random.Random
import arrow.core.Option

interface UrlShortenerServiceAlg {
    fun storeLongUrlAndReturnShortUrl(longUrl: LongUrl): ShortUrl
    fun getAll(): Map<LongUrl, ShortUrl>
    fun getLongUrlWithShortUrl(shortUrl: ShortUrl): Option<LongUrl>
}

class UrlShortenerService private constructor(private val db: DBAlg): UrlShortenerServiceAlg {
    override fun storeLongUrlAndReturnShortUrl(longUrl: LongUrl): ShortUrl {
        val shortUrl = ShortUrl(generateRandomString(5))
        db.store(longUrl, shortUrl)
        return shortUrl
    }

    override fun getAll(): Map<LongUrl, ShortUrl> {
        return db.getAll()
    }

    override fun getLongUrlWithShortUrl(shortUrl: ShortUrl): Option<LongUrl> {
        return db.getLongestUrlByShortUrl(shortUrl)
    }

    companion object {
        fun make(db: DBAlg): UrlShortenerServiceAlg {
            return UrlShortenerService(db)
        }

        fun generateRandomString(length: Int): String {
            val charPool = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..length)
                .map { Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }
    }
}