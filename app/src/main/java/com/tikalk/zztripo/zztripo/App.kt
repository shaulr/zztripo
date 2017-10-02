package com.tikalk.zztripo.zztripo

import android.app.Application
import com.tikalk.zztripo.zztripo.sources.db.DatabaseCreator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DatabaseCreator.createDb(this)
    }
}