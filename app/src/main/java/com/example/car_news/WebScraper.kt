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
    data class CarListing(val name: String, val price: String, val url: String)
    val carListings = mutableListOf<CarListing>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    var scrapingCompleteListener: ((List<CarListing>) -> Unit)? = null

    fun setOnScrapingCompleteListener(listener: (List<CarListing>)  -> Unit) {
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

    private suspend fun fetchData(url: String): List<CarListing> {
        val carListings = mutableListOf<CarListing>()

        try {
            val doc: Document = Jsoup.connect(url).get()

            // Extract data for each car listing
            val listingElements = doc.select("a.listing-heading")
            val priceElements = doc.select("div.col-xs-3.listing-price")

            for (i in listingElements.indices) {
                val carNameElement = listingElements[i]
                val carName = carNameElement.text()
                val carUrl = carNameElement.attr("href")

                val price = priceElements[i].text()
                val carListing = CarListing(carName, price, carUrl)
                carListings.add(carListing)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return carListings
    }
}
