package com.example.homespace.adapters.agency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.homespace.GetAgenciesQuery.Agency
import com.example.homespace.R

class AgencyAdapter :
    PagingDataAdapter<Agency, AgencyAdapter.ViewHolder>(AGENCY_COMPARATOR) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img: ImageView
        private val tvName: TextView
        private val tvAbout: TextView
        init {
            img = view.findViewById(R.id.agencyBanner)
            tvName = view.findViewById(R.id.tvAgencyName)
            tvAbout = view.findViewById(R.id.tvAgencyAbout)
        }
        fun bind(item: Agency) {
            tvName.text = item.name
            tvAbout.text = item.about
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.agency_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos)!!)
    }

    companion object {
        private val AGENCY_COMPARATOR = AgencyComparator
    }
}