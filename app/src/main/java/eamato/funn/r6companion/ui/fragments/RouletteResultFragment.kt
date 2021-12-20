package eamato.funn.r6companion.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.adapters.recycler_view_adapters.SimpleOperatorsAdapter
import eamato.funn.r6companion.R
import eamato.funn.r6companion.databinding.FragmentRouletteResultBinding
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.glide.GlideApp
import eamato.funn.r6companion.utils.glide.ImageResizeTransformation
import eamato.funn.r6companion.utils.recyclerview.*
import eamato.funn.r6companion.viewmodels.RouletteResultPacketOpeningCommonViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.io.File

private const val SCREEN_NAME = "Roulette result screen"

class RouletteResultFragment : BaseFragment() {

    private var fragmentRouletteResultBinding: FragmentRouletteResultBinding? = null

    private val compositeDisposable = CompositeDisposable()

    private val simpleOperatorsAdapter: SimpleOperatorsAdapter by lazy {
        SimpleOperatorsAdapter()
    }

    private val packetOpeningFragment = PacketOpeningFragment()

    private val rouletteResultPacketOpeningCommonViewModel: RouletteResultPacketOpeningCommonViewModel by lazy {
        ViewModelProvider(this).get(RouletteResultPacketOpeningCommonViewModel::class.java)
    }

