package com.yral.reels.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.*
import com.yral.reels.viewmodel.ReelsViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ReelFeedScreen(
    videos: List<ReelsViewModel.VideoResponse>,
    viewModel: ReelsViewModel,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState()

    VerticalPager(
        count = videos.size,
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->

        val video = videos[page]

        ReelItem(
            videoId = video.videoId,
            videoUrl = video.url ?: "",
            isPlaying = pagerState.currentPage == page
        )
    }

    // pagination
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                if (page >= videos.size - 3) {
                    viewModel.fetchNextPage()
                }
            }
    }
}