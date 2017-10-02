package com.tikalk.zztripo.zztripo.home_screen

import android.content.Context


class HomePresenter (val c: Context, val view: HomeScreenContract.View): HomeScreenContract.Presenter{




    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unSubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun leaderButtonClicked() {
        view.openMembersActivity()
    }

    override fun joinerButtonClicked() {
        view.openWaitingActivity()
    }

}
