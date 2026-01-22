package com.example.postsapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Network model for API response
 * This represents the JSON structure from the API
 */
data class Post(
    @SerializedName("userId")
    val userId: Int,
    
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("body")
    val body: String
)
