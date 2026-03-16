package com.yral.reels.ui.theme

import android.content.Intent
import android.net.Uri
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.yral.reels.network.RetrofitInstance
import com.yral.reels.network.VideoInteraction
import kotlinx.coroutines.launch

@Composable
fun ReelItem(
    videoId: Int,
    videoUrl: String,
    isPlaying: Boolean = true
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var playing by remember { mutableStateOf(isPlaying) }

    var likes by remember { mutableStateOf(0) }
    var comments by remember { mutableStateOf(listOf<String>()) }

    var showCommentDialog by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }

    val player = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
            playWhenReady = isPlaying
        }
    }

    // Fetch interactions
    LaunchedEffect(videoId) {
        try {
            val interaction: VideoInteraction =
                RetrofitInstance.api.getInteractions(videoId)

            likes = interaction.likes
            comments = interaction.comments

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(playing) {
        player.playWhenReady = playing
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { playing = !playing }
    ) {

        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // LIKE
            IconButton(onClick = {

                scope.launch {
                    try {
                        val result =
                            RetrofitInstance.api.likeVideo(videoId)

                        likes = result.likes

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }) {
                Icon(Icons.Default.Favorite, contentDescription = "Like")
            }

            Text(likes.toString())

            Spacer(modifier = Modifier.height(16.dp))

            // COMMENT
            IconButton(onClick = {
                showCommentDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Comment")
            }

            Text(comments.size.toString())

            Spacer(modifier = Modifier.height(16.dp))

            // SHARE
            IconButton(onClick = {

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, videoUrl)
                }

                context.startActivity(
                    Intent.createChooser(intent, "Share Video")
                )

            }) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        }
    }

    // COMMENT DIALOG
    if (showCommentDialog) {

        AlertDialog(

            onDismissRequest = { showCommentDialog = false },

            confirmButton = {

                IconButton(onClick = {

                    scope.launch {

                        try {

                            val result =
                                RetrofitInstance.api.commentVideo(
                                    videoId,
                                    newComment
                                )

                            comments = result.comments
                            newComment = ""

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }

            },

            dismissButton = {
                TextButton(onClick = { showCommentDialog = false }) {
                    Text("Close")
                }
            },

            title = { Text("Comments") },

            text = {

                Column {

                    LazyColumn(
                        modifier = Modifier.height(200.dp)
                    ) {

                        items(comments) { comment ->
                            Text(comment)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        placeholder = { Text("Add a comment...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                }

            }

        )
    }
}