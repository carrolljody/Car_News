package com.example.car_news.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.car_news.WebScraper
import com.example.car_news.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel

    class SpannableStringAdapter(
        context: Context,
        resource: Int,
        objects: List<SpannableString>
    ) : ArrayAdapter<SpannableString>(context, resource, objects) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent) as TextView
            view.movementMethod = LinkMovementMethod.getInstance()
            return view
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val webScraper = WebScraper()
        val urlToScrape =
            "https://www.bilbasen.dk/brugt/bil?IncludeEngrosCVR=false&PriceFrom=10000&PriceTo=25000&includeLeasing=false&free=hyundai&IncludeCallForPrice=false&ServiceOK=1&ZipCode=0&IncludeSellForCustomer=false"
        val listView = binding.listView

        val carListAdapter = SpannableStringAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf()
        )

        listView.adapter = carListAdapter

        webScraper.setOnScrapingCompleteListener { carListings ->
            val formattedList = carListings.map { carListing ->
                val clickableText = SpannableString("${carListing.name} - ${carListing.price}")
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val baseUrl = "https://www.bilbasen.dk"
                        val completeUrl = baseUrl.plus(carListing.url)
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(completeUrl)
                        intent.`package` = "com.android.chrome"
                        startActivity(intent)

                        val packageManager = requireActivity().packageManager
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl)))
                        }
                    }
                }


                val carNameStart = 0
                val carNameEnd = carListing.name.length
                clickableText.setSpan(
                    clickableSpan,
                    carNameStart,
                    carNameEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                clickableText
            }

            requireActivity().runOnUiThread {
                carListAdapter.clear()
                carListAdapter.addAll(formattedList)
                carListAdapter.notifyDataSetChanged()
            }
        }

        webScraper.scrapeWebsite(urlToScrape)
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}