package eamato.funn.r6companion.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import eamato.funn.r6companion.R
import eamato.funn.r6companion.databinding.ActivityMainBinding
import eamato.funn.r6companion.entities.dto.RouletteFragmentArgument
import eamato.funn.r6companion.ui.activities.abstracts.BaseActivity
import eamato.funn.r6companion.ui.fragments.RouletteFragmentArgs
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.notifications.R6NotificationManager
import eamato.funn.r6companion.viewmodels.MainViewModel

class MainActivity : BaseActivity() {

    private val requestPermissionLauncher: ActivityResultLauncher<String> by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                createNotificationChannel()
                registerNotificationToken()
            }
        }
    }

    private val mainViewModel: MainViewModel by viewModels()

    private var binding: ActivityMainBinding? = null

    private var sensorManager: SensorManager? = null

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

        if (sensorManager == null)
            sensorManager = ContextCompat.getSystemService(this, SensorManager::class.java)

        installSplashScreen().apply {
            setKeepOnScreenCondition { mainViewModel.isLoadingSplash.value }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding?.run {
            setContentView(root)
            (supportFragmentManager.findFragmentById(R.id.fragment) as? NavHostFragment)?.navController?.run { bnv.setupWithNavController(this) }
        }

//        navigationController.addOnDestinationChangedListener { _, destination, _ ->
//            binding.bnv.menu.forEach {
//                if (destination.matchMenuDestination(it.itemId))
//                    it.isChecked = true
//            }
//        }

//        binding.bnv.setOnItemSelectedListener { item ->
//            if (navigationController.currentDestination?.id == item.itemId)
//                return@setOnItemSelectedListener false
//            item.onNavDestinationSelected(navigationController)
//            return@setOnItemSelectedListener true
//        }

        setParentToolbar()

        mainViewModel.illuminationLevel.observe(this) {
            it?.let { nonNullIlluminationLevel ->
                with(PreferenceManager.getDefaultSharedPreferences(this)) {
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
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                createNotificationChannel()
                registerNotificationToken()
            }
        } else {
            createNotificationChannel()
            registerNotificationToken()
        }

        checkLink(intent)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()

        sensorManager = null
        binding = null
    }

    override fun onResume() {
        super.onResume()

        sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)?.let { nonNullSensor ->
            sensorManager?.registerListener(
                sensorEventListener,
                nonNullSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(sensorEventListener)
    }

    fun setParentToolbar(parentToolbar: Toolbar? = binding?.toolbar) {
        setSupportActionBar(parentToolbar)
        binding?.toolbar?.run toolbar@ {
            (supportFragmentManager.findFragmentById(R.id.fragment) as? NavHostFragment)?.navController?.run controller@ {
                NavigationUI.setupWithNavController(this@toolbar, this@controller)
            }
        }
        supportActionBar?.show()
    }

    private fun checkLink(intent: Intent?) {
        Firebase.dynamicLinks.getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData: PendingDynamicLinkData? ->
                val link = pendingDynamicLinkData?.link
                Log.d("DynamicLinks", "Data: $link")
                if (link == null)
                    return@addOnSuccessListener
                val uri = link.buildUpon().build()
                val path = uri.path ?: return@addOnSuccessListener
                when (path) {
                    "/roll" -> {
                        try {
                            val operators = uri.getQueryParameters("operator_name")
                            val argument = RouletteFragmentArgument(operators)
                            val bundle = RouletteFragmentArgs.Builder()
                                .setRouletteFragmentArgument(argument)
                                .build()
                                .toBundle()

                            (supportFragmentManager.findFragmentById(R.id.fragment) as? NavHostFragment)?.navController?.navigate(
                                R.id.rouletteFragment,
                                bundle,
                                navOptions {
                                    popUpTo(R.id.homeFragment) {
                                        inclusive = false
                                        saveState = true
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("DynamicLinks", "Error")
            }
    }

    private fun createNotificationChannel() {
        R6NotificationManager.createNotificationChannel(
            context = this,
            notificationChannelName = getString(R.string.notification_channel_name),
            notificationChannelDescription = getString(R.string.notification_channel_description)
        )
    }

    private fun registerNotificationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FirebaseInstance", "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result

            if (token == null) {
                Log.w("FirebaseInstance", "Token is null")
            } else {
                Log.d("FirebaseInstance", token)
                mainViewModel.registerNotificationToken(token)
            }
        }
    }
}