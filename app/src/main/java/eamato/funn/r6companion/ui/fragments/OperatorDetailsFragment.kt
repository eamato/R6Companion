package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.OperatorDetailsAdapter
import eamato.funn.r6companion.databinding.FragmentOperatorDetailsBinding
import eamato.funn.r6companion.entities.CompanionOperator
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_ABILITY_ENTITY
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_LOAD_OUT_ENTITY
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.recyclerview.GridSpacingItemDecoration
import eamato.funn.r6companion.viewmodels.OperatorDetailsViewModel

private const val SCREEN_NAME = "Operator details screen"

class OperatorDetailsFragment : BaseFragment() {

    private val operatorDetailsViewModel: OperatorDetailsViewModel? by viewModels()

    private var binding: FragmentOperatorDetailsBinding? = null

    private var operator: CompanionOperator? = null

    private val operatorDetailsAdapter = OperatorDetailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.run {
            operator = OperatorDetailsFragmentArgs.fromBundle(this).operator
        }

        operator?.run { operatorDetailsViewModel?.createListOfDetailsFor(this) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperatorDetailsBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        operatorDetailsViewModel?.operatorDetails?.observe(viewLifecycleOwner) {
            operatorDetailsAdapter.submitList(it)
        }

        binding?.rvOperatorDetails?.run {
            setHasFixedSize(true)
            adapter = operatorDetailsAdapter
            val gridLayoutManager = GridLayoutManager(context, 2)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter?.getItemViewType(position)) {
                        VIEW_TYPE_LOAD_OUT_ENTITY, VIEW_TYPE_ABILITY_ENTITY -> 1
                        else -> 2
                    }
                }
            }
            layoutManager = gridLayoutManager
            addItemDecoration(GridSpacingItemDecoration(
                context = context,
                spanCount = 2,
                spacingResId = R.dimen.margin_20_dp,
                includeEdge = true
            ))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.rvOperatorDetails?.adapter = null
        binding = null
        operator = null
    }

    override fun setLiveDataObservers() {

    }

    override fun onLiveDataObserversSet() {

    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }
}