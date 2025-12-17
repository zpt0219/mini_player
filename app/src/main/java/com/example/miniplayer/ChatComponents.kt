package com.example.miniplayer

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.util.Log
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miniplayer.ui.theme.MiniPlayerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(viewModel: MainViewModel, modifier: Modifier= Modifier){
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scrollState = rememberLazyListState()
    Scaffold (
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar (
                title = {
                    Row(horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()){
                            Text(viewModel.chatUiState.title) }
                        },
                windowInsets = WindowInsets.statusBars,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                expandedHeight = 40.dp)
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ){ paddingValues ->
        Column(modifier=Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Messages(viewModel.chatUiState.messages, scrollState = scrollState,modifier= Modifier.weight(1f), viewModel = viewModel)
            Row (modifier= Modifier.fillMaxWidth()) {
                TextField(value = viewModel.textInputValue,
                    onValueChange = { viewModel.textInputValue=it },
                    modifier= Modifier.weight(1f))
                Button(onClick = {
                    viewModel.postUserMessage(viewModel.textInputValue)
                    viewModel.textInputValue=""
                }) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun Messages(messages: List<Message>, scrollState: LazyListState,
             modifier: Modifier= Modifier, viewModel: MainViewModel){
    LazyColumn(
        reverseLayout = true,
        state = scrollState,
        modifier = modifier.fillMaxSize(),
    ){
        for (index in messages.indices.reversed()){
            val msg=messages[index]
            item {
                Message(
                    viewModel = viewModel,
                    msg = msg,
                )
            }
        }
    }
}

val leftRoundedCornerShape=RoundedCornerShape(
    topStart = 4.dp,
    topEnd = 20.dp,
    bottomStart = 20.dp,
    bottomEnd = 20.dp)

val rightRoundedCornerShape= RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 4.dp,
    bottomStart = 20.dp,
    bottomEnd = 20.dp
)

@Composable
fun Message(msg: Message,viewModel: MainViewModel){
    when(msg.type){
        MessageType.MESSAGE_TYPE_USER -> MessageForUserType(msg)
        MessageType.MESSAGE_TYPE_REPLY -> MessageForReplyType(msg)
        MessageType.MESSAGE_TYPE_MUSIC_PLAYER -> Box(modifier = Modifier.padding(horizontal = 8.dp)) {MessageForMusicPlayerType(msg,viewModel,msg.content.toFloatOrNull()?:1f)}
    }
}

@Composable
fun MessageForReplyType(msg: Message){
    Row (
        modifier = Modifier
            .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start){
        Surface(
            color=MaterialTheme.colorScheme.surfaceVariant,
            shape= leftRoundedCornerShape,
        ){
            Text(msg.content,modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
fun MessageForUserType(msg: Message){
    Row (
        modifier = Modifier
            .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End){
        Surface(
            color=MaterialTheme.colorScheme.primary,
            shape= rightRoundedCornerShape,
        ){
            Text(msg.content,modifier = Modifier.padding(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class,ExperimentalFoundationApi::class)
@Composable
fun MessageForMusicPlayerType(msg: Message,viewModel: MainViewModel,scale: Float=1f){
    val density=LocalDensity.current
    Log.i("#####", "screen width in dp: " + LocalConfiguration.current.screenWidthDp)
    Row(
        modifier = Modifier
            .padding(top = 4.dp, bottom = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center){
        Surface(
            color= MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape((16*scale).dp),
            modifier = Modifier
                .width((480*scale).dp)
                .height((297*scale).dp)
        ) {
            Column(modifier = Modifier
                .padding((32*scale).dp)
                .fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(viewModel.miniAudioPlayer.imageResId),
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        modifier = Modifier.size((88*scale).dp)
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = (16*scale).dp)
                            .height((66*scale).dp), verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((32*scale).dp)
                        ) {
                            Text(
                                viewModel.miniAudioPlayer.title,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize*scale,
                                //modifier = Modifier.basicMarquee(),
                            )
                        }
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((24*scale).dp)
                        ) {
                            Text(
                                viewModel.miniAudioPlayer.subTitle,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(1f, 1f, 1f, 0.5f),
                                fontSize = MaterialTheme.typography.titleSmall.fontSize*scale,
                                //modifier = Modifier.basicMarquee(),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height((13*scale).dp))
                StreamingProgressBar(
                    viewModel,
                    viewModel.miniAudioPlayer.getPlayProgress(),
                    viewModel.miniAudioPlayer.getStreamingProgress(),
                    416f,
                    50f,
                    viewModel.miniAudioPlayer.getPlayTime(),
                    viewModel.miniAudioPlayer.getTotalTime(),
                    scale)
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = (10*scale).dp)
                ) {
                    Row(
                        modifier = Modifier.width((312*scale).dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.repeat),
                            contentDescription = "",
                            tint = if(viewModel.miniAudioPlayer.isLooped) Color(0xFF00BCD4) else Color.White,
                            modifier = Modifier
                                .size((36*scale).dp)
                                .clickable {
                                    viewModel.miniAudioPlayer.toggleLoop()
                                })
                        Icon(
                            painter = painterResource(R.drawable.skip_previous),
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier
                                .size((36*scale).dp)
                                .clickable {
                                    viewModel.miniAudioPlayer.playPrevious()
                                })
                        Box(contentAlignment = Alignment.Center,
                            modifier = Modifier.size((72*scale).dp).clickable{
                                viewModel.miniAudioPlayer.togglePlayOrPause()
                            }){
                            Surface(color = Color(0xff004A77), shape = RoundedCornerShape((36*scale).dp), modifier = Modifier.fillMaxSize()) {}
                            if(viewModel.miniAudioPlayer.isPaused)
                                Icon(imageVector = Icons.Default.PlayArrow, tint = Color.White, contentDescription = "", modifier = Modifier.size((48*scale).dp))
                            else
                                Image(painter = painterResource(R.drawable.pause), contentDescription = "", modifier = Modifier.width((24*scale).dp).height((26*scale).dp))
                        }
                        Icon(
                            painter = painterResource(R.drawable.skip_next),
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier
                                .size((36*scale).dp)
                                .clickable {
                                    viewModel.miniAudioPlayer.playNext()
                                })
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size((36*scale).dp).clickable{
                            viewModel.miniAudioPlayer.toggleIsLiked()
                            viewModel.showToast(if(viewModel.miniAudioPlayer.isLiked) "Favoriate!" else "Faviroate Cancelled!")
                        }){
                            Icon(imageVector = if(viewModel.miniAudioPlayer.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "",
                                tint = if(viewModel.miniAudioPlayer.isLiked) Color(0xffff0000) else Color(0xfff2f2f2),
                                modifier = Modifier.size((24*scale).dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewChat(){
    MiniPlayerTheme (darkTheme = false){
        ChatView(viewModel())
    }
}

@Preview
@Composable
fun PreViewMiniPlayerCard(){
    MiniPlayerTheme {
        MessageForMusicPlayerType(Message(MessageType.MESSAGE_TYPE_MUSIC_PLAYER,""), viewModel(),0.75f)
    }
}