package com.tikalk.zztripo.zztripo.participants

import com.tikalk.zztripo.zztripo.BasePresenter
import com.tikalk.zztripo.zztripo.BaseView
import com.tikalk.zztripo.zztripo.model.Member

interface ParticipantsContract {

    interface View : BaseView<Presenter> {
        fun openGoingTripScreen()
        fun showMembers(members: List<Member>)
    }

    interface Presenter : BasePresenter {
        fun startTripClicked()
        fun loadMembers()
    }
}