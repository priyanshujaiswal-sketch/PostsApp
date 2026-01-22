package com.example.postsapp.ui

import com.example.postsapp.data.local.PostEntity

/**
 * Represents the different states of the UI
 * This makes it easy to handle loading, success, and error states
 */
sealed class UiState {
    data object Loading : UiState()
    data class Success(val posts: List<PostEntity>) : UiState()
    data class Error(val message: String) : UiState()
}
