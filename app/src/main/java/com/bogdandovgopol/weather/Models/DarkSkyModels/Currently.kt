package com.bogdandovgopol.weather.Models.DarkSkyModels

class Currently(
    val time: Long,
    val summary: String,
    val icon: String,
    val precipIntensity: Double,
    val precipProbability: Double,
    val precipType: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val dewPoint: Double,
    val humidity: Double,
    val pressure: Double,
    val windSpeed: Double,
    val windGust: Double,
    val windBearing: Int,
    val cloudCover: Double,
    val uvIndex: Int,
    val visibility: Double,
    val ozone: Double
)