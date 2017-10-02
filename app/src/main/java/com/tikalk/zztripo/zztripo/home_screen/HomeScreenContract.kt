package com.tikalk.zztripo.zztripo.home_screen

import com.tikalk.zztripo.zztripo.BasePresenter
import com.tikalk.zztripo.zztripo.BaseView



/**
 * Created by motibartov on 02/10/2017.
 */

interface HomeScreenContract {

    interface View : BaseView<Presenter> {
        fun openMembersActivity()
        fun openWaitingActivity()
    }

    interface Presenter : BasePresenter {
        fun leaderButtonClicked()
        fun joinerButtonClicked()
    }
}