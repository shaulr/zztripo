package com.tikalk.sensorsui.sensors.map

import android.location.Location
import android.os.Handler
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.annotations.Expose
import com.tikalk.zztripo.zztripo.R

import java.util.HashMap


class MapManager(internal var mMap: GoogleMap?) {

    companion object {

        val TAG = MapManager::class.java.simpleName

        val WORLD_ZOOM = 1
        val CONTINENT_ZOOM = 5
        val CITY_ZOOM = 10
        val STREET_ZOOM = 15
        val BLOCK_ZOOM = 17
        val BUILDINGS_ZOOM = 20
    }

    internal var mMapZoom = CITY_ZOOM.toFloat()

    internal var mNavMarker: Marker? = null
    var bearing: Float = 0.toFloat()
        internal set


    internal var markerMap: Map<String, Marker>
    internal var mHandler = Handler()

    init {
        markerMap = HashMap()
        initMap()
    }

    fun initMap() {
        mMap?.isBuildingsEnabled = true
        mMap?.uiSettings?.isZoomControlsEnabled = true

        mMap?.setOnCameraIdleListener {
            //This means that camera moved and now it's idle again..
            //                Log.i(TAG, "onCameraIdle: ");
            if (mMap?.cameraPosition?.zoom != mMapZoom) {
                //This means user changed the zoom manually by zoom controls or gesture, setting zoom to new zoom value
                Log.i(TAG, "onCameraMove: zoom changed")
                mMapZoom = mMap?.cameraPosition!!.zoom
                updateZoom(mMapZoom)
            }
        }
    }

    fun updateZoom(zoom: Float) {
        mMapZoom = zoom
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(mMapZoom))
        //    mMap.setMaxZoomPreference(zoom);
    }

    fun updateMapLocation(location: Location
    ) {

        mHandler.post {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), mMapZoom))

            val position = LatLng(location.latitude, location.longitude)
            bearing = if (location.hasBearing()) location.bearing else mMap?.cameraPosition!!.bearing

            mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(position, mMapZoom, 0f, bearing)))
            if (mNavMarker == null) {
                mNavMarker = mMap?.addMarker(MarkerOptions().position(position).title("You're Here"))
            } else {
                mNavMarker!!.setPosition(position)
                //                    mNavMarker.setRotation(bearing);
            }
            //                mNavMarker.showInfoWindow();
        }
    }

    fun addNewMarker(position: LatLng): Marker? {
        return mMap?.addMarker(MarkerOptions().position(position))
    }



}
