package com.tikalk.zztripo.zztripo.map

import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.tikalk.sensorsui.sensors.map.MapManager
import com.tikalk.zztripo.zztripo.R
import android.app.Activity
import kotlinx.android.synthetic.main.tab_map_fragment.*


class MapViewFragment : Fragment() , MapScreenContract.View{


    private var googleMap: GoogleMap? = null
    lateinit var mapManager : MapManager
    lateinit var  ongoingTripPresenter : MapScreenContract.Presenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.tab_map_fragment, container, false)

        setPresenter(MapScreenPresenter(activity, this))

        return rootView

    }

    private fun initializeMapView(activity: Activity, savedInstanceState: Bundle?) {
        MapsInitializer.initialize(activity)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mMap ->
            googleMap = mMap

            // For showing a move to my location button
            googleMap!!.isMyLocationEnabled = true

            // For dropping a marker at a point on the Map
            val sydney = LatLng(-34.0, 151.0)
            googleMap!!.addMarker(MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"))

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mapManager = MapManager(googleMap)
            ongoingTripPresenter.findLastKnownLocation()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeMapView(activity, savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun setPresenter(presenter: MapScreenContract.Presenter) {
        ongoingTripPresenter = presenter
    }

    override fun updateMapLocation(location: Location) {
        mapManager.updateMapLocation(location)
    }
}
