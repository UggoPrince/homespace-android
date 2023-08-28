package com.example.homespace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Operation
import com.example.homespace.GetPropertiesStartWithCountryQuery.Property
import com.example.homespace.GetPropertiesQuery
import com.example.homespace.R
import com.squareup.picasso.Picasso
import countryCodes
import java.text.NumberFormat
import java.util.*

class PropertyAdapter<T: GetPropertiesQuery.Property>(
    private val properties: List<T>
    ) :
    RecyclerView.Adapter<PropertyAdapter<T>.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView
        val tvPropType: TextView
        val tvAddress: TextView
        val tvIntent: TextView
        val tvPrice: TextView
        init {
            img = view.findViewById(R.id.propertyPhoto)
            tvPropType = view.findViewById(R.id.tvPropertyType)
            tvAddress = view.findViewById(R.id.tvAddress)
            tvIntent = view.findViewById(R.id.tvIntent)
            tvPrice = view.findViewById(R.id.tvPrice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.property_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val countryCode = countryCodes[properties[pos].country]
        // Create a new Locale
        val currency = Locale(Locale.getDefault().language, countryCode)
        // Create a formatter for currency
        val priceFormat = NumberFormat.getCurrencyInstance(currency)

        holder.tvPropType.text = properties[pos].propertyType
        holder.tvAddress.text = properties[pos].address
        holder.tvIntent.text = "For " + properties[pos].intent
        holder.tvPrice.text = priceFormat.format(properties[pos].price)
        val photos = properties[pos].photos
        if (photos?.size!! > 0) {
            val photo = photos[0]?.photo!!
            Picasso.get().load(photo).into(holder.img);
        }
    }

    override fun getItemCount(): Int {
        return properties.size
    }
}