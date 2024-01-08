package com.example.homespace.adapters.property

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.homespace.GetPropertiesQuery.Property
import com.example.homespace.R
import com.example.homespace.utils.getPriceFormat
import com.squareup.picasso.Picasso

class PropertyAdapter :
    PagingDataAdapter<Property, PropertyAdapter.ViewHolder>(PROPERTY_COMPARATOR) {

    inner class ViewHolder(
        view: View,
    ) : RecyclerView.ViewHolder(view) {
        private val img: ImageView
        private val tvPropType: TextView
        private val tvAddress: TextView
        private val tvIntent: TextView
        private val tvPrice: TextView
        private val card: LinearLayout
        init {
            img = view.findViewById(R.id.propertyPhoto)
            tvPropType = view.findViewById(R.id.tvPropertyType)
            tvAddress = view.findViewById(R.id.tvAddress)
            tvIntent = view.findViewById(R.id.tvIntent)
            tvPrice = view.findViewById(R.id.tvPrice)
            card = view.findViewById(R.id.propertyCard)
        }
        fun bind(item: Property, pos: Int) {
            card.setOnClickListener {
                Log.d("POSITION: ", pos.toString())
            }
            val priceFormat = getPriceFormat(item.country!!)
            tvPropType.text = item.propertyType
            tvAddress.text = item.address
            tvIntent.text = "For " + item.intent
            tvPrice.text = priceFormat.format(item.price)
            val photos = item.photos
            if (photos?.size!! > 0) {
                val photo = photos[0]?.photo!!
                Picasso.get().load(photo).into(img);
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.property_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(getItem(pos)!!, pos)
    }

    companion object {
        private val PROPERTY_COMPARATOR = PropertyComparator
    }
}