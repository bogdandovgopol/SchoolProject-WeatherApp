package com.bogdandovgopol.weather.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bogdandovgopol.weather.Activities.ForecastActivity
import com.bogdandovgopol.weather.Alerts.AlertManager
import com.bogdandovgopol.weather.Helpers.Constants
import com.bogdandovgopol.weather.Helpers.ForecastHelper
import com.bogdandovgopol.weather.Models.LocationModel
import com.bogdandovgopol.weather.R
import com.bogdandovgopol.weather.Services.DarkSky
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.row_location_list.view.*


class LocationListAdapter(val context: Context, val realm: Realm, val locations: OrderedRealmCollection<LocationModel>) :
    RealmRecyclerViewAdapter<LocationModel, LocationListViewHolder>(locations, true) {

    val alertManager = AlertManager()

    //number of items expected to be inside recyclerview
    override fun getItemCount(): Int {
        return locations.size
    }

    //prepare custom row view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_location_list, parent, false)
        return LocationListViewHolder(view)
    }

    //populate row view with data
    override fun onBindViewHolder(holder: LocationListViewHolder, position: Int) {
        val location = getItem(position)!!


        //get forecast
        val darkSky = DarkSky()
        val forecast = darkSky.getForecast(location.latitude, location.longitude)?.take()

        //check if forecast exists
        if (forecast != null) {
            val forecastHelper = ForecastHelper(context)

            //update row UI data
            holder.view.locationNameTxtView.text = location.name
            holder.view.locationDegreeTextView.text = "${forecast.currently.temperature.toInt()}°"
            holder.view.background =
                    forecastHelper.getBackground(forecast.currently.icon) //setting gradient background based on weather icon

            holder.location = location
        }
    }


    fun removeItem(viewHolder: LocationListViewHolder) {

        //remove item from realm db based on row position(adapterPosition)
        val item = getItem(viewHolder.adapterPosition)
        realm.executeTransaction {
            if (item != null) {
                item.deleteFromRealm()

                //notify user
                Toast.makeText(context, "Location has been successfully deleted", Toast.LENGTH_LONG).show()
            } else {
                //notify user about an error
                alertManager.showToHide(
                    context,
                    "Alert",
                    "Location cannot be deleted because it does not exist or has already been deleted. Please try to restart the application.",
                    "Hide"
                )
            }
        }
        //refresh recyclerview
        notifyItemRemoved(viewHolder.adapterPosition)
    }

}

//custom viewholder
class LocationListViewHolder(val view: View, var location: LocationModel? = null) : RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener {
            val intent = Intent(view.context, ForecastActivity::class.java)

            intent.putExtra(Constants.LATITUDE, location?.latitude)
            intent.putExtra(Constants.LONGITUDE, location?.longitude)

            view.context.startActivity(intent)
        }
    }

}