package com.example.postsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity
 * This represents how posts are stored in the local database
 */
@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)
