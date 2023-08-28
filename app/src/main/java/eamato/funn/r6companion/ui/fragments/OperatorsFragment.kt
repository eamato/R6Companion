package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.OperatorsAdapter
import eamato.funn.r6companion.databinding.FragmentOperatorsBinding
import eamato.funn.r6companion.repositories.OperatorsRepository
import eamato.funn.r6companion.ui.fragments.abstracts.BaseCompanionFragment
import eamato.funn.r6companion.utils.FirebaseRemoteConfigDataFetcher
import eamato.funn.r6companion.utils.recyclerview.LinearMarginItemDecoration
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import eamato.funn.r6companion.utils.setOnItemClickListener
import eamato.funn.r6companion.viewmodels.OperatorsViewModel

private const val SCREEN_NAME = "Operators screen"

class OperatorsFragment : BaseCompanionFragment() {

    private var fragmentOperatorsBinding: FragmentOperatorsBinding? = null

    private val operatorsViewModel: OperatorsViewModel? by viewModels()

    private val operatorsAdapter = OperatorsAdapter()

    private var clickListener: RecyclerViewItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        operatorsViewModel?.getAllOperators(
            OperatorsRepository(context, FirebaseRemoteConfigDataFetcher(mainViewModel))
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentOperatorsBinding = FragmentOperatorsBinding.inflate(inflater, container, false)

        return fragmentOperatorsBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentOperatorsBinding?.rvOperators?.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = operatorsAdapter
            addItemDecoration(LinearMarginItemDecoration())
            if (clickListener == null) {
                clickListener = RecyclerViewItemClickListener(
                    context,
                    this,
                    object : RecyclerViewItemClickListener.OnItemClickListener {
                        override fun onItemClicked(view: View, position: Int) {
                            val selectedOperator = operatorsAdapter.getItemAt(position)
                            val arg = OperatorDetailsFragmentArgs.Builder(
                                selectedOperator,
                                selectedOperator.name
                            ).build().toBundle()

                            findNavController().navigate(
                                resId = R.id.OperatorDetailsFragment,
                                args = arg
                            )
                        }

                        override fun onItemLongClicked(view: View, position: Int) {

                        }
                    }
                )
            }
            clickListener?.let { nonNullClickListener ->
                setOnItemClickListener(nonNullClickListener)
            }
        }
    }

    override fun onDestroyView() {
        clickListener?.run {
            fragmentOperatorsBinding?.rvOperators?.removeOnItemTouchListener(this)
        }
        fragmentOperatorsBinding?.rvOperators?.adapter = null

        super.onDestroyView()

        fragmentOperatorsBinding = null
        clickListener = null
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        operatorsViewModel?.companionOperators?.observe(this) { operators ->
            operatorsAdapter.submitList(operators)
        }

//        mainViewModel.observableFirebaseRemoteConfig.observe(this) {
//            it?.let { nonNullFirebaseRemoteConfig ->
//                nonNullFirebaseRemoteConfig.getString(OPERATORS)
//                    .getFirebaseRemoteConfigEntity(Operators::class.java)?.let { nonNullOperators ->
//                        operatorsViewModel?.requestOperators(nonNullOperators)
//                    }
//            }
//        }
//
//        operatorsViewModel?.operators?.observe(this) {
//            it?.let { nonNullOperators ->
//                operatorsAdapter.submitList(nonNullOperators)
//            }
//        }
//
        operatorsViewModel?.isRequestActive?.observe(this) {
            if (it) {
                fragmentOperatorsBinding?.clpbOperators?.show()
            } else {
                fragmentOperatorsBinding?.clpbOperators?.hide()
            }
        }
//
//        operatorsViewModel?.requestError?.observe(this) {
//            it?.let { nonNullError ->
//
//            }
//        }
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