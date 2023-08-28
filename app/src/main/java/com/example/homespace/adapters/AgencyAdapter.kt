package com.example.homespace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homespace.GetAgenciesQuery
import com.example.homespace.R

class AgencyAdapter(private val agencies: List<GetAgenciesQuery.Agency>) :
    RecyclerView.Adapter<AgencyAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView
        val tvName: TextView
        val tvAbout: TextView
        init {
            img = view.findViewById(R.id.agencyBanner)
            tvName = view.findViewById(R.id.tvAgencyName)
            tvAbout = view.findViewById(R.id.tvAgencyAbout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgencyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.agency_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgencyAdapter.ViewHolder, pos: Int) {
        holder.tvName.text = agencies[pos].name
        holder.tvAbout.text = agencies[pos].about
    }

    override fun getItemCount(): Int {
        return agencies.size
    }
}