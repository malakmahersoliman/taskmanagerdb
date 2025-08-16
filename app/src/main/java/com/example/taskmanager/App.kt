package com.example.taskmanager

import android.app.Application
import android.util.Log
import com.example.taskmanager.data.DBSeed

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        DBSeed.run(this)
    }
}
