package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.viewpager_adapters.CompanionAdapter
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import kotlinx.android.synthetic.main.fragment_companion.*

private const val SCREEN_NAME = "Companion screen"

class CompanionFragment : BaseFragment() {

    private val companionAdapter: CompanionAdapter by lazy {
        CompanionAdapter(this)
    }

    private val tabLayoutMediator: TabLayoutMediator by lazy {
        TabLayoutMediator(tl_companion_tabs, vp_companion_screens,
            TabConfigurationStrategy { tab, position ->
                context?.let { nonNullContext ->
                    tab.icon = ContextCompat.getDrawable(
                        nonNullContext, companionAdapter.fragments[position].getFragmentsIcon()
                    )
                    tab.text = getString(companionAdapter.fragments[position].getFragmentsTitle())
                }
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_companion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vp_companion_screens?.adapter = companionAdapter
        tabLayoutMediator.attach()
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {

    }

    override fun onLiveDataObserversSet() {

    }

}