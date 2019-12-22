package eamato.funn.r6companion.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import eamato.funn.r6companion.R
import eamato.funn.r6companion.ui.activities.abstracts.BaseActivity
import eamato.funn.r6companion.utils.isDarkModeEnabled
import eamato.funn.r6companion.utils.notifications.R6NotificationManager
import eamato.funn.r6companion.utils.setDarkMode
import eamato.funn.r6companion.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val navigationController: NavController by lazy {
        findNavController(R.id.fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceManager.getDefaultSharedPreferences(this).isDarkModeEnabled().setDarkMode()

        mainViewModel

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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navigationController)
        bnv.setupWithNavController(navigationController) //TODO fix back press bug!
//        bnv.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
//            override fun onNavigationItemSelected(item: MenuItem): Boolean {
//                if (navigationController.currentDestination?.id == item.itemId)
//                    return false
//                item.onNavDestinationSelected(navigationController)
//                return true
//            }
//        })
    }

}
