package com.example.homespace.adapters.property

import androidx.recyclerview.widget.DiffUtil
import com.example.homespace.GetPropertiesQuery.Property

object PropertyComparator : DiffUtil.ItemCallback<Property>() {
    override fun areItemsTheSame(oldItem: Property, newItem: Property): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean {
        return oldItem.id == newItem.id
    }
}