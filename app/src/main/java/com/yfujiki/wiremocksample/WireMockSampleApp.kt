package com.yfujiki.wiremocksample

import android.app.Activity
import android.app.Application
import java.util.concurrent.atomic.AtomicBoolean

class WireMockSampleApp: Application() {

    lateinit var service: TMDBService
        private set

    private var _isRunningUITest: AtomicBoolean? = null

    companion object {
        private lateinit var instance: WireMockSampleApp

        public fun getInstance(): WireMockSampleApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        if (isRunningUITest()) {
            service = ServiceManager.tmdbTestService()
        } else {
            service = ServiceManager.tmdbService()
        }
    }

    private fun isRunningUITest(): Boolean {
        if (null == _isRunningUITest) {
            var isTest: Boolean

            try {
                Class.forName("androidx.test.espresso.Espresso")
                isTest = true
            } catch (e: ClassNotFoundException) {
                isTest = false
            }

            _isRunningUITest = AtomicBoolean(isTest)
        }

        return _isRunningUITest!!.get()
    }

}