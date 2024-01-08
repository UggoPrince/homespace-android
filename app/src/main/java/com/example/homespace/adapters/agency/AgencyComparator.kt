package com.example.homespace.adapters.agency

import androidx.recyclerview.widget.DiffUtil
import com.example.homespace.GetAgenciesQuery.Agency

object AgencyComparator : DiffUtil.ItemCallback<Agency>() {
    override fun areItemsTheSame(oldItem: Agency, newItem: Agency): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Agency, newItem: Agency): Boolean {
        return oldItem.id == newItem.id
    }
}