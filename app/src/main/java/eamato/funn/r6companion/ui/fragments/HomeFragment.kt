package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.NewsAdapter
import eamato.funn.r6companion.databinding.FragmentHomeBinding
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.viewmodels.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

private const val SCREEN_NAME = "Home screen"

class HomeFragment : BaseFragment() {

    private var buttonsAndValues: List<Pair<View?, String?>>? = null
    private val newsCategories = NEWS_CATEGORIES
        .map {
            val toggled = it.second == null
            TogglingObject(it, toggled)
        }
    private var job: Job? = null
    private var wasErrorOccurred: Boolean = false

    private val newsAdapter = NewsAdapter(::onUpdateClicked, ::onUpdateFavouriteToggle)

    private val homeViewModel: HomeViewModel? by lazy {
        val pref = context?.let { PreferenceManager.getDefaultSharedPreferences(it) } ?: return@lazy null
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(pref) as T
                }
            }
        )[HomeViewModel::class.java]
    }

    private var fragmentHomeBinding: FragmentHomeBinding? = null

    private val newsCategoriesClickListener = View.OnClickListener { view ->
        buttonsAndValues
            ?.find { it.first == view }
            ?.second
            .let { value ->
                changeNewsCategory(value)
            }
    }

    private var myScrollListener: RecyclerView.OnScrollListener? = null

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

        homeViewModel?.additionalData?.observe(viewLifecycleOwner) { data ->
            Log.d("Ola", "Additional data = $data")
        }

        return fragmentHomeBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (myScrollListener == null) {
            myScrollListener = object : RecyclerView.OnScrollListener() {
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
        }

        buttonsAndValues = listOf(
            fragmentHomeBinding?.btnNewsCategoryAll to null,
            fragmentHomeBinding?.btnNewsCategoryEsport to NEWS_CATEGORIES_FILTER_PARAM_ESPORTS_VALUE,
            fragmentHomeBinding?.btnNewsCategoryGameUpdates to NEWS_CATEGORIES_FILTER_PARAM_GAME_UPDATES_VALUE,
            fragmentHomeBinding?.btnNewsCategoryCommunity to NEWS_CATEGORIES_FILTER_PARAM_COMMUNITY_VALUE,
            fragmentHomeBinding?.btnNewsCategoryPatchNotes to NEWS_CATEGORIES_FILTER_PARAM_PATCH_NOTES_VALUE,
            fragmentHomeBinding?.btnNewsCategoryStore to NEWS_CATEGORIES_FILTER_PARAM_STORE_VALUE
        )

        updateButtonsWithNewsCategory()

        fragmentHomeBinding?.flowNewsCategories?.referencedIds?.forEach {
            view.findViewById<Button>(it).setOnClickListener(newsCategoriesClickListener)
        }
        fragmentHomeBinding?.srlNews?.setOnRefreshListener {
            newsAdapter.refresh()
        }
        fragmentHomeBinding?.rvNews?.setHasFixedSize(true)
        context?.let { nonNullContext ->
            fragmentHomeBinding?.rvNews?.layoutManager = LinearLayoutManager(nonNullContext)
        }
        fragmentHomeBinding?.rvNews?.adapter = newsAdapter
        myScrollListener?.run { fragmentHomeBinding?.rvNews.setMyOnScrollListener(this) }

        fragmentHomeBinding?.fabScrollToTop?.setOnClickListener {
            fragmentHomeBinding?.rvNews?.scrollToPosition(0)
        }

        lifecycleScope.launchWhenResumed {
            newsAdapter.loadStateFlow.collectLatest {
                loadListener(it)
            }
        }
    }

    override fun onDestroyView() {
        fragmentHomeBinding?.rvNews?.adapter = null
        myScrollListener?.run { fragmentHomeBinding?.rvNews?.removeOnScrollListener(this) }

        super.onDestroyView()

        fragmentHomeBinding = null

        buttonsAndValues = null

        myScrollListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.language_en -> {
                getUpdates(
                    ENGLISH_NEWS_LOCALE,
                    newsCategories
                        .find { togglingObject -> togglingObject.toggled }
                        ?.data
                        ?.second
                )
                true
            }
            R.id.language_ru -> {
                getUpdates(
                    RUSSIAN_NEWS_LOCALE,
                    newsCategories
                        .find { togglingObject -> togglingObject.toggled }
                        ?.data
                        ?.second
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        getUpdates(
            DEFAULT_NEWS_LOCALE,
            newsCategories
                .find { togglingObject -> togglingObject.toggled }
                ?.data
                ?.second
        )
    }

    override fun onLiveDataObserversSet() {

    }

    private fun getUpdates(newsLocale: String, newsCategory: String?) {
        job?.cancel()
        job = lifecycleScope.launchWhenCreated {
            homeViewModel?.getUpdates(newsLocale, newsCategory)?.collect {
                newsAdapter.submitData(it)
            }
        }
    }

    private fun loadListener(combinedLoadStates: CombinedLoadStates) {
        Log.d("listener", "$combinedLoadStates")
        if (combinedLoadStates.source.refresh is LoadState.Loading) {
            fragmentHomeBinding?.clpbNews?.show()
            fragmentHomeBinding?.fabScrollToTop?.hide()
            buttonsAndValues?.forEach { it.first?.isEnabled = false }
        } else {
            fragmentHomeBinding?.clpbNews?.hide()
            fragmentHomeBinding?.srlNews?.isRefreshing = false
            buttonsAndValues?.forEach { it.first?.isEnabled = true }
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

    private fun changeNewsCategory(newCategory: String?) {
        val currentNewsCategory = newsCategories
            .find { togglingObject -> togglingObject.toggled }
            ?.data
            ?.second

        if (currentNewsCategory == newCategory)
            return

        newsCategories.forEach {
            it.toggled = it.data.second == newCategory
        }

        val newNewsCategory = newsCategories
            .find { togglingObject -> togglingObject.toggled }
            ?.data
            ?.second

        updateButtonsWithNewsCategory()

        getUpdates(homeViewModel?.currentNewsLocale ?: DEFAULT_NEWS_LOCALE, newNewsCategory)
    }

    private fun updateButtonsWithNewsCategory() {
        val currentNewsCategory = newsCategories
            .find { togglingObject -> togglingObject.toggled }
            ?.data
            ?.second

        buttonsAndValues?.forEach {
            it.first?.isSelected = it.second == currentNewsCategory
        }
    }

    private fun onUpdateFavouriteToggle(update: NewsDataMixedWithAds, position: Int) {
        context?.run {
            val newsData = update.newsData ?: return
            val newIsFavourite = newsData.isFavourite.not()
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            if (newIsFavourite)
                pref.saveFavouriteUpdate(newsData)
            else
                pref.removeFavouriteUpdate(newsData)
        }

        newsAdapter.refresh()
    }

    private fun onUpdateClicked(update: NewsDataMixedWithAds) {
        update.newsData?.run {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToNewsDetailsFragment(this)
            )
        }
    }
}
