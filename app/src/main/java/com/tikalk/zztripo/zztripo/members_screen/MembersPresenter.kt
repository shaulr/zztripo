package com.tikalk.zztripo.zztripo.members_screen

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.tikalk.zztripo.zztripo.model.Member

class MembersPresenter (val context: Context, val view : MembersScreenContract.View): MembersScreenContract.Presenter{


    override fun subscribe() {
    }

    override fun unSubscribe() {
    }

    override fun startTripClicked() {
        view.openGoingTripScreen()
    }

    override fun loadMembers(){
        view.showMembers(createMockMembers())
    }

    private fun createMockMembers(): List<Member> {
        val moti =  Member("Moti", LatLng(31.8, 34.2), 85, null )
        val tamir =  Member("Tamir", LatLng(31.8, 34.2), 72, null )
        val shaul =  Member("Shaul", LatLng(31.8, 34.2), 71, null )

        return listOf(moti, tamir, shaul)
    }


}
