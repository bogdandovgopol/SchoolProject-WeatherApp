package com.bogdandovgopol.weather.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bogdandovgopol.weather.Alerts.AlertManager
import com.bogdandovgopol.weather.Helpers.Constants
import com.bogdandovgopol.weather.Models.LocationModel
import com.bogdandovgopol.weather.R
import com.bogdandovgopol.weather.Services.GoogleMaps
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_add_location.*

class AddLocationActivity : AppCompatActivity() {

    private val alertManager = AlertManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)


        backToLocationListBtn.setOnClickListener {
            backToLocationListBtn()
        }

        searchLocationBtn.setOnClickListener {
            searchLocation()
        }

    }

    private fun searchLocation() {

        val googleMaps = GoogleMaps(this)

        //get location(lat, long, locality) from passed address
        val googleLocation = googleMaps.geocode(locationNameTxt.text.toString())

        //check if something has been found
        if (googleLocation != null) {

            val lat: Double = googleLocation[0] as Double // example: -33.868819699999996
            val long: Double = googleLocation[1] as Double // example: 151.2092955
            val locality: String = googleLocation[2] as String // example: Sydney

            try {
                insertLocationIntoRealmDatabase(locality, lat, long)

            } catch (e: Exception) {
                //notify user about error(something with real db)
                alertManager.showToHide(
                    this,
                    "Error",
                    "We could not add this location. :(",
                    "Hide"
                )
            }

        } else {
            //notify user about an error (no addresses found)
            alertManager.showToHide(
                this,
                "Alert",
                "Location has not been found. Please check your location name.",
                "OK"
            )
        }


    }

    private fun backToLocationListBtn() {
        //redirect to location list activity
        val intent = Intent(this, LocationListActivity::class.java)
        startActivity(intent)
    }

    private fun insertLocationIntoRealmDatabase(locality: String, lat: Double, long: Double) {
        //initialize realm database
        Realm.init(this)
        val config = RealmConfiguration.Builder().name(Constants.REALM_DB_NAME).build()
        val realm = Realm.getInstance(config)

        //check if location already exist in database
        val locations = realm.where(LocationModel::class.java).contains("name", locality).findFirst()
        if (locations == null || locations.latitude != lat || locations.longitude != long) {
            //insert into realm db
            realm.executeTransaction { realm ->

                //create new row/index/etc...
                val location = realm.createObject(
                    LocationModel::class.java,
                    autoIncrement(realm.where(LocationModel::class.java).max("id"))
                )

                //set row/index data
                location.name = locality
                location.longitude = long
                location.latitude = lat

                //redirect to screen with list of all locations
                backToLocationListBtn()
            }
        } else {
            //notify user about error
            alertManager.showToHide(
                this,
                "Alert",
                "Location already exists in your database",
                "OK"
            )
        }
    }

    private fun autoIncrement(id: Number?): Int {
        return if (id == null) 0 else id.toInt() + 1
    }

}
