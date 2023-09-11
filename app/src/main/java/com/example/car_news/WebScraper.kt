package com.example.car_news

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.coroutines.CoroutineContext

class WebScraper : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private var scrapingCompleteListener: ((String) -> Unit)? = null

    fun setOnScrapingCompleteListener(listener: (String) -> Unit) {
        scrapingCompleteListener = listener
    }

    fun scrapeWebsite(url: String) {
        GlobalScope.launch(coroutineContext) {
            try {
                val scrapedData = fetchData(url)
                withContext(Dispatchers.Main) {
                    scrapingCompleteListener?.invoke(scrapedData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchData(url: String): String {
        return try {
            val doc: Document = Jsoup.connect(url).get()
            // Parse and process the HTML using Jsoup as needed
            val title = doc.title()
            title // Return the scraped data
        } catch (e: Exception) {
            "Error: Unable to scrape data"
        }
    }
}
