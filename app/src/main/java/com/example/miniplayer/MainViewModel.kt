package com.example.miniplayer

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val chatUiState: ChatUiState= ChatUiState("Mini Player in Chat",listOf())
    var textInputValue by mutableStateOf("")
    val miniAudioPlayer: MiniAudioPlayer= MiniAudioPlayer()

    var myToast: Toast?=null

    var appContext : Context? = null

    init {
        postUserMessage("Hello mini embedded player")
        receiveReplyMessage("I'm an example mini player app")
        chatUiState.addMessage(Message(MessageType.MESSAGE_TYPE_MUSIC_PLAYER,"0.75"))
        receiveReplyMessage("another example with shrinking while keeping original design")
        chatUiState.addMessage(Message(MessageType.MESSAGE_TYPE_MUSIC_PLAYER,""))
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
    }

    fun receiveReplyMessage(content:String){
        chatUiState.addMessage(Message(MessageType.MESSAGE_TYPE_REPLY,content))
    }
}