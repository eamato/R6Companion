package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.NewsAdapterV2
import eamato.funn.r6companion.databinding.FragmentHomeBinding
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import eamato.funn.r6companion.utils.setMyOnScrollListener
import eamato.funn.r6companion.utils.setOnItemClickListener
import eamato.funn.r6companion.viewmodels.HomeViewModelV2
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val SCREEN_NAME = "Home screen"

class HomeFragment : BaseFragment() {

    private var job: Job? = null
    private var wasErrorOccurred: Boolean = false

    private val newsAdapter = NewsAdapterV2()

    private val homeViewModel: HomeViewModelV2? by viewModels()

    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
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
            binding?.fabScrollToTop?.let { nonNullFabScrollToTop ->
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
        binding?.rvNews?.let { nonNullView ->
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

        binding?.srlNews?.setOnRefreshListener {
            newsAdapter.refresh()
        }
        binding?.rvNews?.setHasFixedSize(true)
        context?.let { nonNullContext ->
            binding?.rvNews?.layoutManager = LinearLayoutManager(nonNullContext)
        }
        binding?.rvNews?.adapter = newsAdapter
        binding?.rvNews.setMyOnScrollListener(myScrollListener)
        onNewsClickListener?.let { nonNullListener ->
            binding?.rvNews.setOnItemClickListener(nonNullListener)
        }

        binding?.fabScrollToTop?.setOnClickListener {
            binding?.rvNews?.scrollToPosition(0)
        }

        lifecycleScope.launch {
            newsAdapter.loadStateFlow.collectLatest {
                loadListener(it)
            }
        }
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        getUpdates(getString(R.string.news_request_lang))
    }

    override fun onLiveDataObserversSet() {

    }

    private fun getUpdates(newsLocale: String) {
        job?.cancel()
        job = lifecycleScope.launch {
            homeViewModel?.getUpdates(newsLocale)?.collect {
                newsAdapter.submitData(it)
            }
        }
    }

    private fun loadListener(combinedLoadStates: CombinedLoadStates) {
        Log.d("listener", "$combinedLoadStates")
        if (combinedLoadStates.source.refresh is LoadState.Loading) {
            binding?.clpbNews?.show()
            binding?.fabScrollToTop?.hide()
        } else {
            binding?.clpbNews?.hide()
            binding?.srlNews?.isRefreshing = false
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
        binding?.rvNews?.let { nonNullView ->
            Snackbar.make(nonNullView, R.string.an_error_occurred, Snackbar.LENGTH_SHORT)
                .setAction(R.string.retry) {
                    newsAdapter.retry()
                }
                .setAnchorView(mainActivity?.findViewById(R.id.bnv))
                .show()
        }
    }

}
