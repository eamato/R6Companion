package eamato.funn.r6companion.ui.fragments

import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment

private const val SCREEN_NAME = "Settings screen"

class SettingsFragment : BaseFragment() {

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

}