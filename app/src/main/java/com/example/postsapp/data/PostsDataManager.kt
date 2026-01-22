package com.example.postsapp.data

import com.example.postsapp.data.local.PostDao
import com.example.postsapp.data.local.PostEntity
import com.example.postsapp.data.model.Post
import com.example.postsapp.data.remote.PostsApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Data Manager - Coordinates data flow between API and Database
 * 
 * Responsibilities:
 * 1. Fetch data from API
 * 2. Save data to Room database
 * 3. Provide Flow of data from database to UI
 * 
 * This is NOT a Repository (we're keeping it simple)
 * This class orchestrates the data flow
 */
class PostsDataManager(
    private val apiService: PostsApiService,
    private val postDao: PostDao,
    private val coroutineScope: CoroutineScope
) {
    
    /**
     * Observes posts from the database
     * UI will collect this Flow and update automatically when data changes
     */
    fun observePosts(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }
    
    /**
     * Fetches posts from API and saves to database
     * 
     * Data Flow:
     * 1. Call API on IO dispatcher (background thread)
     * 2. Convert API models to database entities
     * 3. Save to Room database
     * 4. Flow automatically emits new data to observers
     * 
     * @return Result with success or error message
     */
    suspend fun fetchAndSavePosts(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                println("PostsDataManager: Starting API fetch...")
                
                // Step 1: Fetch from API
                val posts = apiService.getPosts()
                println("PostsDataManager: Successfully fetched ${posts.size} posts from API")
                
                // Step 2: Convert to database entities
                val entities = posts.map { post ->
                    PostEntity(
                        id = post.id,
                        userId = post.userId,
                        title = post.title,
                        body = post.body
                    )
                }
                println("PostsDataManager: Converted ${entities.size} posts to entities")
                
                // Step 3: Save to database
                postDao.insertPosts(entities)
                println("PostsDataManager: Successfully saved ${entities.size} posts to database")
                
                Result.success(Unit)
            } catch (e: Exception) {
                println("PostsDataManager: ERROR - ${e.javaClass.simpleName}: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
    
    /**
     * Refreshes data - clears old data and fetches new
     */
    fun refreshPosts() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                postDao.deleteAllPosts()
                fetchAndSavePosts()
            } catch (e: Exception) {
                // Error handling can be improved with a proper error state
                e.printStackTrace()
            }
        }
    }
}
