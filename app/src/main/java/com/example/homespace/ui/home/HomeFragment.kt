package com.example.homespace.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homespace.GetPropertiesQuery
import com.example.homespace.GetPropertiesStartWithCountryQuery
import com.example.homespace.MainActivity
import com.example.homespace.adapters.property.PropertyAdapter
import com.example.homespace.adapters.property.PropertyFromCountryAdapter
import com.example.homespace.databinding.FragmentHomeBinding
import com.example.homespace.models.MyAddress
import com.example.homespace.services.LocationService
import com.example.homespace.ui.BaseFragment
import com.example.homespace.ui.RemotePresentationState
import com.example.homespace.ui.asRemotePresentationState
import com.example.homespace.utils.Network
import com.example.homespace.viewModels.MainViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val mainViewModel: MainViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val locPropertyViewModel: PropertyByLocationViewModel by viewModel()
    private val locationService = LocationService.instance
    private var searchString = ""
    private var isNewSearch = false;
    private var lastRetrieval = "";
    private lateinit var parentActivity: MainActivity
    private lateinit var searchBoxLayout: ConstraintLayout
    private var propertyList: RecyclerView? = null
    private val listStateId = "propertyList"
    private lateinit var searchResultsView: View
    private var adapter1 = PropertyAdapter()
    private var adapter2 = PropertyFromCountryAdapter()
    private var state: Parcelable? = null

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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // retainInstance
        searchBoxLayout = binding.searchBoxLayout
        searchResultsView = binding.searchResultsView.root//= root.findViewById(R.id.searchResultsView)
        adapter1.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        adapter2.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        propertyList = binding.searchResultsView.propertyList //root.findViewById(R.id.propertyList)
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            propertyList?.layoutManager = LinearLayoutManager(requireContext())
        } else {
            propertyList?.layoutManager = GridLayoutManager(requireContext(), 2)
        }
        if (state != null) {
            setRecyclerViewStateAndAdapter()
        }
    //else if (savedInstanceState != null) {
