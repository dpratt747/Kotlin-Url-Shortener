package persistence

import arrow.core.Option
import arrow.core.firstOrNone
import domain.*
import java.util.concurrent.atomic.AtomicReference

interface DBAlg {
    fun store(longUrl: LongUrl, shortUrl: ShortUrl)
    fun getAll(): Map<LongUrl, ShortUrl>
    fun getLongestUrlByShortUrl(url: ShortUrl): Option<LongUrl>
}

class InMemoryDatabase private constructor(private val state: AtomicReference<MutableMap<LongUrl, ShortUrl>>) : DBAlg {
    override fun store(longUrl: LongUrl, shortUrl: ShortUrl) {
        state.getAndUpdate { map ->
            map.put(longUrl, shortUrl)
            map
        }
    }

    override fun getAll(): Map<LongUrl, ShortUrl> {
        return state.get().toMap()
    }

    override fun getLongestUrlByShortUrl(url: ShortUrl): Option<LongUrl> {
        return state.get().filter { (_, value) ->
            value == url
        }.keys.firstOrNone()
    }

    companion object {
        fun make(state: MutableMap<LongUrl, ShortUrl> = mutableMapOf()): DBAlg {
            return InMemoryDatabase(AtomicReference(state))
        }
    }
}