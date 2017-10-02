package com.tikalk.zztripo.zztripo.logics

/**
 * Created by shaulr on 02/10/2017.
 */
class MessageProvider {
    private object Holder { val INSTANCE = MessageProvider() }

    companion object {
        val instance: MessageProvider by lazy { Holder.INSTANCE }
    }

    fun getPingJson() : String {
        return "Ping!"
    }

    fun getPongJson() : String {
        return "Pong!"
    }
}