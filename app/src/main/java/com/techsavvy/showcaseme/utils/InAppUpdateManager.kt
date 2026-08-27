package com.techsavvy.showcaseme.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateManager(private val activity: Activity) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    fun checkForAppUpdate(updateLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Trigger mandatory immediate update flow
                startImmediateUpdate(appUpdateInfo, updateLauncher)
            }
        }.addOnFailureListener { e ->
            Log.e("InAppUpdateManager", "Check for app update failed: ${e.message}")
        }
    }

    fun resumeUpdateIfInProgress(updateLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // Resume ongoing mandatory update flow if user re-opens app
                startImmediateUpdate(appUpdateInfo, updateLauncher)
            }
        }
    }

    private fun startImmediateUpdate(
        appUpdateInfo: AppUpdateInfo,
        updateLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            )
        } catch (e: Exception) {
            Log.e("InAppUpdateManager", "Failed to start immediate update flow: ${e.message}")
        }
    }
}