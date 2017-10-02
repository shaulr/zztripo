package com.tikalk.zztripo.zztripo.members_screen

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tikalk.zztripo.zztripo.R
import com.tikalk.zztripo.zztripo.model.Member
import kotlinx.android.synthetic.main.memeber_row_item.view.*

/**
 * Created by motibartov on 02/10/2017.
 */

class RecyclerAdapter (val context: Context) : RecyclerView.Adapter<RecyclerAdapter.MembersViewHolder>(){

    companion object {
        val TAG = "RecyclerAdapter"

    }
    lateinit var data : List<Member>

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        holder.bindMember(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MembersViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.memeber_row_item, parent, false)
        return MembersViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    fun updateMembers(members: List<Member>){
        data = members
        notifyDataSetChanged()
        Log.i(TAG, "members adapter updated, data size: " + data.size)
    }

    class MembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindMember(member: Member){
            Log.i(TAG, "bindMember")
            itemView.tvRowItemName.text = member.name
            itemView.tvRowItemLocation.text = member.location.latitude.toString() + " , " + member.location.longitude.toString()
            itemView.tvBatteryRowItem.text = member.batteryLevel.toString() + " %"
        }
    }
}