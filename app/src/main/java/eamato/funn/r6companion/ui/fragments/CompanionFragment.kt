package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import eamato.funn.r6companion.adapters.viewpager_adapters.CompanionAdapter
import eamato.funn.r6companion.databinding.FragmentCompanionBinding
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment

private const val SCREEN_NAME = "Companion screen"

class CompanionFragment : BaseFragment() {

    private var fragmentCompanionBinding: FragmentCompanionBinding? = null

    private val companionAdapter: CompanionAdapter by lazy {
        CompanionAdapter(this)
    }

    private val tabLayoutMediator: TabLayoutMediator? by lazy {
        val tabs = fragmentCompanionBinding?.tlCompanionTabs
        val screens = fragmentCompanionBinding?.vpCompanionScreens
        if (tabs != null && screens != null) {
            TabLayoutMediator(tabs, screens) { tab, position ->
                context?.let { nonNullContext ->
                    tab.icon = ContextCompat.getDrawable(
                        nonNullContext, companionAdapter.fragments[position].getFragmentsIcon()
                    )
                    tab.text = getString(companionAdapter.fragments[position].getFragmentsTitle())
                }
            }
        } else {
            null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentCompanionBinding = FragmentCompanionBinding.inflate(inflater, container, false)
        return fragmentCompanionBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentCompanionBinding?.vpCompanionScreens?.adapter = companionAdapter
        tabLayoutMediator?.attach()
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {

    }

    override fun onLiveDataObserversSet() {

    }

}