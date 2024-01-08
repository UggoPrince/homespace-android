package com.example.homespace.ui.agencies

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homespace.GetAgenciesQuery.*
import com.example.homespace.R
import com.example.homespace.adapters.agency.AgencyAdapter
import com.example.homespace.databinding.FragmentAgenciesBinding
import com.example.homespace.ui.RemotePresentationState
import com.example.homespace.ui.asRemotePresentationState
import com.example.homespace.utils.Network
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class
AgenciesFragment : Fragment() {
    private var _binding: FragmentAgenciesBinding? = null
    private val agenciesViewModel: AgenciesViewModel by viewModel()
    private lateinit var agencyList: RecyclerView
    private lateinit var agencyResultsView: View
    private var adapter = AgencyAdapter()
    private var state: Parcelable? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgenciesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        agencyResultsView = root.findViewById(R.id.agencyResultsView)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        agencyList = binding.agencyResultsView.agencyList //.findViewById(R.id.agencyList)
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            agencyList.layoutManager = LinearLayoutManager(requireContext())
        } else {
            agencyList.layoutManager = GridLayoutManager(requireContext(), 2)
        }
        bindState(
            uiState = agenciesViewModel.state,
            pagingData = agenciesViewModel.agencies,
            uiActions = agenciesViewModel.accept
        )
        binding.buttonReTryGettingAgencies.setOnClickListener {
            getAgencies(agenciesViewModel.accept) }
        if (state != null) {
            agencyList.adapter = adapter
        } else {
            getAgencies(agenciesViewModel.accept)
        }
    }

    override fun onPause() {
        super.onPause()
        state = agencyList.layoutManager?.onSaveInstanceState()
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    private fun bindState(
        uiState: StateFlow<UiStateForPulledAgencies>,
        pagingData: Flow<PagingData<Agency>>,
        uiActions: (UiActionForAgency) -> Unit
    ) {
        bindList(
            adapter = adapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun bindList(
        adapter: AgencyAdapter,
        uiState: StateFlow<UiStateForPulledAgencies>,
        pagingData: Flow<PagingData<Agency>>,
        onScrollChanged: (UiActionForAgency.Scroll) -> Unit
    ) {
        agencyList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(
                    UiActionForAgency.Scroll(currentQuery = ""))
            }
        })
        val notLoading = adapter.loadStateFlow.asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolled }
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
                if (shouldScroll) agencyList.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                Log.d("AGENCY LOADING STATE", loadState.refresh.toString())

                // val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                val isLoading =  loadState.refresh is LoadState.Loading && adapter.itemCount == 0
                // show progress bar
                if (isLoading) {
                    showProgressBar()
                }
                // Only show the list if refresh succeeds, either from the the local db or the remote.
                if (loadState.source.refresh is LoadState.NotLoading ||
                    loadState.mediator?.refresh is LoadState.NotLoading) {
                    showAgencies()
                }
                // Show loading spinner during initial load or refresh.
                if (loadState.mediator?.refresh is LoadState.Loading) {
                    Log.d("LOADING: ", loadState.toString())
                    showProgressBar()
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

    private fun getAgencies(onQueryRetrieved: (UiActionForAgency) -> Unit) {
        if (Network.isOnline(requireContext())) {
            agencyList.adapter = adapter
            onQueryRetrieved(UiActionForAgency.Pull(""))
        } else {
            showTryAgainLayout()
        }
    }

    private fun showAgencies() {
        toggleProgressBar(View.GONE)
        toggleTryAgainLayout(View.GONE)
        toggleRecyclerView(View.VISIBLE)
    }

    // show the try again layout
    private fun showTryAgainLayout() {
        toggleProgressBar(View.GONE)
        toggleRecyclerView(View.GONE)
        toggleTryAgainLayout(View.VISIBLE)
    }

    // display progress bar
    private fun showProgressBar() {
        toggleProgressBar(View.VISIBLE)
        toggleTryAgainLayout(View.GONE)
        toggleRecyclerView(View.GONE)
    }

    // hide or show progress bar
    private fun toggleProgressBar(visible: Int) { binding.agencyProgressBar.visibility = visible }

    // hide or show recyclerview
    private fun toggleRecyclerView(visible: Int) { agencyResultsView.visibility = visible }

    // hide or show try again layout
    private fun toggleTryAgainLayout(visible: Int) { binding.getAgenciesRetryLayout.visibility = visible}
}