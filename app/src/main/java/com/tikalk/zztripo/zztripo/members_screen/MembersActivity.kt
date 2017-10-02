package com.tikalk.zztripo.zztripo.members_screen

import android.arch.lifecycle.LifecycleActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.tikalk.zztripo.zztripo.R
import com.tikalk.zztripo.zztripo.flow.repos.ParticipantsViewModel
import com.tikalk.zztripo.zztripo.model.Member
import kotlinx.android.synthetic.main.activity_members.*

class MembersActivity : LifecycleActivity(), MembersScreenContract.View {


    val TAG = "MembersActivity"
    lateinit var membersPresenter : MembersScreenContract.Presenter

    val viewModelClass = ParticipantsViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        setPresenter(MembersPresenter(this, this))


        btnStartTrip.setOnClickListener({

            membersPresenter.startTripClicked()
        })



    }

    override fun onResume() {
        super.onResume()
        membersPresenter.loadMembers()
    }

    override fun setPresenter(presenter: MembersScreenContract.Presenter) {
        this.membersPresenter = presenter
    }

    override fun openGoingTripScreen() {

        Log.i(TAG, "Starting trip, opening Going Trip screen")
    }

    override fun showMembers(members: List<Member>) {
        val adapter = RecyclerAdapter(this)
        membersRecyclerView.layoutManager = LinearLayoutManager(this)
        membersRecyclerView.adapter = adapter


        adapter.updateMembers(members)
    }
}
