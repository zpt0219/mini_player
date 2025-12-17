package com.example.miniplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.core.net.toUri
import androidx.media3.common.Player
import kotlin.math.max
import kotlin.math.min

data class AudioData(
    val uri: Uri,
    val title:String,
    val subTitle:String,
    val imageResId: Int=0,
    var isLiked: Boolean=false,
)
class MiniAudioPlayer {
    private var player: ExoPlayer?=null

    private lateinit var audioList:List<AudioData>
    private var currentPlayingAudioIndex:Int=0

    var passedTime by mutableStateOf(0f)
    var streamingTime by mutableStateOf(0f)
    var musicTotalTime by mutableStateOf(1f)

    var isLooped by mutableStateOf(false)
    var isPaused by mutableStateOf(false)

    var title by mutableStateOf("example title")

    var subTitle by mutableStateOf("example subtitle")

    var imageResId by mutableStateOf(R.drawable.album_music1)

    var isLiked by  mutableStateOf(false)

    fun buildMusicFileUri(context:Context, musicResId:Int) : Uri{
        return "android.resource://${context.packageName}/$musicResId".toUri()
    }

    fun initialize(context: Context){
        if(player!=null){
            return
        }
        player = ExoPlayer.Builder(context).build()

        audioList=mutableListOf(
            AudioData(buildMusicFileUri(context,R.raw.mamomo_twelve_paintings_clear_light),
                "clear light, twelve paintings, mamomo music collections",
                "subtitle1, this is an example description for music 1",
                R.drawable.album_music1),
            AudioData(buildMusicFileUri(context,R.raw.mamomo_twelve_paintings_old_blue_town),
                "old blue town, twelve paintings",
                "short subTitle2",
                R.drawable.album_music2),
        )
        currentPlayingAudioIndex=0

        player?.addListener(playerListener)

        play()
    }

    val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    // Show a loading indicator
                }
                Player.STATE_READY -> {
                    musicTotalTime= player?.duration?.div(1000f) ?: 0f
                    updateTime(0f)
                }
                Player.STATE_ENDED -> {
                    // The media has finished playing
                    if(isLooped){
                        play()
                    }else{
                        playNext()
                    }
                }
                Player.STATE_IDLE -> {
                    // Player is idle
                }
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            // Handle play/pause events here
        }

        // You can override other methods as needed, such as onMediaItemTransition, etc.
    }

    fun getPlayProgress():Float{
        return max(0f, min(1f, passedTime/musicTotalTime))
    }

    fun getStreamingProgress(): Float{
        return max(0f, min(1f, streamingTime/musicTotalTime))
    }

    fun floatToMmSs(timeInSeconds: Float): String{
        val totalSeconds=timeInSeconds.toInt()
        return String.format("%d:%02d",totalSeconds/60,totalSeconds%60)
    }

    fun getPlayTime():String{
        return floatToMmSs(passedTime)
    }

    fun getTotalTime():String{
        return floatToMmSs(musicTotalTime)
    }

    fun togglePlayOrPause(){
        if(isPaused){
            resume()
        }else{
            pause()
        }
    }

    fun toggleIsLiked(){
        audioList[currentPlayingAudioIndex].isLiked=!audioList[currentPlayingAudioIndex].isLiked
        isLiked=audioList[currentPlayingAudioIndex].isLiked
    }

    fun pause(){
        player?.pause()
        isPaused=true
    }

    fun resume(){
        player?.play()
        isPaused=false
    }

    fun play(){
        if(audioList.isEmpty()){
            return
        }
        val audioData=audioList.get(currentPlayingAudioIndex)
        player?.setMediaItem(MediaItem.fromUri(audioData.uri))
        player?.prepare()
        player?.play()
        passedTime=0f
        streamingTime=0f
        isPaused=false
        title=audioData.title
        subTitle=audioData.subTitle
        imageResId=audioData.imageResId
        isLiked=audioData.isLiked
    }

    fun toggleLoop(){
        isLooped=!isLooped
    }

    fun playNext(){
        if(audioList.isEmpty()){
            return
        }

        currentPlayingAudioIndex+=1
        if(currentPlayingAudioIndex>=audioList.size){
            currentPlayingAudioIndex=0
        }
        play()
    }

    fun playPrevious(){
        if(audioList.isEmpty()){
            return
        }

        currentPlayingAudioIndex-=1
        if(currentPlayingAudioIndex<0){
            currentPlayingAudioIndex=audioList.size-1
        }
        play()
    }

    fun updateTime(streamingDelta: Float){
        passedTime=player?.currentPosition?.div(1000f)?:0f
        streamingTime= min(musicTotalTime,max(passedTime,streamingTime+streamingDelta))
    }

    fun seekTo(ratio: Float){
        player?.seekTo(Math.round(ratio*musicTotalTime*1000.0))
        updateTime(0f)
    }

    @OptIn(UnstableApi::class)
    fun release(){
        if(player==null){
            return
        }
        player?.run {
            playWhenReady =false
            release()
        }
        player=null
    }

}