package com.example.miniplayer

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.min
import kotlin.math.max
import com.example.miniplayer.ui.theme.MiniPlayerTheme

@Composable
fun StreamingProgressBar(viewModel: MainViewModel,progress:Float,streamingProgress: Float, widthDp: Float,boxHeightDp:Float,currTime:String,totalTime:String,scale:Float =1f){
    val thumbRadius=6*scale
    var boxActualSize by remember { mutableStateOf(IntSize.Zero) }
    val actualWidthDp=if (boxActualSize.width>0) boxActualSize.width/ LocalDensity.current.density else widthDp
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .width((widthDp*scale).dp)
            .height((boxHeightDp*scale).dp)
            .onGloballyPositioned { coordinates -> boxActualSize = coordinates.size }
            .pointerInteropFilter{ motionEvent ->
                when(motionEvent.action){
                    MotionEvent.ACTION_DOWN->{
                        viewModel.miniAudioPlayer.seekTo(min(1f, max(0f,motionEvent.x/boxActualSize.width)))
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Handle touch movement
                        viewModel.miniAudioPlayer.seekTo(min(1f, max(0f,motionEvent.x/boxActualSize.width)))
                        false // Don't consume the event, allowing it to propagate further if needed
                    }
                    MotionEvent.ACTION_UP -> {
                        // Handle the touch release event
                        true
                    }
                    else -> false
                }
            }) {
        Surface(color= Color(1f,1f,1f,0.2f), modifier = Modifier
            .fillMaxWidth()
            .height((2.4*scale).dp)){}
        Surface(color=Color(1f,1f,1f,0.5f), modifier = Modifier
            .width((actualWidthDp * streamingProgress).dp)
            .height((2*scale).dp)){}
        Surface(color=Color(1f,1f,1f,1f), modifier = Modifier
            .width((actualWidthDp*progress).dp).height((3.17*scale).dp)){}
        Surface(color= Color.White, modifier = Modifier
            .offset(x = (actualWidthDp * progress - thumbRadius).dp)
            .size((thumbRadius * 2).dp), shape = RoundedCornerShape((thumbRadius*2).dp)){}
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxSize().padding(top = ((boxHeightDp/2+2)*scale+thumbRadius).dp)){
            Row (horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()){
                Text(currTime, style = MaterialTheme.typography.labelSmall,color=Color(1f,1f,1f,0.7f), fontSize = MaterialTheme.typography.labelSmall.fontSize*scale)
                Text(totalTime, style = MaterialTheme.typography.labelSmall,color=Color(1f,1f,1f,0.7f), fontSize = MaterialTheme.typography.labelSmall.fontSize*scale)
            }
        }
    }
}

@Preview
@Composable
fun PreviewStreamingProgressBar(){
    MiniPlayerTheme {
        StreamingProgressBar(viewModel(), 0.1f,0.9f,500f,50f,"abc","def",0.75f)
    }
}