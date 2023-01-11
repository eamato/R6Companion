package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.OperatorsAdapter
import eamato.funn.r6companion.databinding.FragmentOperatorsBinding
import eamato.funn.r6companion.entities.Operators
import eamato.funn.r6companion.firebase.things.OPERATORS
import eamato.funn.r6companion.ui.fragments.abstracts.BaseCompanionFragment
import eamato.funn.r6companion.utils.getFirebaseRemoteConfigEntity
import eamato.funn.r6companion.utils.recyclerview.LinearMarginItemDecoration
import eamato.funn.r6companion.viewmodels.OperatorsViewModel

private const val SCREEN_NAME = "Operators screen"

class OperatorsFragment : BaseCompanionFragment() {

    private var fragmentOperatorsBinding: FragmentOperatorsBinding? = null

    private val operatorsViewModel: OperatorsViewModel? by viewModels()

    private val operatorsAdapter = OperatorsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentOperatorsBinding = FragmentOperatorsBinding.inflate(inflater, container, false)

        return fragmentOperatorsBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentOperatorsBinding?.rvOperators?.setHasFixedSize(true)
        fragmentOperatorsBinding?.rvOperators?.layoutManager = LinearLayoutManager(context)
        fragmentOperatorsBinding?.rvOperators?.adapter = operatorsAdapter
        fragmentOperatorsBinding?.rvOperators?.addItemDecoration(LinearMarginItemDecoration())
    }

    override fun onDestroyView() {
        fragmentOperatorsBinding?.rvOperators?.adapter = null

        super.onDestroyView()

        fragmentOperatorsBinding = null
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        mainViewModel.observableFirebaseRemoteConfig.observe(this) {
            it?.let { nonNullFirebaseRemoteConfig ->
                nonNullFirebaseRemoteConfig.getString(OPERATORS)
                    .getFirebaseRemoteConfigEntity(Operators::class.java)?.let { nonNullOperators ->
                        operatorsViewModel?.requestOperators(nonNullOperators)
                    }
            }
        }

        operatorsViewModel?.operators?.observe(this) {
            it?.let { nonNullOperators ->
                operatorsAdapter.submitList(nonNullOperators)
            }
        }

        operatorsViewModel?.isRequestActive?.observe(this) {
            if (it) {
                fragmentOperatorsBinding?.clpbOperators?.show()
            } else {
                fragmentOperatorsBinding?.clpbOperators?.hide()
            }
        }

        operatorsViewModel?.requestError?.observe(this) {
            it?.let { nonNullError ->

            }
        }
    }

    override fun onLiveDataObserversSet() {

    }

    override fun getFragmentsTitle(): Int {
        return R.string.operators_fragment_label
    }

    override fun getFragmentsIcon(): Int {
        return R.drawable.ic_operators_24dp
    }

}