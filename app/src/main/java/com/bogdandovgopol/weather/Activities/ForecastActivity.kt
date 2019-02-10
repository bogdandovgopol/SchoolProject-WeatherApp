package com.bogdandovgopol.weather.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.bogdandovgopol.weather.Adapters.DailyForecastListAdapter
import com.bogdandovgopol.weather.Extensions.RecyclerViewVerticalSpacingDecoration
import com.bogdandovgopol.weather.Helpers.Constants
import com.bogdandovgopol.weather.Helpers.ForecastHelper
import com.bogdandovgopol.weather.Models.DarkSkyModels.Forecast
import com.bogdandovgopol.weather.R
import com.bogdandovgopol.weather.Services.DarkSky
import com.bogdandovgopol.weather.Services.GoogleMaps
import kotlinx.android.synthetic.main.activity_forecast.*
import kotlin.math.roundToInt

class ForecastActivity : AppCompatActivity() {

    private var forecast: Forecast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        //call helpers
        val googleMaps = GoogleMaps(this)

        //get passed data
        val latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
        val longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
        val locality = googleMaps.reverseGeocode(latitude, longitude)

        //get forecast
        getForecast(latitude, longitude)

        //recyclerview setup
        if (forecast != null) {
            //initialize recyclerview
            initRecyclerView()

            //update UI
            populateUIWithForecastData(locality)
        }


        /* BUTTONS */

        backToLocationListBtn.setOnClickListener {
            backToLocationListBtn()
        }


    }

    //initialize recyclerview
    private fun initRecyclerView() {
        val linearLayout = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dailyForecastView.layoutManager = linearLayout
        dailyForecastView.adapter = DailyForecastListAdapter(this, forecast!!)
        dailyForecastView.addItemDecoration(RecyclerViewVerticalSpacingDecoration(20))

    }

    private fun getForecast(latitude: Double, longitude: Double) {
        val darkSky = DarkSky()
        //get serialized forecast data

        val forecast = darkSky.getForecast(latitude, longitude)?.take()
        if (forecast != null) {
            this.forecast = forecast
        } else {
            this.forecast = null
        }
    }

    private fun populateUIWithForecastData(locality: String?) {

        val forecastHelper = ForecastHelper(this)

        //update UI
        forecastScrollView.background = forecastHelper.getBackground(forecast!!.currently.icon)
        localityNameTxt.text = locality
        localityTimeTxt.text = forecastHelper.timestampToDate(forecast!!.timezone)
        forecastSummaryTxt.text = forecast!!.currently.summary
        forecastIcon.setImageResource(forecastHelper.getIcon(forecast!!.currently.icon))
        forecastCurrentTemperatureTxt.text = "${forecast!!.currently.temperature.toInt()}°"
        forecastMaxTemperatureTxt.text = "${forecast!!.daily.data[0].temperatureHigh.toInt()}°"
        forecastMinTemperatureTxt.text = "${forecast!!.daily.data[0].temperatureLow.toInt()}°"

        //detail outles
        uvIndexTxt.text = forecast!!.currently.uvIndex.toString()
        chanceOfRainTtxt.text = "${(forecast!!.currently.precipProbability * 100).roundToInt()}%"
        windSpeedTxt.text = "${forecast!!.currently.windSpeed.toInt()} km/h"
        humidiyTxt.text = "${(forecast!!.currently.humidity * 100).roundToInt()}%"
        pressureTtxt.text = "${forecast!!.currently.pressure.toInt()} hPa"
        dewPointTxt.text = "${forecast!!.currently.dewPoint.toInt()}°"
    }

    private fun backToLocationListBtn() {
        //redirect to location list activity
        val intent = Intent(this, LocationListActivity::class.java)
        startActivity(intent)
    }


}
