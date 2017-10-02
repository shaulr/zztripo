package com.tikalk.zztripo.zztripo.sources.participant

import com.tikalk.zztripo.zztripo.entities.Participant
import com.tikalk.zztripo.zztripo.sources.db.DatabaseCreator
import io.reactivex.Single

object ParticipantsLocalDataSource : ParticipantsDataSource {

    val reposDao = DatabaseCreator.database.participantDao()

    override fun getParticipants(): Single<List<Participant>>
        = reposDao
                .loadAllParticipant()
                .firstOrError()
                .doOnSuccess { if (it.isEmpty()) throw Exception() }


    override fun saveParticipants(list: List<Participant>)
        =  reposDao.insertAll(list.toMutableList())

    override fun addParticipant( participant: Participant) =
            reposDao.addParticipant(participant)
}