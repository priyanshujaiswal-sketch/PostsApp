package com.example.postsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.postsapp.data.PostsDataManager
import com.example.postsapp.data.local.PostEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PostsScreen(dataManager: PostsDataManager) {
    // State to hold UI state
    var uiState by remember { mutableStateOf<UiState>(UiState.Loading) }
    

    LaunchedEffect(Unit) {
        println("PostsScreen: LaunchedEffect started")

        // Observe data from database using Flow (this runs continuously)
        println("PostsScreen: Starting to observe posts from database...")
        launch {
            dataManager.observePosts()
                .catch { exception ->
                    println("PostsScreen: Flow error - ${exception.message}")
                }
                .collect { posts ->
                    println("PostsScreen: Received ${posts.size} posts from Flow")
                    if (posts.isNotEmpty()) {
                        // If we have cached data, show it regardless of API status
                        uiState = UiState.Success(posts)
                    }
                }
        }
        
        // Try to fetch fresh data from API (runs once)
        launch(Dispatchers.IO) {
            println("PostsScreen: Starting fetchAndSavePosts...")
            val result = dataManager.fetchAndSavePosts()
            if (result.isFailure) {
                println("PostsScreen: Fetch failed - ${result.exceptionOrNull()?.message}")
                // Only show error if we don't have any cached data
                withContext(Dispatchers.Main) {
                    if (uiState is UiState.Loading) {
                        uiState = UiState.Error(
                            result.exceptionOrNull()?.message ?: "Unknown error occurred"
                        )
                    }
                }
            } else {
                println("PostsScreen: Fetch succeeded!")
            }
        }
    }
    
    // UI based on state
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e)
                    )
                )
            )
    ) {
        when (val state = uiState) {
            is UiState.Loading -> LoadingView()
            is UiState.Success -> PostsList(posts = state.posts)
            is UiState.Error -> ErrorView(message = state.message)
        }
    }
}


@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = Color(0xFF00d4ff),
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading posts...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun ErrorView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0f3460)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFe94560)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}


@Composable
fun PostsList(posts: List<PostEntity>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0f3460),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "📱 Posts Feed",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${posts.size} posts loaded",
                    fontSize = 14.sp,
                    color = Color(0xFF00d4ff)
                )
            }
        }
        
        // Posts list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post ->
                PostItem(post = post)
            }
        }
    }
}

@Composable
fun PostItem(post: PostEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0f3460)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Post ID badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF00d4ff).copy(alpha = 0.2f),
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = "#${post.id}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00d4ff)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Title
            Text(
                text = post.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Body
            Text(
                text = post.body,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // User ID
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👤",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "User ${post.userId}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
