package com.tikalk.zztripo.zztripo.home_screen

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.tikalk.zztripo.zztripo.R
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnJoiner.setOnClickListener({

        })

        btnLeader.setOnClickListener({})
    }
}
