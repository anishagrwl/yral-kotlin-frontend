package com.yral.reels.network

data class ResponseDTO<T>(
    val success: Boolean,
    val message: String,
    val data: T,
    val page: Int,
    val size: Int,
    val total: Int
)