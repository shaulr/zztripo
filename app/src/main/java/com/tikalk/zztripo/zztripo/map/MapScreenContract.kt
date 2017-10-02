package com.tikalk.zztripo.zztripo.map

import android.location.Location
import com.tikalk.zztripo.zztripo.BasePresenter
import com.tikalk.zztripo.zztripo.BaseView


interface MapScreenContract {

    interface View : BaseView<Presenter> {
       fun updateMapLocation(location: Location)
    }

    interface Presenter : BasePresenter {
        fun findLastKnownLocation()
    }
}