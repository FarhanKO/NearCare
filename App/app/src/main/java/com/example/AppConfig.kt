package com.example

import com.example.BuildConfig

/**
 * Centralized configuration for platform-specific keys and debugging.
 */
object AppConfig {
    /**
     * SET TO TRUE TO TEST YOUR iOS KEY ON AN ANDROID DEVICE.
     * When false, it automatically detects the platform.
     */
    private const val DEBUG_FORCE_IOS_KEY = false

    /**
     * Returns the appropriate Maps API key based on the current platform or debug override.
     */
    fun getMapsApiKey(): String {
        if (DEBUG_FORCE_IOS_KEY) return BuildConfig.IOS_MAPS_API_KEY
        
        val osName = System.getProperty("os.name")?.lowercase() ?: "android"
        return if (osName.contains("ios") || osName.contains("mac")) {
            BuildConfig.IOS_MAPS_API_KEY
        } else {
            BuildConfig.MAPS_API_KEY
        }
    }
}
