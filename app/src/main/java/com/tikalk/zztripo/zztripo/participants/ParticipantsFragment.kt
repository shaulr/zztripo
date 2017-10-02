package com.tikalk.zztripo.zztripo.participants

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tikalk.zztripo.zztripo.R
import com.tikalk.zztripo.zztripo.model.Member
import kotlinx.android.synthetic.main.fragment_prticipants.*

/**
 * Created by motibartov on 02/10/2017.
 */

class ParticipantsFragment : Fragment(), ParticipantsContract.View{

    lateinit var participantsPresenter : ParticipantsContract.Presenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_prticipants, container, false)
        setPresenter(ParticipantsPresenter(activity, this))
        return rootView
    }

    override fun onResume() {
        super.onResume()
        participantsPresenter.loadMembers()
    }

    override fun setPresenter(presenter: ParticipantsContract.Presenter) {
        participantsPresenter = presenter
    }

    override fun openGoingTripScreen() {
        participantsPresenter.loadMembers()
    }

    override fun showMembers(members: List<Member>) {
        val adapter = RecyclerAdapter(activity)
        membersRecyclerView.layoutManager = LinearLayoutManager(activity)
        membersRecyclerView.adapter = adapter


        adapter.updateMembers(members)
    }




}