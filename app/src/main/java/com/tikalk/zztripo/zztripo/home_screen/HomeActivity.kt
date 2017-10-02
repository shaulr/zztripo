package com.tikalk.zztripo.zztripo.home_screen

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.tikalk.zztripo.zztripo.R
import com.tikalk.zztripo.zztripo.entities.Participant
import com.tikalk.zztripo.zztripo.sources.db.AppDatabase
import com.tikalk.zztripo.zztripo.sources.db.dao.ParticipantDao
import com.tikalk.zztripo.zztripo.sources.participant.ParticipantsLocalDataSource
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnJoiner.setOnClickListener({

        })

        btnLeader.setOnClickListener({})
    }

    //TODO remove in release: example for adding Participant
    fun addParticipant(participant: Participant){
        val participant = Participant(1,"shaul","/1.png",Pair<Float?,Float?>(1f,2f),100f)
        ParticipantsLocalDataSource.addParticipant(participant)
    }
}
