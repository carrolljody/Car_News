package com.example.car_news.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.car_news.WebScraper
import com.example.car_news.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textDashboard
        val webScraper = WebScraper()
        val urlToScrape = "https://www.bilbasen.dk/brugt/bil?PriceFrom=10000&PriceTo=25000"
        webScraper.setOnScrapingCompleteListener { scrapedData ->
            textView.text = scrapedData
        }
        webScraper.scrapeWebsite(urlToScrape)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}