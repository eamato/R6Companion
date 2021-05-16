package eamato.funn.r6companion

import android.app.Application
import androidx.preference.PreferenceManager
import eamato.funn.r6companion.utils.getDarkMode
import eamato.funn.r6companion.utils.setDarkMode

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        PreferenceManager.getDefaultSharedPreferences(this).getDarkMode().setDarkMode()
    }

}