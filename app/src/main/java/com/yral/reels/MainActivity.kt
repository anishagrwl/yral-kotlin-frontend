package com.yral.reels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.yral.reels.ui.theme.ReelFeedScreen
import com.yral.reels.ui.theme.YralTheme
import com.yral.reels.viewmodel.ReelsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel = ReelsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YralTheme {
                Scaffold { innerPadding ->  // <-- get padding from Scaffold
                    val links by viewModel.links.collectAsState()

                    LaunchedEffect(Unit) {
                        viewModel.fetchNextPage()
                    }

                    // Apply the padding to the feed
                    ReelFeedScreen(
                        videos = links,
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}