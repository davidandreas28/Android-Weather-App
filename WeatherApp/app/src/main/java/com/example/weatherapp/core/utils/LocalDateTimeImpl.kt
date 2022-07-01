package com.example.weatherapp.core.utils

import java.time.LocalDateTime

object LocalDateTimeImpl : DateTime {
    override fun getDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}