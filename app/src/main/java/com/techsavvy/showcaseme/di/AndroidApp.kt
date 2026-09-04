package com.techsavvy.showcaseme.di

import android.app.Application
import com.techsavvy.showcaseme.push.PushNotifications
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Created up front rather than when the first message lands: Android 8
        // and above discard a notification posted to a channel that does not
        // exist yet, and the first one usually arrives while the app is closed.
        PushNotifications.ensureChannel(this)
    }
}
