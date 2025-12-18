package com.example.miniplayer

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val chatUiState: ChatUiState= ChatUiState("Mini Player in Chat",listOf())
    var textInputValue by mutableStateOf("")
    val miniAudioPlayer: MiniAudioPlayer= MiniAudioPlayer()

    var myToast: Toast?=null

    var appContext : Context? = null

    var firstUserMessage:Boolean = true

    init {
        receiveReplyMessage("I'm an scaled mini player at 360dp, send any input to get a horizontal shrunk mini player.")
        chatUiState.addMessage(Message(MessageType.MESSAGE_TYPE_MUSIC_PLAYER,"0.75"))
    }

    fun showToast(text:String){
        myToast?.cancel()
        myToast=Toast.makeText(appContext, text, Toast.LENGTH_SHORT)
        myToast?.show()
    }

    fun onMainActivityCreate(context: Context){
        miniAudioPlayer.initialize(context)
        appContext=context
    }

    fun onMainActivityReleased(){
        miniAudioPlayer.release()
    }

    fun postUserMessage(content:String){
        chatUiState.addMessage(Message(MessageType.MESSAGE_TYPE_USER,content))
        val minScaleAt480px=1/appContext!!.resources.displayMetrics.density
        if(firstUserMessage){
            firstUserMessage=false
            receiveReplyMessage("an example only shrinking horizontally using 480dp, input any number between $minScaleAt480px and 1")
            chatUiState.addMessage(Message(MessageType.MESSAGE_TYPE_MUSIC_PLAYER,""))
            return
        }

        var scale=content.toFloatOrNull()?:0f
        if(scale<=minScaleAt480px||scale>1f){
            scale=1f;
        }
        receiveReplyMessage("an example mini player with scale $scale")
        chatUiState.addMessage(Message(MessageType.MESSAGE_TYPE_MUSIC_PLAYER,scale.toString()))
    }

    fun receiveReplyMessage(content:String){
        chatUiState.addMessage(Message(MessageType.MESSAGE_TYPE_REPLY,content))
    }
}