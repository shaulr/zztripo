package com.tikalk.zztripo.zztripo.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "participant")
data class Participant(@PrimaryKey var id: Long?,
                       var name: String?,
                       var image: String?,
                       var location: Pair<Float?, Float?>?,
                       var battery: Float?)
{
    constructor() : this(-1L, "", "",null,100F)
}