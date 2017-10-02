package com.tikalk.zztripo.zztripo.home_screen

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.tikalk.zztripo.zztripo.R
import com.tikalk.zztripo.zztripo.entities.Participant
import com.tikalk.zztripo.zztripo.sources.db.AppDatabase
import com.tikalk.zztripo.zztripo.sources.db.dao.ParticipantDao
import com.tikalk.zztripo.zztripo.sources.participant.ParticipantsLocalDataSource
import kotlinx.android.synthetic.main.activity_home.*
import android.widget.Toast
import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.*
import java.util.Arrays.deepToString
import android.content.pm.PackageManager
import android.util.Log
import com.tikalk.zztripo.zztripo.members_screen.MembersActivity


class HomeActivity : AppCompatActivity(), HomeScreenContract.View {

    val TAG = "HomeActivity"

    private lateinit var homePresenter: HomeScreenContract.Presenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        checkPermissions()

        btnJoiner.setOnClickListener({
            homePresenter.joinerButtonClicked()

        })

        btnLeader.setOnClickListener({
            homePresenter.leaderButtonClicked()
        })

        setPresenter(HomePresenter(this, this))
    }


    override fun setPresenter(presenter: HomeScreenContract.Presenter) {
        homePresenter = presenter
    }

    override fun openMembersActivity() {
        Log.i(TAG, "About to open Members Activity")
        val intent = Intent(this, MembersActivity::class.java)

        startActivity(intent)

    }

    override fun openWaitingActivity() {
        Log.i(TAG, "About to open Waiting Activity")
    }

    private fun checkPermissions() {

        val permissions = getUsagePermissions(this)
        //        getUsagePermissions(this);

        Log.i(TAG, "permissions: " + Arrays.deepToString(permissions))

        val rxPermissions = RxPermissions(this) // where this is an Activity instance
        rxPermissions
                .request(*permissions)
                .subscribe { granted ->
                    if (granted) {
                        doAfterPermissions()
                    } else {
                        Toast.makeText(this, "This app required this permission Therefore asking again...", Toast.LENGTH_SHORT).show()
                    }
                }
    }


    fun getUsagePermissions(context: Context): Array<String> {
        try {
            return context
                    .packageManager
                    .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                    .requestedPermissions
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("This should have never happened.", e) // I'd return null and Log.wtf(...) in production
        }

    }

    private fun doAfterPermissions() {

    }

    //TODO remove in release: example for adding Participant
    fun addParticipant(participant: Participant){
        val participant = Participant(1,"shaul","/1.png",Pair<Float?,Float?>(1f,2f),100f)
        ParticipantsLocalDataSource.addParticipant(participant)
    }
}
