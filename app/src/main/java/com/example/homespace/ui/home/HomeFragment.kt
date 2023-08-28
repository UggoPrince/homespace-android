package com.example.homespace.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homespace.GetPropertiesQuery
import com.example.homespace.MainActivity
import com.example.homespace.R
import com.example.homespace.databinding.FragmentHomeBinding
import com.example.homespace.models.MyAddress
import com.example.homespace.services.LocationService
import com.example.homespace.ui.BaseFragment
import com.example.homespace.viewModels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.homespace.GetPropertiesStartWithCountryQuery
import com.example.homespace.adapters.PropertyAdapter
import com.example.homespace.utils.Network

class HomeFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentHomeBinding? = null
    private val mainViewModel: MainViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val locationService = LocationService.instance
    private var searchString = ""
    private lateinit var parentActivity: MainActivity
    private lateinit var homeSearchBar: SearchView
    private lateinit var topSearchBar: SearchView
    private lateinit var searchBoxLayout: ConstraintLayout
    private lateinit var propertyList: RecyclerView
    private lateinit var searchResultsView: View

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = activity as MainActivity
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        propertyList = root.findViewById(R.id.propertyList)
        searchResultsView = root.findViewById(R.id.searchResultsView)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeSearchBar = binding.homeSearchBar
        topSearchBar = binding.topSearchBar
        searchBoxLayout = binding.searchBoxLayout
        propertyList.layoutManager = LinearLayoutManager(requireContext())
        binding.buttonReTryGettingProperties.setOnClickListener {
            getPropertiesAtAddress()
        }
        binding.homeSearchBar.setOnQueryTextListener(this)
        binding.topSearchBar.setOnQueryTextListener(this)
        locationService.setFusedLocationClient(requireContext())
        // locationService.getDeviceLocation(requireContext())
        observeMainViewModel()
        observeHomeViewModel()
        parentActivity.locationIsEnabled()
        parentActivity.networkIsEnabled()
        checkLocationIsEnabled()
    }

    private fun observeMainViewModel() {
        with(mainViewModel) {
            address.observe(viewLifecycleOwner) {
                receiveDeviceAddress(it)
            }
        }
    }

    private fun observeHomeViewModel() {
        with(homeViewModel) {
            properties.observe(viewLifecycleOwner) {
                Log.d("PROPERTIES", it.toString())
                showHomeProperties(it)
            }
            searchedProperties.observe(viewLifecycleOwner) {
                Log.d("PROPERTIES", it.toString())
                showSearchedProperties(it)
            }
            couldGetProperties.observe(viewLifecycleOwner) {
                if (!it) showTryAgainLayout()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkLocationIsEnabled() {
        if (locationService.canGetLocation(requireContext())) {
            if (locationService.networkEnabled && locationService.locationEnabled) {
                toggleProgressBar(View.VISIBLE)
                mainViewModel.getLocation(requireContext())
                // locationService.getDeviceLocation(requireContext())
            } else {
                toggleSearchBoxLayout(View.VISIBLE)
            }
        } else {
            toggleSearchBoxLayout(View.VISIBLE)
            toggleProgressBar(View.GONE)
        }
    }

    // hide or show search view layout
    private fun toggleSearchBoxLayout(visible: Int) { binding.searchBoxLayout.visibility = visible }

    // hide or show progress bar
    private fun toggleProgressBar(visible: Int) { binding.progressBar.visibility = visible }

    // hide or shaw try again layout
    private fun toggleTryAgainLayout(visible: Int) {
        binding.tryAgainLayout.visibility =  visible
    }

    // hide or show recyclerview
    private fun toggleRecyclerView(visible: Int) {searchResultsView.visibility = visible}

    private fun receiveDeviceAddress(address: MyAddress) {
        // home_search_bar.setQuery(address.line, false)
        // Toast.makeText(requireContext(), address.toString(), Toast.LENGTH_LONG).show()
        Log.d("ADDRESS", address.toString())
        searchString = address.line
        getPropertiesAtAddress()
    }

    private fun getPropertiesAtAddress() {
        homeViewModel.getHomeProperties(searchString)
    }

    private fun getSearchedProperties() {
        homeViewModel.searchedProperties(searchString)
    }

    private fun showHomeProperties(list: List<GetPropertiesStartWithCountryQuery.Property>) {
        propertyList.layoutManager = LinearLayoutManager(requireContext())
        propertyList.adapter = PropertyAdapter(list)
        showProperties()
    }

    private fun showSearchedProperties(list: List<GetPropertiesQuery.Property>) {
        // val l = list as List<GetPropertiesStartWithCountryQuery.Property>
        propertyList.layoutManager = LinearLayoutManager(requireContext())
        propertyList.adapter = PropertyAdapter(list)
        showProperties()
    }

    // display the agencies
    private fun showProperties(/*list: List<Any>*/) {
        /*propertyList.layoutManager = LinearLayoutManager(requireContext())
        propertyList.adapter = PropertyAdapter(list)*/
        toggleSearchBoxLayout(View.GONE)
        toggleProgressBar(View.GONE)
        toggleTryAgainLayout(View.GONE)
        topSearchBar.visibility = View.VISIBLE
        toggleRecyclerView(View.VISIBLE)
    }

    // show try again layout
    private fun showTryAgainLayout() {
        toggleRecyclerView(View.GONE)
        toggleProgressBar(View.GONE)
        toggleTryAgainLayout(View.VISIBLE)
    }

    // show progress bar layout
    private fun showProgressBarLayout() {
        toggleProgressBar(View.VISIBLE)
        toggleSearchBoxLayout(View.GONE)
        toggleTryAgainLayout(View.GONE)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchString = query!!
        homeSearchBar.clearFocus()
        topSearchBar.clearFocus()
        if (Network.isOnline(requireContext())) {
            showProgressBarLayout()
            getPropertiesAtAddress()
        } else {
            showTryAgainLayout()
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        //
        return true
    }
}