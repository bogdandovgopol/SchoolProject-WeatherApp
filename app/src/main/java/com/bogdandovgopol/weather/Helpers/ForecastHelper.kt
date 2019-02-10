package com.bogdandovgopol.weather.Helpers

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.bogdandovgopol.weather.R
import java.lang.Exception
import java.util.*
import java.text.SimpleDateFormat


class ForecastHelper(val context: Context) {


    fun getBackground(icon: String): Drawable {
        when (icon) {
            "clear-day" -> {
                return ContextCompat.getDrawable(context, R.drawable.clear_day_bg)!!
            }
            "clear-night" -> {
                return ContextCompat.getDrawable(context, R.drawable.clear_night_bg)!!
            }
            "rain" -> {
                return ContextCompat.getDrawable(context, R.drawable.rain_bg)!!
            }
            "snow" -> {
                return ContextCompat.getDrawable(context, R.drawable.snow_bg)!!
            }
            "sleet" -> {
                return ContextCompat.getDrawable(context, R.drawable.sleet_bg)!!
            }
            "wind" -> {
                return ContextCompat.getDrawable(context, R.drawable.wind_bg)!!
            }
            "fog" -> {
                return ContextCompat.getDrawable(context, R.drawable.fog_bg)!!
            }
            "cloudy" -> {
                return ContextCompat.getDrawable(context, R.drawable.cloudy_bg)!!
            }
            "partly-cloudy-day" -> {
                return ContextCompat.getDrawable(context, R.drawable.partly_cloudy_day_bg)!!
            }
            "partly-cloudy-night" -> {
                return ContextCompat.getDrawable(context, R.drawable.partly_cloudy_night_bg)!!
            }
            "hail" -> {
                return ContextCompat.getDrawable(context, R.drawable.hail_bg)!!
            }
            "thunderstorm" -> {
                return ContextCompat.getDrawable(context, R.drawable.thunderstorm_bg)!!
            }
            "tornado" -> {
                return ContextCompat.getDrawable(context, R.drawable.tornado_bg)!!
            }
            else -> {
                return ContextCompat.getDrawable(context, R.drawable.clear_day_bg)!!
            }
        }
    }

    //get right icon drawable based on icon text from text
    fun getIcon(icon: String): Int {
        when (icon) {
            "clear-day" -> {
                return R.drawable.clear_day_icon
            }
            "clear-night" -> {
                return R.drawable.clear_night_icon
            }
            "rain" -> {
                return R.drawable.rain_icon
            }
            "snow" -> {
                return R.drawable.snow_icon
            }
            "sleet" -> {
                return R.drawable.sleet_icon
            }
            "wind" -> {
                return R.drawable.wind_icon
            }
            "fog" -> {
                return R.drawable.fog_icon
            }
            "cloudy" -> {
                return R.drawable.cloudy_icon
            }
            "partly-cloudy-day" -> {
                return R.drawable.partly_cloudy_day_icon
            }
            "partly-cloudy-night" -> {
                return R.drawable.partly_cloudy_night_icon
            }
            "hail" -> {
                return R.drawable.hail_icon
            }
            "thunderstorm" -> {
                return R.drawable.thunderstorm_icon
            }
            "tornado" -> {
                return R.drawable.tornado_icon
            }
            else -> {
                return R.drawable.clear_day_icon
            }
        }
    }

    //get formatted datetime from timemezone Example: Monday, 1:30 PM
    fun timestampToDate(timezone: String): String? {

        try {
            val format = SimpleDateFormat("EEEE, hh:mm a")
            format.timeZone = TimeZone.getTimeZone(timezone)

            return format.format(Date())
        } catch (e: Exception) {
            return null
        }
    }

    //get short name of a day from timezone. Example: Fri
    fun timestampToShortDayOfWeek(timezone: String, timestamp: Long): String? {

        try {
            val format = SimpleDateFormat("EEE")
            format.timeZone = TimeZone.getTimeZone(timezone)

            return format.format(Date(timestamp * 1000))
        } catch (e: Exception) {
            return null
        }
    }

}