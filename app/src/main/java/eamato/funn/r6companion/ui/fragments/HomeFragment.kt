package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.NewsAdapter
import eamato.funn.r6companion.databinding.FragmentHomeBinding
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import eamato.funn.r6companion.viewmodels.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

private const val SCREEN_NAME = "Home screen"

class HomeFragment : BaseFragment() {

    private var job: Job? = null
    private var wasErrorOccurred: Boolean = false

    private val newsAdapter = NewsAdapter()

    private val homeViewModel: HomeViewModel? by viewModels()

    private var fragmentHomeBinding: FragmentHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return fragmentHomeBinding?.root
    }

    private val myScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (
                newState == RecyclerView.SCROLL_STATE_DRAGGING &&
                !recyclerView.canScrollVertically(1) &&
                wasErrorOccurred
            )
                onDataSourceErrorOccurred()
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            fragmentHomeBinding?.fabScrollToTop?.let { nonNullFabScrollToTop ->
                if (dy >= 0 && nonNullFabScrollToTop.isShown)
                    nonNullFabScrollToTop.hide()
                else if (!recyclerView.canScrollVertically(-1))
                    nonNullFabScrollToTop.hide()
                else
                    nonNullFabScrollToTop.show()
            }
        }
    }

    private val onNewsClickListener: RecyclerViewItemClickListener? by lazy {
        fragmentHomeBinding?.rvNews?.let { nonNullView ->
            RecyclerViewItemClickListener(context, nonNullView, object : RecyclerViewItemClickListener.OnItemClickListener {
                override fun onItemClicked(view: View, position: Int) {
                    newsAdapter.getItemAtPosition(position)?.let { nonNullSelectedNews ->
                        nonNullSelectedNews.newsData?.let {
                            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewsDetailsFragment(it))
                        }
                    }
                }

                override fun onItemLongClicked(view: View, position: Int) {

                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentHomeBinding?.srlNews?.setOnRefreshListener {
            newsAdapter.refresh()
        }
        fragmentHomeBinding?.rvNews?.setHasFixedSize(true)
        context?.let { nonNullContext ->
            fragmentHomeBinding?.rvNews?.layoutManager = LinearLayoutManager(nonNullContext)
        }
        fragmentHomeBinding?.rvNews?.adapter = newsAdapter
        fragmentHomeBinding?.rvNews.setMyOnScrollListener(myScrollListener)
        onNewsClickListener?.let { nonNullListener ->
            fragmentHomeBinding?.rvNews.setOnItemClickListener(nonNullListener)
        }

        fragmentHomeBinding?.fabScrollToTop?.setOnClickListener {
            fragmentHomeBinding?.rvNews?.scrollToPosition(0)
        }

        lifecycleScope.launchWhenResumed {
            newsAdapter.loadStateFlow.collectLatest {
                loadListener(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.language_en -> {
                getUpdates(ENGLISH_NEWS_LOCALE)
                true
            }
            R.id.language_ru -> {
                getUpdates(RUSSIAN_NEWS_LOCALE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        getUpdates(DEFAULT_NEWS_LOCALE)
    }

    override fun onLiveDataObserversSet() {

    }

    private fun getUpdates(newsLocale: String) {
        job?.cancel()
        job = lifecycleScope.launchWhenCreated {
            homeViewModel?.getUpdates(newsLocale)?.collect {
                newsAdapter.submitData(it)
            }
        }
    }

    private fun loadListener(combinedLoadStates: CombinedLoadStates) {
        Log.d("listener", "$combinedLoadStates")
        if (combinedLoadStates.source.refresh is LoadState.Loading) {
            fragmentHomeBinding?.clpbNews?.show()
            fragmentHomeBinding?.fabScrollToTop?.hide()
        } else {
            fragmentHomeBinding?.clpbNews?.hide()
            fragmentHomeBinding?.srlNews?.isRefreshing = false
        }

        val error = combinedLoadStates.source.append as? LoadState.Error
            ?: combinedLoadStates.source.prepend as? LoadState.Error
            ?: combinedLoadStates.source.refresh as? LoadState.Error
            ?: combinedLoadStates.append as? LoadState.Error
            ?: combinedLoadStates.prepend as? LoadState.Error
            ?: combinedLoadStates.refresh as? LoadState.Error

        wasErrorOccurred = if (error != null) {
            onDataSourceErrorOccurred()
            true
        } else {
            false
        }
    }

    private fun onDataSourceErrorOccurred() {
        fragmentHomeBinding?.rvNews?.let { nonNullView ->
            val snackbar = Snackbar.make(nonNullView, R.string.an_error_occurred, Snackbar.LENGTH_SHORT)
            snackbar.setAction(R.string.retry) {
                    newsAdapter.retry()
                }
            snackbar.anchorView = mainActivity?.findViewById(R.id.bnv)
            snackbar.show()
        }
    }

}
