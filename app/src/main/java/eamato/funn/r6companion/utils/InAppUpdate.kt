package eamato.funn.r6companion.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import eamato.funn.r6companion.R

class InAppUpdate(activity: Activity) : InstallStateUpdatedListener {

    private var appUpdateManager: AppUpdateManager
    private val MY_REQUEST_CODE = 500
    private var parentActivity: Activity = activity

    private var currentType = AppUpdateType.FLEXIBLE

    init {
        appUpdateManager = AppUpdateManagerFactory.create(parentActivity)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val thereIsAnUpdate = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            if (thereIsAnUpdate) {
                startUpdate(info)
                currentType = AppUpdateType.IMMEDIATE
            }
        }

        appUpdateManager.registerListener(this)
    }

    override fun onStateUpdate(state: InstallState) {
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                Snackbar.make(
                    parentActivity.findViewById(R.id.bnv),
                    parentActivity.getString(R.string.in_app_update_downloaded),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            InstallStatus.FAILED -> {
                Snackbar.make(
                    parentActivity.findViewById(R.id.bnv),
                    parentActivity.getString(R.string.in_app_update_failed),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            InstallStatus.INSTALLED -> {
                appUpdateManager.completeUpdate()

                Snackbar.make(
                    parentActivity.findViewById(R.id.bnv),
                    parentActivity.getString(R.string.in_app_update_installed),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startUpdate(info: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(info, AppUpdateType.IMMEDIATE, parentActivity, MY_REQUEST_CODE)
    }

    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            when (currentType) {
                AppUpdateType.IMMEDIATE -> {
                    if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        startUpdate(info)
                    }
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                parentActivity.finishAffinity()
            }
        }
    }

    fun onDestroy() {
        appUpdateManager.unregisterListener(this)
    }
}