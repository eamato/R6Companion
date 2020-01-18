package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.NewsAdapter
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.LiveDataStatuses
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import eamato.funn.r6companion.utils.setMyOnScrollListener
import eamato.funn.r6companion.utils.setOnItemClickListener
import eamato.funn.r6companion.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

private const val SCREEN_NAME = "Home screen"

class HomeFragment : BaseFragment() {

    private val newsAdapter = NewsAdapter()

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private val myScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (
                newState == RecyclerView.SCROLL_STATE_DRAGGING &&
                !recyclerView.canScrollVertically(1) &&
                homeViewModel.hasDataSourceError()
            )
                onDataSourceErrorOccurred()
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            fab_scroll_to_top?.let { nonNullFabScrollToTop ->
                if (dy >= 0 && nonNullFabScrollToTop.isShown)
                    nonNullFabScrollToTop.hide()
                else if (!recyclerView.canScrollVertically(-1))
                    nonNullFabScrollToTop.hide()
                else
                    nonNullFabScrollToTop.show()
            }
        }
    }

    private val onNewsClickListener: RecyclerViewItemClickListener by lazy {
        RecyclerViewItemClickListener(context, rv_news, object : RecyclerViewItemClickListener.OnItemClickListener {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        srl_news?.setOnRefreshListener {
            homeViewModel.refreshNews()
        }

        rv_news?.setHasFixedSize(true)
        context?.let { nonNullContext ->
            rv_news?.layoutManager = LinearLayoutManager(nonNullContext)
            rv_news?.adapter = newsAdapter
        }
        rv_news.setMyOnScrollListener(myScrollListener)
        rv_news.setOnItemClickListener(onNewsClickListener)

        fab_scroll_to_top?.setOnClickListener {
            rv_news?.scrollToPosition(0)
        }

        clpb_news?.hide()
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        homeViewModel.news.observe(this, Observer {
            it?.let {
                newsAdapter.submitList(it)
            }
        })

        homeViewModel.requestNewsStatus.observe(this, Observer {
            srl_news?.isRefreshing = false
            when (it) {
                LiveDataStatuses.ERROR -> {
                    onDataSourceErrorOccurred()
                    clpb_news?.hide()
                }
                LiveDataStatuses.WAITING -> {
                    fab_scroll_to_top?.hide()
                    clpb_news?.show()
                }
                else -> clpb_news?.hide()
            }
        })
    }

    override fun onLiveDataObserversSet() {

    }

    private fun onDataSourceErrorOccurred() {
        Snackbar.make(rv_news, R.string.an_error_occurred, Snackbar.LENGTH_SHORT)
            .setAction(R.string.retry) {
                homeViewModel.retry()
            }
            .setAnchorView(mainActivity?.findViewById<View>(R.id.bnv))
            .show()
    }

}
