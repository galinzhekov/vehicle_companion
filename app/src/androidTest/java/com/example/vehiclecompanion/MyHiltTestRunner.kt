package com.example.vehiclecompanion // Match your app's base package

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

// A custom runner to set up the Hilt environment for UI tests
class MyHiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // Use HiltTestApplication for all Hilt UI tests
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}

