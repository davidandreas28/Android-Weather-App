package com.example.weatherapp.core.datasources.remote

sealed class NetworkResultWrapper<out T> {
    data class Success<out T>(val value: T) : NetworkResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null) :
        NetworkResultWrapper<Nothing>()

    object NetworkError : NetworkResultWrapper<Nothing>()
}