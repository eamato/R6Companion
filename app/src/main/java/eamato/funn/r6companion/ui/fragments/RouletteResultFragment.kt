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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.adapters.SimpleOperatorsAdapter
import kotlinx.android.synthetic.main.fragment_roulette_result.*
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.glide.GlideApp
import eamato.funn.r6companion.utils.glide.ImageResizeTransformation
import eamato.funn.r6companion.utils.recyclerview.*
import eamato.funn.r6companion.viewmodels.RouletteResultPacketOpeningCommonViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_roulette_result.pb_waiting
import java.io.File

// TODO enable disable screenshoting on result ready
class RouletteResultFragment : BaseFragment() {

    private val compositeDisposable = CompositeDisposable()

    private val simpleOperatorsAdapter: SimpleOperatorsAdapter by lazy {
        SimpleOperatorsAdapter()
    }

    private val rouletteResultPacketOpeningCommonViewModel: RouletteResultPacketOpeningCommonViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(RouletteResultPacketOpeningCommonViewModel::class.java)
        } ?: throw Exception("Invalid activity")
    }

    private var autoScroller: AutoScroller<RecyclerView.LayoutManager, RouletteOperator, SimpleOperatorsAdapter.SimpleOperatorsViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_roulette_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb_waiting.show()

        arguments?.let { nonNullArguments ->
            val arguments = RouletteResultFragmentArgs.fromBundle(nonNullArguments)
            val winnerCandidates = ArrayList(arguments.rollingOperators)
            val winner = arguments.rollingWinner

            rouletteResultPacketOpeningCommonViewModel.winner.value = winner

            tv_winner_name.text = winner.name

            context?.let { nonNullContext ->
                GlideApp.with(nonNullContext)
                    .load(winner.imgLink)
                    .override(WINNER_OPERATOR_IMAGE_WIDTH, WINNER_OPERATOR_IMAGE_HEIGHT)
                    .transform(ImageResizeTransformation(WINNER_OPERATOR_IMAGE_WIDTH, WINNER_OPERATOR_IMAGE_HEIGHT))
                    .placeholder(R.drawable.transparent_300)
                    .error(R.drawable.no_data_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .listener(object : IDoAfterTerminateGlide {
                        override fun doAfterTerminate() {
                            pb_waiting.hide()
                        }
                    })
                    .dontAnimate()
                    .into(iv_winner_image)
            }

            rv_rolling_operators.setHasFixedSize(true)
            rv_rolling_operators.adapter = simpleOperatorsAdapter

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
                            rv_rolling_operators,
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
                        rv_rolling_operators,
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
                            rv_rolling_operators,
                            simpleOperatorsAdapter,
                            layoutManager
                        )

                    Triple(layoutManager, infinityScrollListener, autoScroller)
                }
            }.also {
                autoScroller = it.third
                rv_rolling_operators.layoutManager = it.first
                simpleOperatorsAdapter.submitList(winnerCandidates)
                rv_rolling_operators.setMyOnScrollListener(it.second)
                rv_rolling_operators?.post {
                    it.second.prepareData()
                    it.third.startAutoScrollSTEEndless(startPosition = it.third.getCurrentPosition()/*, initialDelay = 500L*/)
                }
            }

        } ?: pb_waiting.hide()

        cl_winner.visibility = View.GONE
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.screen_and_share -> {
                context?.let { nonNullContext ->
                    item.isEnabled = false

                    val screen = File(nonNullContext.filesDir, "winner_screen.jpeg")

                    val handlerThread = HandlerThread("Background looper")
                    if (!handlerThread.isAlive)
                        handlerThread.start()
                    val backgroundLooper = handlerThread.looper

                    val displayMetrics = DisplayMetrics()
                    ContextCompat.getSystemService(nonNullContext, WindowManager::class.java)
                        ?.defaultDisplay
                        ?.getMetrics(displayMetrics)

                    compositeDisposable.add(
                        createScreenshotAndGetItsUri(cl_root, activity?.window, screen, displayMetrics)
                            .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterTerminate {
                                handlerThread.quit()
                                item.isEnabled = true
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}