package com.example.miniplayer

import android.os.Build
import android.os.Bundle
import android.view.Choreographer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.example.miniplayer.ui.theme.MiniPlayerTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()
    var isActivityStarted: Boolean = false
    var lastFrameTimeNanos:Long=0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.onMainActivityCreate(this)
        setContent {
            MiniPlayerTheme {
                ChatView(viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        isActivityStarted=true
        startFrameUpdates()
    }

    override fun onStop() {
        super.onStop()
        if(!isChangingConfigurations){
            viewModel.miniAudioPlayer.pause()
        }
        isActivityStarted=false;

    }

    override fun onDestroy() {
        super.onDestroy()
        if(!isChangingConfigurations){
            viewModel.onMainActivityReleased()
        }
    }

    fun startFrameUpdates(){
        Choreographer.getInstance().postFrameCallback ( object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                val deltaTime=if(lastFrameTimeNanos==0L){
                    0f
                }else{
                    (frameTimeNanos-lastFrameTimeNanos).div(1e9f)
                }
                lastFrameTimeNanos=frameTimeNanos
                updateAnimation(deltaTime)

                // Re-post the callback for the next frame
                if(isActivityStarted){
                    Choreographer.getInstance().postFrameCallback(this)
                }
            }
        })
    }

    fun updateAnimation(deltaTime: Float){
        viewModel.miniAudioPlayer.updateTime(deltaTime*5)
    }
}


@Preview
@Composable
private fun ChatPreview(){
    MiniPlayerTheme {
        ChatView(viewModel())
    }
}