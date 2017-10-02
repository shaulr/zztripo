package com.tikalk.zztripo.zztripo.flow.repos

import android.arch.lifecycle.MediatorLiveData
import com.tikalk.zztripo.zztripo.entities.Participant
import io.reactivex.disposables.Disposable


class ParticipantsLiveData : MediatorLiveData<Pair<List<Participant>?, Throwable?>>() {

    private var disposable: Disposable? = null

    override fun onInactive() {
        super.onInactive()
        if (disposable?.isDisposed?.not() ?: false) {
            disposable?.dispose()
        }
    }

}
