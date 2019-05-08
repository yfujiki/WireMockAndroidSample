package com.yfujiki.wiremocksample

import android.app.Activity
import android.app.Application

class WireMockSampleApp: Application() {

    lateinit var service: TMDBService
        private set

    companion object {
        private lateinit var instance: WireMockSampleApp

        public fun getInstance(): WireMockSampleApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        service = ServiceManager.tmdbService() // ToDo : Switch this in testing context
    }
}