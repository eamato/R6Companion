package eamato.funn.r6companion.ui.activities

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import eamato.funn.r6companion.R
import eamato.funn.r6companion.databinding.ActivityMainBinding
import eamato.funn.r6companion.ui.activities.abstracts.BaseActivity
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.notifications.R6NotificationManager
import eamato.funn.r6companion.viewmodels.MainViewModel

class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.illuminationLevel.observe(this, {
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
                            Handler(Looper.getMainLooper()).postDelayed({
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

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.w("FirebaseInstance", "getInstanceId failed", it.exception)
                return@addOnCompleteListener
            }

            val token = it.result

            if (token == null)
                Log.w("FirebaseInstance", "Token is null")
            else
                Log.d("FirebaseInstance", token)
        }

        MobileAds.initialize(this)
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

        setParentToolbar()

        navigationController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bnv.menu.forEach {
                if (destination.matchMenuDestination(it.itemId))
                    it.isChecked = true
            }
        }
        binding.bnv.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                if (navigationController.currentDestination?.id == item.itemId)
                    return false
                item.onNavDestinationSelected(navigationController)
                return true
            }
        })
    }

    fun setParentToolbar(parentToolbar: Toolbar = binding.toolbar) {
        setSupportActionBar(parentToolbar)
        NavigationUI.setupWithNavController(binding.toolbar, navigationController)
        supportActionBar?.show()
    }

}