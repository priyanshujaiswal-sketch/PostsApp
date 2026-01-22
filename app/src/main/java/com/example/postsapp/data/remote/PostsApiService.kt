package com.example.postsapp.data.remote

import com.example.postsapp.data.model.Post
import retrofit2.http.GET

interface PostsApiService {

    @GET("posts")
    suspend fun getPosts(): List<Post>
}
