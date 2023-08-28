package com.example.homespace.ui.agencies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homespace.GetAgenciesQuery.*
import com.example.homespace.R
import com.example.homespace.adapters.AgencyAdapter
import com.example.homespace.databinding.FragmentAgenciesBinding
import com.example.homespace.utils.Network
import org.koin.androidx.viewmodel.ext.android.viewModel

class AgenciesFragment : Fragment() {

    private var _binding: FragmentAgenciesBinding? = null
    private val agenciesViewModel: AgenciesViewModel by viewModel()

    private lateinit var agencyList: RecyclerView
    private lateinit var agencyResultsView: View

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
        agencyList = root.findViewById(R.id.agencyList)
        agencyResultsView = root.findViewById(R.id.agencyResultsView)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonReTryGettingAgencies.setOnClickListener { getAgencies() }
        observeViewModel()
        getAgencies()
    }

    private fun observeViewModel() {
        with(agenciesViewModel) {
            agencies.observe(viewLifecycleOwner) {
                Log.d("AGENCIES", it.toString())
                if (it != null) {
                    showAgencies(it)
                } else showTryAgainLayout()
            }
            /*couldGetAgencies.observe(viewLifecycleOwner) {
                if (it == false) showTryAgainLayout()
                else toggleTryAgainLayout(View.GONE)
            }*/
        }
    }

    private fun getAgencies() {
        if (Network.isOnline(requireContext())) {
            showProgressBar()
            agenciesViewModel.getAgencies()
        } else {
            showTryAgainLayout()
        }
    }

    private fun showAgencies(agencies: List<Agency>) {
        agencyList.layoutManager = LinearLayoutManager(requireContext())
        agencyList.adapter = AgencyAdapter(agencies)
        toggleProgressBar(View.GONE)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}