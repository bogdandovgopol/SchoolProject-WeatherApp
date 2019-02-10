package com.bogdandovgopol.weather.Models.DarkSkyModels

class Forecast(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currently: Currently,
    val daily: Daily
)