package eamato.funn.r6companion.ui.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import eamato.funn.r6companion.R
import eamato.funn.r6companion.ui.activities.abstracts.BaseActivity
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

        mainViewModel
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navigationController)
        bnv.setupWithNavController(navigationController)
    }

}
