package com.example.weatherapp.core.datasources.remote

data class ErrorResponse(
    val error: NetworkErrorBody
)

data class NetworkErrorBody(
    val code: Int,
    val message: String
)