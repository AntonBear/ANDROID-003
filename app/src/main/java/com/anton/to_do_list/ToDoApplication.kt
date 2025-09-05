package com.anton.to_do_list

import android.app.Application
import com.anton.to_do_list.data.network.NetworkMonitor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ToDoApplication : Application(){

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate() {
        super.onCreate()
        networkMonitor.startMonitoring()
    }
}
