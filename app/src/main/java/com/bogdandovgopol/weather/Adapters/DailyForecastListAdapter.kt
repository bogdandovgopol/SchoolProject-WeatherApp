package com.bogdandovgopol.weather.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bogdandovgopol.weather.Helpers.ForecastHelper
import com.bogdandovgopol.weather.Models.DarkSkyModels.Forecast
import com.bogdandovgopol.weather.R
import kotlinx.android.synthetic.main.column_daily_forecast_item.view.*
import kotlin.math.roundToInt


class DailyForecastListAdapter(val context: Context, val forecast: Forecast) :
    RecyclerView.Adapter<DailyForecasListViewHolder>() {

    //number of items expected to be inside recyclerview
    override fun getItemCount(): Int {
        return forecast.daily.data.size - 1 //-1 because api return index 0 as today, wee need only data starting from nextday(tomorrow)
    }

    //populate row with datta
    override fun onBindViewHolder(holder: DailyForecasListViewHolder, position: Int) {

        //check if forecast exists
        if (forecast.daily.data.isNotEmpty()) {
            //call helpers
            val forecastHelper = ForecastHelper(context)

            val forecastItem = forecast.daily.data[position + 1] // to prevent errors, because we -1 in getItemCount()

            //update row UI data
            holder.itemView.dayTxt.text = forecastHelper.timestampToShortDayOfWeek(forecast.timezone, forecastItem.time)
            holder.itemView.chanceOfRainTxt.text = "${(forecastItem.precipProbability * 100).roundToInt()}%"
            holder.itemView.maxDegreeTxt.text = "${forecastItem.temperatureHigh.toInt()}°"
            holder.itemView.minDegreeTxt.text = "${forecastItem.temperatureLow.toInt()}°"
            holder.itemView.weatherIconImageView.setImageResource(forecastHelper.getIcon(forecastItem.icon))


        }
    }

    //prepare custom row view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecasListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.column_daily_forecast_item, parent, false)
        return DailyForecasListViewHolder(view)
    }



}

//custom viewholder
class DailyForecasListViewHolder(view: View) : RecyclerView.ViewHolder(view)