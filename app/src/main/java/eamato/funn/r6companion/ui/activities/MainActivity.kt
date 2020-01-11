package eamato.funn.r6companion.ui.activities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import eamato.funn.r6companion.R
import eamato.funn.r6companion.ui.activities.abstracts.BaseActivity
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.notifications.R6NotificationManager
import eamato.funn.r6companion.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val navigationController: NavController by lazy {
        findNavController(R.id.fragment)
    }

    private val sensorManager: SensorManager? by lazy {
        ContextCompat.getSystemService(this, SensorManager::class.java)
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (mainViewModel.applyIlluminationSensorValue)
                mainViewModel.updateIlluminationLevel(event?.values?.get(0))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceManager.getDefaultSharedPreferences(this).getDarkMode().setDarkMode()

        mainViewModel.illuminationLevel.observe(this, Observer {
            it?.let { nonNullIlluminationLevel ->
                with (PreferenceManager.getDefaultSharedPreferences(this)) {
                    getDarkMode()
                        .takeIf { darkMode -> darkMode == PREFERENCE_DARK_MODE_VALUE_ADAPTIVE }
                        ?.let {
                            if (nonNullIlluminationLevel > getDarkModeIlluminationThreshold())
                                PREFERENCE_DARK_MODE_VALUE_OFF.setDarkMode()
                            else
                                PREFERENCE_DARK_MODE_VALUE_ON.setDarkMode()
                        }
                        ?.takeIf { newModeApplied -> newModeApplied }
                        ?.also {
                            mainViewModel.applyIlluminationSensorValue = false
                            Handler().postDelayed({
                                mainViewModel.applyIlluminationSensorValue = true
                            }, DARK_MODE_SWITCHER_DELAY)
                        }
                }
            }
        })

        R6NotificationManager.createNotificationChannel(
            context = this,
            notificationChannelName = getString(R.string.notification_channel_name),
            notificationChannelDescription = getString(R.string.notification_channel_description)
        )

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FirebaseInstance", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token
                if (token == null)
                    Log.w("FirebaseInstance", "Token is null")
                else
                    Log.d("FirebaseInstance", token)
            })
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)?.let { nonNullSensor ->
            sensorManager?.registerListener(sensorEventListener, nonNullSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(sensorEventListener)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navigationController)
        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            bnv?.menu?.forEach {
                if (destination.matchMenuDestination(it.itemId))
                    it.isChecked = true
            }
        }
        bnv?.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                if (navigationController.currentDestination?.id == item.itemId)
                    return false
                item.onNavDestinationSelected(navigationController)
                return true
            }
        })
    }

}