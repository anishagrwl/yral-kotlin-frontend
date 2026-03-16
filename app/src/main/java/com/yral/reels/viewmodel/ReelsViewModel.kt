package com.yral.reels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yral.reels.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReelsViewModel : ViewModel() {

    data class VideoResponse(
        val videoId: Int = 0,
        val url: String? = null
    )

    private val _links = MutableStateFlow<List<VideoResponse>>(emptyList())
    val links: StateFlow<List<VideoResponse>> = _links

    private var currentPage = 0
    private val pageSize = 20
    private var totalVideos = Int.MAX_VALUE
    private var isLoading = false

    fun fetchNextPage() {
        if (isLoading || _links.value.size >= totalVideos) return

        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getLinks(currentPage, pageSize)
                if (response.success) {
                    _links.value = _links.value + response.data
                    currentPage++
                    totalVideos = response.total
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}