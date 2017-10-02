package com.tikalk.zztripo.zztripo.sources.db

import com.tikalk.zztripo.zztripo.entities.Participant



import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.tikalk.zztripo.zztripo.sources.db.dao.ParticipantDao


@Database(entities = arrayOf(Participant::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun participantDao(): ParticipantDao

    companion object {
        const val DATABASE_NAME = "basic-sample-db"
    }

}