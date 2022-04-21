package eu.kanade.tachiyomi.extension.en.elgoonishshive

import android.os.Build
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import rx.Observable
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class ElGoonishShive : ParsedHttpSource() {

    override val name = "El Goonish Shive"

    override val baseUrl = "https://www.egscomics.com"

    override val lang = "en"

    override val supportsLatest = false

    private var counter = 1

    override fun fetchPopularManga(page: Int): Observable<MangasPage> {
        val egs = SManga.create().apply {
            title = "El Goonish Shive"
            artist = "Dan Shive"
            author = "Dan Shive"
            status = SManga.ONGOING
            url = "/comic/archive"
            description = "A strange comic about a group of teenagers and the bizarre, often supernatural, situations that they face. Includes a continuing storyline with non-linear joke comics on the side. WARNING: Often ignores the laws of Physics."
            thumbnail_url = "https://hiveworkscomics.com/frontboxes/300x250_EGS.png"
        }

        val egsnp = SManga.create().apply {
            title = "El Goonish Shive: Newspaper"
            artist = "Dan Shive"
            author = "Dan Shive"
            status = SManga.ONGOING
            url = "/egsnp/archive"
            description = "A sub-comic of El Goonish Shive"
            thumbnail_url = "https://www.egscomics.com/images/logo2.gif"
        }

        val sketchbook = SManga.create().apply {
            title = "El Goonish Shive: Sketchbook"
            artist = "Dan Shive"
            author = "Dan Shive"
            status = SManga.ONGOING
            url = "/sketchbook/archive"
            description = "A collection of sketches by the author of EGS"
            thumbnail_url = "https://www.egscomics.com/images/logo.png"
        }

        return Observable.just(MangasPage(arrayListOf(egs, egsnp, sketchbook), false))
    }

    override fun fetchSearchManga(page: Int, query: String, filters: FilterList): Observable<MangasPage> = Observable.just(MangasPage(emptyList(), false))

    override fun fetchMangaDetails(manga: SManga) = Observable.just(manga)

    override fun chapterListParse(response: Response): List<SChapter> {
        counter = 1
        return super.chapterListParse(response).distinct().reversed()
    }

    override fun chapterListSelector() = "div#leftarea select option:gt(0)"

    override fun chapterFromElement(element: Element): SChapter {
        // val doc: Document = Jsoup.connect(baseUrl + "/" + element.attr("value")).get()
        val chapter = SChapter.create()
        chapter.url = "/" + element.attr("value")
        chapter.name = counter.toString() + " - " + element.text().substringAfter(" - ")
        chapter.chapter_number = counter++.toFloat() // chapter.url.substringAfterLast("/").toFloat()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ofPattern = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
            chapter.date_upload = LocalDate.parse(element.text().substringBefore(" - "), ofPattern).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
        return chapter
    }

    override fun pageListParse(document: Document) = document.select("img#cc-comic").mapIndexed { i, element -> Page(i, "", element.attr("src")) }

    override fun imageUrlParse(document: Document) = throw Exception("Not used")

    override fun popularMangaSelector(): String = throw Exception("Not used")

    override fun searchMangaFromElement(element: Element): SManga = throw Exception("Not used")

    override fun searchMangaNextPageSelector(): String? = throw Exception("Not used")

    override fun searchMangaSelector(): String = throw Exception("Not used")

    override fun popularMangaRequest(page: Int): Request = throw Exception("Not used")

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request = throw Exception("Not used")

    override fun popularMangaNextPageSelector(): String? = throw Exception("Not used")

    override fun popularMangaFromElement(element: Element): SManga = throw Exception("Not used")

    override fun mangaDetailsParse(document: Document): SManga = throw Exception("Not used")

    override fun latestUpdatesNextPageSelector(): String? = throw Exception("Not used")

    override fun latestUpdatesFromElement(element: Element): SManga = throw Exception("Not used")

    override fun latestUpdatesRequest(page: Int): Request = throw Exception("Not used")

    override fun latestUpdatesSelector(): String = throw Exception("Not used")
}
