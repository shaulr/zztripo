package com.tikalk.zztripo.zztripo.map

import android.content.Context
import android.location.Location
import android.location.LocationManager



class MapScreenPresenter(val context: Context, val view : MapScreenContract.View) : MapScreenContract.Presenter{

    override fun subscribe() {

    }

    override fun unSubscribe() {
    }

    override fun findLastKnownLocation() {
        view.updateMapLocation(getLastKnownLocation())
    }

    private fun getLastKnownLocation(): Location {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    }
}
