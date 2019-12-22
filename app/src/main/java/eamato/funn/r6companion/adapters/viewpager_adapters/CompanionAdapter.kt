package eamato.funn.r6companion.adapters.viewpager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import eamato.funn.r6companion.ui.fragments.MapsFragment
import eamato.funn.r6companion.ui.fragments.OperatorsFragment
import eamato.funn.r6companion.ui.fragments.WeaponsFragment
import eamato.funn.r6companion.ui.fragments.abstracts.BaseCompanionFragment

class CompanionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    val fragments: Array<BaseCompanionFragment> = arrayOf(
        OperatorsFragment(), WeaponsFragment(), MapsFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}