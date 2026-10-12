package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

data class WikipediaSearchResult(
    val title: String,
    val snippet: String
)

data class WikipediaPageSummary(
    val title: String,
    val extract: String,
    val description: String?,
    val thumbnailUrl: String?
)

class WikipediaApiClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
) {

    private val userAgent = "PublicPulse/1.0 (Android; OpenCivicPulse/1.0; contact: publicpulse@example.org)"

    /**
     * Wikimedia Resmî REST API'sinden aylık gerçek sayfa görüntülenme sayısını çeker.
     * https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/tr.wikipedia.org/all-access/user/{article}/monthly/{start}/{end}
     */
    suspend fun getMonthlyPageviews(articleTitle: String): Long = withContext(Dispatchers.IO) {
        try {
            val formattedTitle = articleTitle.trim().replace(" ", "_")
            val encodedTitle = URLEncoder.encode(formattedTitle, "UTF-8")

            // Tarih aralığı: Son 3 ay
            val cal = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyyMM0100", Locale.US)
            val endDate = dateFormat.format(cal.time)
            cal.add(Calendar.MONTH, -2)
            val startDate = dateFormat.format(cal.time)

            val url = "https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/tr.wikipedia.org/all-access/user/$encodedTitle/monthly/$startDate/$endDate"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext 0L
            }

            val bodyString = response.body?.string() ?: return@withContext 0L
            val json = JSONObject(bodyString)
            val items = json.optJSONArray("items")
            if (items != null && items.length() > 0) {
                // En son ayın verisini al
                val latest = items.getJSONObject(items.length() - 1)
                return@withContext latest.optLong("views", 0L)
            }
            0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Türkçe Wikipedia'dan sayfa özeti, biyografi özeti ve görsel çeker.
     * https://tr.wikipedia.org/api/rest_v1/page/summary/{title}
     */
    suspend fun getPageSummary(articleTitle: String): WikipediaPageSummary? = withContext(Dispatchers.IO) {
        try {
            val formattedTitle = articleTitle.trim().replace(" ", "_")
            val encodedTitle = URLEncoder.encode(formattedTitle, "UTF-8")
            val url = "https://tr.wikipedia.org/api/rest_v1/page/summary/$encodedTitle"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val bodyString = response.body?.string() ?: return@withContext null
            val json = JSONObject(bodyString)

            val title = json.optString("title", articleTitle)
            val extract = json.optString("extract", "")
            val description = json.optString("description", null)
            val thumbObj = json.optJSONObject("thumbnail")
            val thumbUrl = thumbObj?.optString("source", null)

            WikipediaPageSummary(
                title = title,
                extract = extract,
                description = description,
                thumbnailUrl = thumbUrl
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Türkçe Wikipedia'da arama yapar (Kişi keşfetme ve otomatik ekleme için).
     */
    suspend fun searchWikipedia(query: String): List<WikipediaSearchResult> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) return@withContext emptyList()
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://tr.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encoded&utf8=1&format=json&srlimit=8"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()

            val body = response.body?.string() ?: return@withContext emptyList()
            val json = JSONObject(body)
            val queryObj = json.optJSONObject("query") ?: return@withContext emptyList()
            val searchArr = queryObj.optJSONArray("search") ?: return@withContext emptyList()

            val results = mutableListOf<WikipediaSearchResult>()
            for (i in 0 until searchArr.length()) {
                val item = searchArr.getJSONObject(i)
                val title = item.optString("title", "")
                val snippetRaw = item.optString("snippet", "")
                val snippetClean = snippetRaw.replace(Regex("<[^>]*>"), "")
                if (title.isNotBlank()) {
                    results.add(WikipediaSearchResult(title, snippetClean))
                }
            }
            results
        } catch (e: Exception) {
            emptyList()
        }
    }
}
