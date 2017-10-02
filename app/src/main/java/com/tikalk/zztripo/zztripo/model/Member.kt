package com.tikalk.zztripo.zztripo.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng


data class Member(val name: String, var location: LatLng, val batteryLevel : Int, var avatar: Bitmap?)
