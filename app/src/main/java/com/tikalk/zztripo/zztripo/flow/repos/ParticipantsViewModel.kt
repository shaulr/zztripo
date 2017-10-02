package com.tikalk.zztripo.zztripo.flow.repos

import com.tikalk.zztripo.zztripo.entities.Participant
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData


open class ParticipantsViewModel(application: Application?) : AndroidViewModel(application) {

    private val participantsLiveData: MutableLiveData<Participant> = MutableLiveData<Participant>()

}