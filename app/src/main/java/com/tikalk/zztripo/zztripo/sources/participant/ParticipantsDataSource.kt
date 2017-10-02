package com.tikalk.zztripo.zztripo.sources.participant

import com.tikalk.zztripo.zztripo.entities.Participant
import io.reactivex.Single

interface ParticipantsDataSource {

    fun getParticipants(): Single<List<Participant>>

    fun saveParticipants(list: List<Participant>) : Unit = Unit

    fun addParticipant(participant: Participant) : Unit = Unit

}