    private var autoScroller: AutoScroller<RecyclerView.LayoutManager, RouletteOperator, SimpleOperatorsAdapter.SimpleOperatorsViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentRouletteResultBinding = FragmentRouletteResultBinding.inflate(inflater, container, false)
        return fragmentRouletteResultBinding?.root
    }

    override fun onPause() {
        autoScroller?.stopAutoScroll()

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_roulette_result, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.screen_and_share)?.isVisible = rouletteResultPacketOpeningCommonViewModel.isOpenPackDone.value ?: false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.screen_and_share -> {
                context?.let { nonNullContext ->
                    item.isVisible = false

                    val screen = File(nonNullContext.filesDir, "winner_screen.jpeg")

                    val handlerThread = HandlerThread("Background looper")
                    if (!handlerThread.isAlive)
                        handlerThread.start()
                    val backgroundLooper = handlerThread.looper

                    val displayMetrics = DisplayMetrics()
                    ContextCompat.getSystemService(nonNullContext, WindowManager::class.java)
                        ?.defaultDisplay
                        ?.getMetrics(displayMetrics)

                    val window = activity?.window
                    val screenshotView = window?.decorView ?: fragmentRouletteResultBinding?.clRoot
                    screenshotView?.let { nonNullView ->
                        compositeDisposable.add(
                            createScreenshotAndGetItsUri(nonNullView, window, screen, displayMetrics)
                                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                                .observeOn(AndroidSchedulers.mainThread())
                                .doAfterTerminate {
                                    handlerThread.quit()
                                    item.isVisible = true
                                }
                                .subscribe({
                                    try {
                                        val screenUri = FileProvider.getUriForFile(
                                            nonNullContext,
                                            nonNullContext.applicationContext.packageName + ".provider",
                                            it
                                        )
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            type = "image/jpeg"
                                            putExtra(Intent.EXTRA_STREAM, screenUri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Share error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }, {
                                    Toast.makeText(context, "Create screenshot error: ${it.message}", Toast.LENGTH_SHORT).show()
                                })
                        )
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        rouletteResultPacketOpeningCommonViewModel.isOpenPackDone.observe(this, {
            it?.let {
                activity?.invalidateOptionsMenu()
                changePacketOpeningVisibility(!it)
                if (it)
                    fragmentRouletteResultBinding?.clWinner?.visibility = View.VISIBLE
                else
                    fragmentRouletteResultBinding?.clWinner?.visibility = View.GONE
            }
        })

        mainViewModel.winnerCandidates.observe(this, {
            it?.let {
                initSimpleOperatorsList(it.toMutableList())
            }
        })
    }

    override fun onLiveDataObserversSet() {

    }

    private fun changePacketOpeningVisibility(isVisible: Boolean) {
        val transaction = childFragmentManager.beginTransaction()
        if (isVisible)
            transaction.replace(R.id.fragment_packet_opening, packetOpeningFragment, PacketOpeningFragment.TAG)
        else
            transaction.remove(packetOpeningFragment)
        transaction.commit()
    }

    private fun initSimpleOperatorsList(winnerCandidates: MutableList<RouletteOperator>) {
        fragmentRouletteResultBinding?.pbWaiting?.show()

        arguments?.let { nonNullArguments ->
            val arguments = RouletteResultFragmentArgs.fromBundle(nonNullArguments)
            val winner = arguments.rollingWinner

            fragmentRouletteResultBinding?.tvWinnerName?.text = winner.name

            fragmentRouletteResultBinding?.ivWinnerImage?.let { nonNullImageView ->
                GlideApp.with(nonNullImageView)
                    .load(winner.imgLink)
                    .override(WINNER_OPERATOR_IMAGE_WIDTH, WINNER_OPERATOR_IMAGE_HEIGHT)
                    .transform(ImageResizeTransformation(WINNER_OPERATOR_IMAGE_WIDTH, WINNER_OPERATOR_IMAGE_HEIGHT))
                    .placeholder(R.drawable.transparent_300)
                    .error(R.drawable.no_data_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .listener(object : IDoAfterTerminateGlide {
                        override fun doAfterTerminate() {
                            fragmentRouletteResultBinding?.pbWaiting?.hide()
                        }
                    })
                    .dontAnimate()
                    .into(nonNullImageView)
            }

            fragmentRouletteResultBinding?.rvRollingOperators?.setHasFixedSize(true)
            fragmentRouletteResultBinding?.rvRollingOperators?.adapter = simpleOperatorsAdapter

            when (activity?.resources?.configuration?.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    val layoutManager =
                        CustomLinearLayoutManager(
                            context,
                            RecyclerView.HORIZONTAL,
                            false,
                            AutoScroller.scrollToPeriod
                        )
                    val infinityScrollListener = InfinityLinearScrollListener(
                        layoutManager,
                        winnerCandidates
                    )
                    val autoScroller =
                        LinearAutoScroller(
                            fragmentRouletteResultBinding?.rvRollingOperators,
                            simpleOperatorsAdapter,
                            layoutManager
                        )

                    Triple(layoutManager, infinityScrollListener, autoScroller)
                }
                Configuration.ORIENTATION_LANDSCAPE -> {
                    val layoutManager =
                        CustomGridLayoutManager(
                            context,
                            3,
                            AutoScroller.scrollToPeriod
                        )
                    val infinityScrollListener = InfinityGridScrollListener(
                        layoutManager,
                        winnerCandidates
                    )
                    val autoScroller = GridAutoScroller(
                        fragmentRouletteResultBinding?.rvRollingOperators,
                        simpleOperatorsAdapter,
                        layoutManager
                    )

                    Triple(layoutManager, infinityScrollListener, autoScroller)
                }
                else -> {
                    val layoutManager =
                        CustomLinearLayoutManager(
                            context,
                            RecyclerView.HORIZONTAL,
                            false,
                            AutoScroller.scrollToPeriod
                        )
                    val infinityScrollListener = InfinityLinearScrollListener(
                        layoutManager,
                        winnerCandidates
                    )
                    val autoScroller =
                        LinearAutoScroller(
                            fragmentRouletteResultBinding?.rvRollingOperators,
                            simpleOperatorsAdapter,
                            layoutManager
                        )

                    Triple(layoutManager, infinityScrollListener, autoScroller)
                }
            }.also {
                autoScroller = it.third
                fragmentRouletteResultBinding?.rvRollingOperators?.layoutManager = it.first
                simpleOperatorsAdapter.submitList(winnerCandidates)
                fragmentRouletteResultBinding?.rvRollingOperators.setMyOnScrollListener(it.second)
                fragmentRouletteResultBinding?.rvRollingOperators?.post {
                    it.second.prepareData()
                    it.third.startAutoScrollSTEEndless(startPosition = it.third.getCurrentPosition()/*, initialDelay = 500L*/)
                }
            }

        } ?: fragmentRouletteResultBinding?.pbWaiting?.hide()
    }

}