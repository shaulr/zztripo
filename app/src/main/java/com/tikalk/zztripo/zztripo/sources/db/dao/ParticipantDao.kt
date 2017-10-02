package com.tikalk.zztripo.zztripo.sources.db.dao

import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.tikalk.zztripo.zztripo.entities.Participant
import io.reactivex.Flowable

import android.arch.persistence.room.Dao

@Dao
interface ParticipantDao {

    @Query("SELECT * FROM participant")
    fun loadAllParticipant(): Flowable<List<Participant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: MutableList<Participant>) : Unit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addParticipant(participant: Participant) : Unit

}