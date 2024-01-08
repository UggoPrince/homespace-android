package com.example.homespace.models

import com.apollographql.apollo3.api.*

data class ApiResponse<T: Any>(
    var data: T? = null,
    var errors: List<Error>? = null,
    var hasErrors: Boolean = false,
    var statusCode: Int = 200,
    var errorType: String = "",
    var message: String = "",
    var exception: Exception? = null
)
