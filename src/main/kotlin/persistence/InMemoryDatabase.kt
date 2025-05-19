package persistence

import arrow.core.Option
import arrow.core.firstOrNone
import domain.*

interface DBAlg {
    fun store(longUrl: LongUrl, shortUrl: ShortUrl)
    fun getAll(): Map<LongUrl, ShortUrl>
    fun getLongestUrlByShortUrl(url: ShortUrl): Option<LongUrl>
}

class InMemoryDatabase private constructor(private val state: MutableMap<LongUrl, ShortUrl>) : DBAlg {
    override fun store(longUrl: LongUrl, shortUrl: ShortUrl) {
        state.put(longUrl, shortUrl)
    }

    override fun getAll(): Map<LongUrl, ShortUrl> {
        return state.toMap()
    }

    override fun getLongestUrlByShortUrl(url: ShortUrl): Option<LongUrl> {
        return state.filter { (_, value) ->
            value == url
        }.keys.firstOrNone()
    }

    companion object {
        fun make(state: MutableMap<LongUrl, ShortUrl> = mutableMapOf()): DBAlg {
            return InMemoryDatabase(state)
        }
    }
}