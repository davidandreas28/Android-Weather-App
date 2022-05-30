package com.example.weatherapp.core.utils

import java.time.LocalDateTime

class LocalDateTimeImpl : DateTime {
    override fun getDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}