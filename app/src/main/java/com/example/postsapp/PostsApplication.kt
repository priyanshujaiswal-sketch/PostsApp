package com.example.postsapp

import android.app.Application
import com.example.postsapp.data.PostsDataManager
import com.example.postsapp.data.local.AppDatabase
import com.example.postsapp.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Application class - Entry point for the app
 * 
 * This is where we manually create our dependencies:
 * - Database
 * - API Service
 * - Data Manager
 * - Coroutine Scope
 * 
 * No Hilt or Dagger - we're doing simple manual dependency injection
 */
class PostsApplication : Application() {
    
    /**
     * Application-level coroutine scope
     * Uses SupervisorJob so if one coroutine fails, others continue
     */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    /**
     * Lazy initialization of database
     */
    val database by lazy {
        AppDatabase.getInstance(this)
    }
    
    /**
     * Lazy initialization of API service
     */
    val apiService by lazy {
        RetrofitClient.createApiService()
    }
    
    /**
     * Lazy initialization of data manager
     * This is what the UI will use to get data
     */
    val dataManager by lazy {
        PostsDataManager(
            apiService = apiService,
            postDao = database.postDao(),
            coroutineScope = applicationScope
        )
    }
}
