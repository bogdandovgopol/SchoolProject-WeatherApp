package com.bogdandovgopol.weather.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.Toast
import com.bogdandovgopol.weather.Adapters.LocationListAdapter
import com.bogdandovgopol.weather.Adapters.LocationListViewHolder
import com.bogdandovgopol.weather.Helpers.Constants
import com.bogdandovgopol.weather.Helpers.ForecastHelper
import com.bogdandovgopol.weather.Helpers.StatusBarHelper
import com.bogdandovgopol.weather.Models.LocationModel
import com.bogdandovgopol.weather.R
import com.bogdandovgopol.weather.Services.DarkSky
import com.bogdandovgopol.weather.Services.GoogleMaps
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_location_list.*


class LocationListActivity : AppCompatActivity(), PermissionsListener, LocationEngineListener {


    private val context: Context = this
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#C34D4D"))
    private lateinit var deleteIcon: Drawable

    //used for mapbox -> getting current location
    private lateinit var permissionManager: PermissionsManager
    private lateinit var originLocation: Location
    private var locationEngine: LocationEngine? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)


        //change statusbar color
        val StatusBar = StatusBarHelper()
        StatusBar.makeTransparent(window)

        //initialize mapbox
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        enableLocation()

        //recyclerview -> realm version
        locationRecyclerView.layoutManager = LinearLayoutManager(this)

        //set delete icon
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete)!!


        //initialize realm db
        Realm.init(this)
        val config = RealmConfiguration.Builder().name(Constants.REALM_DB_NAME).build()
        val realm = Realm.getInstance(config)

        //read from realm db => gett all saved locations
        val locations = realm.where(LocationModel::class.java).findAll()

        locationRecyclerView.adapter =
            LocationListAdapter(context, realm, locations as OrderedRealmCollection<LocationModel>)

        //swipe implementation
        swipeToDelete(realm, locations)

        /* BUTTONS */
        addLocationBtn.setOnClickListener {
            addLocationBtn()
        }

        //click on current location forecast row
        currentLocationLayout.setOnClickListener {
            currentLocationLayoutClick()
        }

    }

    /// GETTING CURRENT LOCATION - START
    /// Documentation: https://docs.mapbox.com/android/maps/overview/

    fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine()
        } else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    @SuppressLint("MissingPermission")
    fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.addLocationEngineListener(this)
        locationEngine?.activate()

        val lastLocation = locationEngine?.lastLocation
        if (lastLocation != null) {
            originLocation = lastLocation
            updateCurrentLocationForecastRow(lastLocation)
        }
    }


    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //explain why location is needed
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocation()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            originLocation = location
            updateCurrentLocationForecastRow(location)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    /// GETTING CURRENT LOCATION - END


    private fun updateCurrentLocationForecastRow(location: Location) {
        //get forecast
        val darkSky = DarkSky()
        val forecast = darkSky.getForecast(location.latitude, location.longitude)?.take()

        //check if forecast exists
        if (forecast != null) {
            //call helpers
            val forecastHelper = ForecastHelper(context)
            val googleMaps = GoogleMaps(context)

            val locality = googleMaps.reverseGeocode(location.latitude, location.longitude)// example: Sydney

            //update row UI data

            //show current location row only if last location is received
            currentLocationLayout.visibility = ConstraintLayout.VISIBLE

            currentLocationNameTextView.text = locality
            currentLocationDegreeTextView.text = "${forecast.currently.temperature.toInt()}°"
            currentLocationLayout.background =
                forecastHelper.getBackground(forecast.currently.icon) //setting gradient background based on weather icon

        }

    }


    private fun addLocationBtn() {
        val intent = Intent(this, AddLocationActivity::class.java)
        startActivity(intent)
    }

    private fun currentLocationLayoutClick() {
        val intent = Intent(this, ForecastActivity::class.java)

        intent.putExtra(Constants.LATITUDE, originLocation.latitude)
        intent.putExtra(Constants.LONGITUDE, originLocation.longitude)

        startActivity(intent)
    }

    //implementing swipe to recyclerview with red background and icon for delete action
    private fun swipeToDelete(realm: Realm, locations: RealmResults<LocationModel>) {

        //swipe manager
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(holder: RecyclerView.ViewHolder, position: Int) {
                LocationListAdapter(context, realm, locations).removeItem((holder as LocationListViewHolder))
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                drawSwipeDeleteBackgroundAndIcon(dX, itemView, iconMargin, c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            private fun drawSwipeDeleteBackgroundAndIcon(
                dX: Float,
                itemView: View,
                iconMargin: Int,
                c: Canvas
            ) {
                //swipe left
                if (dX < 0) {
                    swipeBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                }
                swipeBackground.draw(c)
                deleteIcon.draw(c)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(locationRecyclerView)
    }

}
