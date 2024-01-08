package com.example.homespace.utils

import countryCodes
import java.text.NumberFormat
import java.util.*

fun getPriceFormat(country: String): NumberFormat {
    val countryCode = countryCodes[country]
    // Create a new Locale
    val currency = Locale(Locale.getDefault().language, countryCode)
    // Create a formatter for currency
    return  NumberFormat.getCurrencyInstance(currency)
}