//            val recyclerState: Parcelable? = savedInstanceState.getParcelable(listStateId)
//            propertyList.layoutManager?.onRestoreInstanceState(recyclerState)
//            showProperties()
//        }
        binding.bindState(
            uiState = homeViewModel.state,
            pagingData = homeViewModel.searchedProperties,
            uiActions = homeViewModel.accept
        )
        binding.bindPulledState(
            uiState = locPropertyViewModel.state,
            pagingData = locPropertyViewModel.properties,
            uiActions = locPropertyViewModel.accept
        )
        // propertyList.layoutManager = LinearLayoutManager(requireContext())
        binding.buttonReTryGettingProperties.setOnClickListener {
            getPropertiesAtAddress(locPropertyViewModel.accept)
        }
        locationService.setFusedLocationClient(requireContext())
        // locationService.getDeviceLocation(requireContext())
        observeMainViewModel()
        parentActivity.locationIsEnabled()
        parentActivity.networkIsEnabled()
        checkLocationIsEnabled()
    }

    private fun observeMainViewModel() {
        with(mainViewModel) {
            address.observe(viewLifecycleOwner) {
                if (it != null) receiveDeviceAddress(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        state = propertyList?.layoutManager?.onSaveInstanceState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (propertyList != null) {
            outState.putParcelable(
                listStateId,
                propertyList?.layoutManager?.onSaveInstanceState()
            )
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    private fun setRecyclerViewStateAndAdapter() {
        if (lastRetrieval == "pulled") {
            propertyList?.adapter = adapter2
        } else if (lastRetrieval == "searched") {
            propertyList?.adapter = adapter1
        }
    }

    private fun checkLocationIsEnabled() {
        if (locationService.canGetLocation(requireContext())) {
            if (locationService.networkEnabled && locationService.locationEnabled) {
                toggleProgressBar(View.VISIBLE)
                mainViewModel.getLocation(requireContext())
                // locationService.getDeviceLocation(requireContext())
            } else {
                showSearchBoxLayout()
                // toggleSearchBoxLayout(View.VISIBLE)
            }
        } else {
            showSearchBoxLayout()
            // toggleSearchBoxLayout(View.VISIBLE)
            // toggleProgressBar(View.GONE)
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
        if (address.line != searchString) {
            Log.d("ADDRESS", address.toString())
            searchString = address.line
            getPropertiesAtAddress(locPropertyViewModel.accept)
        } else {
            showProperties()
        }
    }

    private fun getPropertiesAtAddress(onQueryChanged: (UiAction2) -> Unit) {
        if (Network.isOnline(requireContext())) {
            lastRetrieval = "pulled"
            propertyList?.adapter = adapter2
            onQueryChanged(UiAction2.Pull(query = searchString.trim()))
        } else {
            showTryAgainLayout()
        }
    }

    private fun getSearchedProperties(onQueryChanged: (UiAction.Search) -> Unit) {
        binding.homeSearchBar.clearFocus()
        binding.topSearchBar.clearFocus()
        if (Network.isOnline(requireContext())) {
            isNewSearch = true
            lastRetrieval = "searched"
            propertyList?.adapter = adapter1
            onQueryChanged(UiAction.Search(query = searchString.trim()))
        } else {
            showTryAgainLayout()
        }
    }

    // display the agencies
    private fun showProperties() {
        toggleSearchBoxLayout(View.GONE)
        toggleProgressBar(View.GONE)
        toggleTryAgainLayout(View.GONE)
        binding.topSearchBar.visibility = View.VISIBLE
        toggleRecyclerView(View.VISIBLE)
        Log.d("DISPLAY PROPERTIES", propertyList?.adapter.toString())
    }

    // show try again layout
    private fun showTryAgainLayout() {
        isNewSearch = false
        toggleRecyclerView(View.GONE)
        toggleProgressBar(View.GONE)
        toggleTryAgainLayout(View.VISIBLE)
    }

    // show progress bar layout
    private fun showProgressBarLayout() {
        toggleProgressBar(View.VISIBLE)
        toggleSearchBoxLayout(View.GONE)
        toggleRecyclerView(View.GONE)
        toggleTryAgainLayout(View.GONE)
    }

    private fun showSearchBoxLayout() {
        if (lastRetrieval == "") {
            toggleProgressBar(View.GONE)
            toggleTryAgainLayout(View.GONE)
            toggleRecyclerView(View.GONE)
            toggleSearchBoxLayout(View.VISIBLE)
        }
    }

    private fun FragmentHomeBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<GetPropertiesQuery.Property>>,
        uiActions: (UiAction) -> Unit
    ) {
        // propertyList.adapter = adapter1
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindSearchedList(
            adapter = adapter1,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentHomeBinding.bindPulledState(
        uiState: StateFlow<UiState2>,
        pagingData: Flow<PagingData<GetPropertiesStartWithCountryQuery.Property>>,
        uiActions: (UiAction2) -> Unit
    ) {
        // propertyList.adapter = adapter1
        bindPullSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindPulledList(
            adapter = adapter2,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentHomeBinding.bindPullSearch(
        uiState: StateFlow<UiState2>,
        onQueryChanged: (UiAction2.Pull) -> Unit
    ) {
        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect { topSearchBar.query }
        }
    }

    private fun FragmentHomeBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        homeSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchString = query!!
                binding.topSearchBar.setQuery(searchString.toString(), false);
                getSearchedProperties(onQueryChanged)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        topSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchString = query!!
                getSearchedProperties(onQueryChanged)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect { topSearchBar.query }
        }
    }

    private fun bindSearchedList(
        adapter: PropertyAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<GetPropertiesQuery.Property>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        propertyList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) propertyList?.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                Log.d("SEARCHED LOADING STATE", loadState.refresh.toString())

                // val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                val isLoading = loadState.refresh is LoadState.Loading && adapter.itemCount == 0
                // show empty list
                // if (isListEmpty) {}
                // show progress bar
                if (isLoading || isNewSearch) {
                    showProgressBarLayout()
                }
                // Only show the list if refresh succeeds, either from the the local db or the remote.
                if (loadState.source.refresh is LoadState.NotLoading ||
                    loadState.mediator?.refresh is LoadState.NotLoading) {
                    showProperties()
                    isNewSearch = false
                }
                // Show loading spinner during initial load or refresh.
                if (loadState.mediator?.refresh is LoadState.Loading) {
                    showProgressBarLayout()
                }
                // Show the retry state if initial load or refresh fails.
                if (loadState.mediator?.refresh
                            is LoadState.Error && adapter.itemCount == 0) {
                    showTryAgainLayout()
                }
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (errorState != null) {
                    showTryAgainLayout()
                }
            }
        }
    }

    private fun bindPulledList(
        adapter: PropertyFromCountryAdapter,
        uiState: StateFlow<UiState2>,
        pagingData: Flow<PagingData<GetPropertiesStartWithCountryQuery.Property>>,
        onScrollChanged: (UiAction2.Scroll) -> Unit
    ) {
        propertyList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction2.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) propertyList?.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                Log.d("PULLED LOADING STATE", loadState.refresh.toString())

                // val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                val isLoading = loadState.refresh is LoadState.Loading && adapter.itemCount == 0
                // show empty list
                if (isLoading) {
                    showSearchBoxLayout()
                }
                // Only show the list if refresh succeeds, either from the the local db or the remote.
                if (loadState.source.refresh is LoadState.NotLoading ||
                    loadState.mediator?.refresh is LoadState.NotLoading) {
                    showProperties()
                }
                // Show loading spinner during initial load or refresh.
                if (loadState.mediator?.refresh is LoadState.Loading) {
                    showProgressBarLayout()
                }
                // Show the retry state if initial load or refresh fails.
                if (loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0) {
                    Log.d("LOAD FAILED", loadState.toString())
                    showTryAgainLayout()
                }
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (errorState != null) {
                    showTryAgainLayout()
                }
            }
        }
    }
}