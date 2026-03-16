package com.yral.reels.network

import com.yral.reels.viewmodel.ReelsViewModel
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class VideoInteraction(
    val likes: Int = 0,
    val comments: List<String> = emptyList()
)

interface ReelsApi {
    @GET("/yral/reels/interactions")
    suspend fun getInteractions(@Query("videoId") videoId: Int): VideoInteraction

    @POST("/yral/reels/like")
    suspend fun likeVideo(@Query("videoId") videoId: Int): VideoInteraction

    @POST("/yral/reels/comment")
    suspend fun commentVideo(
        @Query("videoId") videoId: Int,
        @Query("comment") comment: String
    ): VideoInteraction

    @GET("/yral/reels/links")
    suspend fun getLinks(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ResponseDTO<List<ReelsViewModel.VideoResponse>>
}