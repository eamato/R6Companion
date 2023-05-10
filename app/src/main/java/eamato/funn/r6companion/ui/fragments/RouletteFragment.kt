package eamato.funn.r6companion.ui.fragments

import android.content.Intent
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.RouletteOperatorsAdapter
import eamato.funn.r6companion.databinding.FragmentRouletteBinding
import eamato.funn.r6companion.repositories.OperatorsRepository
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import eamato.funn.r6companion.viewmodels.RouletteViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

private const val SCREEN_NAME = "Roulette screen"

class RouletteFragment : BaseFragment(), SearchView.OnQueryTextListener {

    private var searchQuery: String? = ""

    private val compositeDisposable = CompositeDisposable()

    private val rouletteViewModel: RouletteViewModel? by viewModels()

    private var fragmentRouletteBinding: FragmentRouletteBinding? = null

    private val rouletteOperatorsAdapter: RouletteOperatorsAdapter by lazy {
        RouletteOperatorsAdapter()
    }

    private var allOperatorsRouletteClickListener: RecyclerViewItemClickListener? = null

    private val argument: RouletteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        savedInstanceState?.restoreStateIfNeed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentRouletteBinding = FragmentRouletteBinding.inflate(inflater, container, false)
        return fragmentRouletteBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allOperatorsRouletteClickListener == null)
            allOperatorsRouletteClickListener = fragmentRouletteBinding?.rvAllRouletteOperators?.let {
            RecyclerViewItemClickListener(context, it, object : RecyclerViewItemClickListener.OnItemClickListener {
                override fun onItemClicked(view: View, position: Int) {
                    rouletteViewModel?.selectUnSelectRouletteOperator(rouletteOperatorsAdapter.getItemAtPosition(position))
                }

                override fun onItemLongClicked(view: View, position: Int) {

                }
            })
        }
        fragmentRouletteBinding?.rvAllRouletteOperators?.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(context, 3)
        activity?.resources?.configuration?.orientation?.takeIf { it == ORIENTATION_LANDSCAPE }?.run {
            layoutManager = GridLayoutManager(context, 5)
        }
        fragmentRouletteBinding?.rvAllRouletteOperators?.layoutManager = layoutManager
        fragmentRouletteBinding?.rvAllRouletteOperators?.adapter = rouletteOperatorsAdapter
        allOperatorsRouletteClickListener?.let {
            fragmentRouletteBinding?.rvAllRouletteOperators?.setOnItemClickListener(it)
        }

        fragmentRouletteBinding?.btnRoll?.run {
            val canRoll = rouletteViewModel?.canRoll?.value ?: false

            isEnabled = canRoll

            text = if (canRoll) {
                val allOperators = rouletteViewModel?.visibleRouletteOperators?.value?.size ?: 0
                var selectedOperators = 0
                rouletteViewModel?.visibleRouletteOperators?.value?.forEach { operator ->
                    if (operator.isSelected)
                        selectedOperators++
                }
                getString(
                    R.string.roll_counted_pattern, selectedOperators, allOperators
                )
            } else {
                getString(R.string.roll)
            }

            setOnClickListener {
                context?.let { nonNullContext ->
                    val inputManager = ContextCompat.getSystemService(nonNullContext, InputMethodManager::class.java)
                    inputManager?.hideSoftInputFromWindow(it.windowToken, 0)
                }
                rouletteViewModel?.roll()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (rouletteViewModel?.visibleRouletteOperators?.value?.isNullOrEmpty() == true) {
            context?.let { nonNullContext ->
                rouletteViewModel?.getAllOperators(
                    OperatorsRepository(nonNullContext, FirebaseRemoteConfigDataFetcher(mainViewModel)),
                    PreferenceManager.getDefaultSharedPreferences(nonNullContext),
                    argument.rouletteFragmentArgument?.operatorNames
                )
            }
        }
    }

    override fun onDestroyView() {
        fragmentRouletteBinding?.rvAllRouletteOperators?.adapter = null

        super.onDestroyView()

        fragmentRouletteBinding = null
        allOperatorsRouletteClickListener = null
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_roulette, menu)

        menu.findItem(R.id.app_bar_search)?.let { nonNullSearch ->
            val searchView = nonNullSearch.actionView as SearchView

            val sq = searchQuery
            if (sq != null && sq.isNotEmpty()) {
                searchView.clearFocus()
                searchView.isIconified = false
                nonNullSearch.expandActionView()
                searchView.setOnQueryTextListener(this)
                searchView.setQuery(sq, true)
            } else {
                searchView.setOnQueryTextListener(this)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.save_selected)?.isVisible = rouletteViewModel?.areThereAnySelectedOperators() ?: false

        context?.run {
            val disposable = PreferenceManager.getDefaultSharedPreferences(this).areThereSavedSelectedOperators()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        menu.findItem(R.id.delete_saved)?.isVisible = it
                        menu.findItem(R.id.restore_saved)?.isVisible = it
                    },
                    {
                        menu.findItem(R.id.delete_saved)?.isVisible = false
                        menu.findItem(R.id.restore_saved)?.isVisible = false
                    }
                )

            compositeDisposable.add(disposable)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.alphabetic_sort_ascending -> {
                rouletteViewModel?.sortByNameAscending {
                    fragmentRouletteBinding?.rvAllRouletteOperators?.smoothScrollToPosition(0)
                }
                true
            }
            R.id.alphabetic_sort_descending -> {
                rouletteViewModel?.sortByNameDescending {
                    fragmentRouletteBinding?.rvAllRouletteOperators?.smoothScrollToPosition(0)
                }
                true
            }
            R.id.sort_selected -> {
                rouletteViewModel?.sortSelected {
                    fragmentRouletteBinding?.rvAllRouletteOperators?.smoothScrollToPosition(0)
                }
                true
            }
            R.id.select_all -> {
                rouletteViewModel?.selectAll()
                true
            }
            R.id.un_select_all -> {
                rouletteViewModel?.unSelectAll()
                true
            }
            R.id.save_selected -> {
                context?.let { nonNullContext ->
                    if (isAdded) {
                        AlertDialog.Builder(nonNullContext)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.attention)
                            .setMessage(R.string.save_confirmation_message)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                rouletteViewModel?.saveSelectedOperators(
                                    PreferenceManager.getDefaultSharedPreferences(nonNullContext)
                                ) {
                                    activity?.invalidateOptionsMenu()
                                }
                            }
                            .setNegativeButton(R.string.no) { _, _ -> }
                            .create()
                            .show()
                    }
                }
                true
            }
            R.id.delete_saved -> {
                context?.let { nonNullContext ->
                    if (isAdded) {
                        AlertDialog.Builder(nonNullContext)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.attention)
                            .setMessage(R.string.delete_saved_confirmation_message)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                rouletteViewModel?.deleteSavedSelectedOperators(
                                    PreferenceManager.getDefaultSharedPreferences(nonNullContext)
                                ) {
                                    activity?.invalidateOptionsMenu()
                                }
                            }
                            .setNegativeButton(R.string.no) { _, _ -> }
                            .create()
                            .show()
                    }
                }
                true
            }
            R.id.restore_saved -> {
                context?.let { nonNullContext ->
                    rouletteViewModel?.selectPreviouslySelectedOperators(
                        PreferenceManager.getDefaultSharedPreferences(nonNullContext)
                    )
                }
                true
            }
            R.id.share_roll -> {
                rouletteViewModel?.createShortDynamicLink()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(ROULETTE_SEARCH_QUERY_KEY, searchQuery)
        super.onSaveInstanceState(outState)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchQuery = newText
        newText?.let { nonNullNewText ->
            rouletteViewModel?.filter(nonNullNewText)
        } ?: rouletteViewModel?.restore()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchQuery = query
        query?.let { nonNullQuery ->
            rouletteViewModel?.filter(nonNullQuery)
        } ?: rouletteViewModel?.restore()
        return true
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        rouletteViewModel?.canRoll?.observe(this) {
            fragmentRouletteBinding?.btnRoll?.isEnabled = it

            if (it) {
                val allOperators = rouletteViewModel?.visibleRouletteOperators?.value?.size ?: 0
                var selectedOperators = 0
                rouletteViewModel?.visibleRouletteOperators?.value?.forEach { operator ->
                    if (operator.isSelected)
                        selectedOperators++
                }
                fragmentRouletteBinding?.btnRoll?.text = getString(
                    R.string.roll_counted_pattern, selectedOperators, allOperators
                )
            } else {
                fragmentRouletteBinding?.btnRoll?.text = getString(R.string.roll)
            }
        }

        rouletteViewModel?.isRequestActive?.observe(this, {
            if (it)
                fragmentRouletteBinding?.clpbWaiting?.show()
            else
                fragmentRouletteBinding?.clpbWaiting?.hide()
        })

        rouletteViewModel?.visibleRouletteOperators?.observe(this, { rouletteOperators ->
            rouletteOperators?.let { nonNullRouletteOperators ->
                rouletteOperatorsAdapter.submitList(nonNullRouletteOperators)
            }
        })

        rouletteViewModel?.rollingOperatorsAndWinner?.observe(this, { rollingOperatorsAndWinner ->
            rollingOperatorsAndWinner?.let { nonNullRollingOperatorsAndWinner ->
                activity?.run {
                    mainViewModel.winnerCandidates.value = nonNullRollingOperatorsAndWinner.first
                    val action = RouletteFragmentDirections.actionRouletteFragmentToRouletteResultFragment(nonNullRollingOperatorsAndWinner.second)
                    if (findNavController().currentDestination?.id == R.id.rouletteFragment)
                        findNavController().navigate(action)
                }
            }
        })

        rouletteViewModel?.rollLink?.observe(this) {
            it?.run uri@ {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, this@uri.toString())
                    type = "text/plain"
                }

                try {
                    val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_with))
                    startActivity(shareIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onLiveDataObserversSet() {

    }

    private fun Bundle.restoreStateIfNeed() {
        searchQuery = this.getString(ROULETTE_SEARCH_QUERY_KEY, null)
    }
}