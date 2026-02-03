package com.fitness.sample

import android.app.Application
import com.fitness.sdk.FitnessSDK

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the Fitness SDK
        FitnessSDK.initialize(this) {
            databaseName("fitness_sample_db")
            enableLogging(true)
        }
    }
}
