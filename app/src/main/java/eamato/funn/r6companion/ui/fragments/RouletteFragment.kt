package eamato.funn.r6companion.ui.fragments

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.RouletteOperatorsAdapter
import eamato.funn.r6companion.databinding.FragmentRouletteBinding
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.viewmodels.RouletteViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_roulette.*

class RouletteFragment : BaseFragment(), SearchView.OnQueryTextListener {

    private var searchQuery: String? = ""

    private val compositeDisposable = CompositeDisposable()

    private val rouletteViewModel: RouletteViewModel by lazy {
        ViewModelProviders.of(this).get(RouletteViewModel::class.java)
    }

    private var binding: FragmentRouletteBinding? = null

    private val rouletteOperatorsAdapter: RouletteOperatorsAdapter by lazy {
        RouletteOperatorsAdapter()
    }

    private val allOperatorsRouletteClickListener: RecyclerViewItemClickListener by lazy {
        RecyclerViewItemClickListener(context, rv_all_roulette_operators, object : RecyclerViewItemClickListener.OnItemClickListener {
            override fun onItemClicked(view: View, position: Int) {
                rouletteViewModel.selectUnSelectRouletteOperator(rouletteOperatorsAdapter.getItemAtPosition(position))
            }

            override fun onItemLongClicked(view: View, position: Int) {

            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        rouletteViewModel.visibleRouletteOperators.observe(this, Observer { rouletteOperators ->
            rouletteOperators?.let { nonNullRouletteOperators ->
                rouletteOperatorsAdapter.submitList(nonNullRouletteOperators)
            }
        })

        rouletteViewModel.rollingOperatorsAndWinner.observe(this, Observer { rollingOperatorsAndWinner ->
            rollingOperatorsAndWinner?.let { nonNullRollingOperatorsAndWinner ->
                activity?.run {
                    val action = RouletteFragmentDirections.actionRouletteFragmentToRouletteResultFragment(
                        nonNullRollingOperatorsAndWinner.second, nonNullRollingOperatorsAndWinner.first.toParcelableList()
                    )
                    findNavController(R.id.fragment).navigate(action)
                }
            }
        })

        savedInstanceState?.restoreStateIfNeed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_roulette, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.lifecycleOwner = this
        binding?.rouletteViewModel = rouletteViewModel

        rv_all_roulette_operators.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(context, 3)
        activity?.resources?.configuration?.orientation?.takeIf { it == ORIENTATION_LANDSCAPE }?.run {
            layoutManager = GridLayoutManager(context, 5)
        }
        rv_all_roulette_operators.layoutManager = layoutManager
        rv_all_roulette_operators.adapter = rouletteOperatorsAdapter
        rv_all_roulette_operators.setOnItemClickListener(allOperatorsRouletteClickListener)

        btn_roll.setOnClickListener {
            rouletteViewModel.roll()
        }
    }

    override fun onResume() {
        super.onResume()

        if (rouletteViewModel.visibleRouletteOperators.value.isNullOrEmpty()) {
            context?.let { nonNullContext ->
                rouletteViewModel.getAllOperators(nonNullContext.assets, PreferenceManager.getDefaultSharedPreferences(nonNullContext))
            }
        }
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

        menu.findItem(R.id.save_selected)?.isVisible = rouletteViewModel.areThereAnySelectedOperators()

        val disposable = PreferenceManager.getDefaultSharedPreferences(context).areThereSavedSelectedOperators()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.alphabetic_sort_ascending -> {
                rouletteViewModel.sortByNameAscending {
                    rv_all_roulette_operators.smoothScrollToPosition(0)
                }
                true
            }
            R.id.alphabetic_sort_descending -> {
                rouletteViewModel.sortByNameDescending {
                    rv_all_roulette_operators.smoothScrollToPosition(0)
                }
                true
            }
            R.id.select_all -> {
                rouletteViewModel.selectAll()
                true
            }
            R.id.un_select_all -> {
                rouletteViewModel.unSelectAll()
                true
            }
            R.id.save_selected -> {
                context?.let { nonNullContext ->
                    AlertDialog.Builder(nonNullContext)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.attention)
                        .setMessage(R.string.save_confirmation_message)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            rouletteViewModel.saveSelectedOperators(
                                PreferenceManager.getDefaultSharedPreferences(context)
                            ) {
                                activity?.invalidateOptionsMenu()
                            }
                        }
                        .create()
                        .show()
                }
                true
            }
            R.id.delete_saved -> {
                context?.let { nonNullContext ->
                    AlertDialog.Builder(nonNullContext)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.attention)
                        .setMessage(R.string.delete_saved_confirmation_message)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            rouletteViewModel.deleteSavedSelectedOperators(
                                PreferenceManager.getDefaultSharedPreferences(context)
                            ) {
                                activity?.invalidateOptionsMenu()
                            }
                        }
                        .create()
                        .show()
                }
                true
            }
            R.id.restore_saved -> {
                context?.let { nonNullContext ->
                    rouletteViewModel.selectPreviouslySelectedOperators(
                        PreferenceManager.getDefaultSharedPreferences(nonNullContext)
                    )
                }
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
            rouletteViewModel.filter(nonNullNewText)
        } ?: rouletteViewModel.restore()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchQuery = query
        query?.let { nonNullQuery ->
            rouletteViewModel.filter(nonNullQuery)
        } ?: rouletteViewModel.restore()
        return true
    }

    private fun Bundle.restoreStateIfNeed() {
        searchQuery = this.getString(ROULETTE_SEARCH_QUERY_KEY, null)
    }

}