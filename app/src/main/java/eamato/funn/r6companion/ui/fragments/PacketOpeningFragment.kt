package eamato.funn.r6companion.ui.fragments

import android.app.ActivityManager
import android.content.res.Resources
import android.graphics.*
import android.os.*
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.BitmapCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import eamato.funn.r6companion.R
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.open_pack.*
import eamato.funn.r6companion.viewmodels.RouletteResultPacketOpeningCommonViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_packet_opening.*

class PacketOpeningFragment : BaseFragment(), SurfaceHolder.Callback {

    companion object {
        const val TAG = "pack_opening_fragment"
    }

    private val compositeDisposable = CompositeDisposable()

    private val backgroundColor: Int by lazy {
        context?.let { nonNullContext ->
            ContextCompat.getColor(nonNullContext, R.color.colorBackground)
        } ?: Color.WHITE
    }

    private val res: Resources by lazy {
        resources
    }

    private val idlePacketAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.idle_packet_animation)
    }

    private var shouldDispatchTouchEvent = true

    private val rouletteResultPacketOpeningCommonViewModel: RouletteResultPacketOpeningCommonViewModel by lazy {
        requireParentFragment().run {
            ViewModelProviders.of(this).get(RouletteResultPacketOpeningCommonViewModel::class.java)
        }
    }

    private lateinit var canvasSize: MySize

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_packet_opening, container, false)
    }

    override fun onStop() {
        super.onStop()

        compositeDisposable.clear()

        sv_canvas.holder.removeCallback(this)
        sv_canvas.clearAnimation()
        sv_canvas.setOnTouchListener(null)
    }

    override fun onStart() {
        super.onStart()

        sv_canvas.holder.addCallback(this)
        sv_canvas.startAnimation(idlePacketAnimation)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        compositeDisposable.clear()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        holder?.let { nonNullHolder ->
            val f = Flowable
                .just(preparePlayer(nonNullHolder))
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { player2 ->
                    player2.middlePlayRoadPlayer.playRoad.playbackStatus.observe(this, Observer {
                        when (it) {
                            PlaybackStatus.PLAYING -> {
                                shouldDispatchTouchEvent = false
                                player2.topPlayRoadPlayer.isFrameVisible.set(false)

                                sv_canvas.clearAnimation()
                            }
                            PlaybackStatus.PAUSED -> {
                                player2.middlePlayRoadPlayer.playbackMode.set(PlaybackMode.STRAIGHT)
                                shouldDispatchTouchEvent = true
                                player2.topPlayRoadPlayer.isFrameVisible.set(true)

                                sv_canvas.startAnimation(idlePacketAnimation)
                            }
                            PlaybackStatus.STOPPED -> {
                                player2.middlePlayRoadPlayer.playbackMode.set(PlaybackMode.STRAIGHT)
                                shouldDispatchTouchEvent = true
                                player2.topPlayRoadPlayer.isFrameVisible.set(true)

                                sv_canvas.startAnimation(idlePacketAnimation)
                            }
                            PlaybackStatus.PLAYINGFBF -> {
                                shouldDispatchTouchEvent = true
                                player2.topPlayRoadPlayer.isFrameVisible.set(false)

                                sv_canvas.clearAnimation()
                            }
                            else -> {

                            }
                        }
                    })

                    pb_waiting?.hide()

                    val myGestureDetectorImplementation2 =
                        MyGestureDetectorImplementation2(player2, canvasSize)
                    val gestureDetector =
                        GestureDetector(context, myGestureDetectorImplementation2)

                    sv_canvas.setOnTouchListener { _, event ->
                        if (shouldDispatchTouchEvent) {
                            gestureDetector.onTouchEvent(event)
                            if (event.action == MotionEvent.ACTION_UP && myGestureDetectorImplementation2.isScrollDetected) {
                                myGestureDetectorImplementation2.isScrollDetected = false
                                player2.middlePlayRoadPlayer.playbackMode.set(PlaybackMode.REVERSED)
                                player2.middlePlayRoadPlayer.playRoad.playbackStatus.value =
                                    PlaybackStatus.PLAYING
                            }
                        }
                        true
                    }

                    player2.playback
                }
                .subscribe({
                    draw(nonNullHolder, it.bottomLayer, it.centerLayer, it.topLayer, backgroundColor)
                }, {
                    it.printStackTrace()
                })
            compositeDisposable.add(f)
        }
    }

    private fun preparePlayer(surfaceHolder: SurfaceHolder): Player2? {
        runOnUiThread { pb_waiting?.show() }
        canvasSize = surfaceHolder.getCanvasMySize()
        draw(
            surfaceHolder,
            BitmapFactory.decodeResource(res, R.drawable.under_glow_00000).getScaledBitmapToSaveSize(canvasSize.width, canvasSize.height),
            BitmapFactory.decodeResource(res, R.drawable.alpha_pack_00000).getScaledBitmapToSaveSize(canvasSize.width, canvasSize.height),
            null,
            backgroundColor
        )
        val playlist = initPacketData()
        return Player2(playlist) {
            runOnUiThread {
                sv_canvas.setOnTouchListener(null)
                compositeDisposable.clear()
                rouletteResultPacketOpeningCommonViewModel.isOpenPackDone.value = true
            }
        }
    }

    private fun runOnUiThread(doWhat: () -> Unit) {
        Handler(Looper.getMainLooper()).post {
            doWhat.invoke()
        }
    }

    private fun initPacketData(): Playlist {
        val alphaPackResources = arrayOf(
            R.drawable.alpha_pack_00000,
            R.drawable.alpha_pack_00001,
            R.drawable.alpha_pack_00002,
            R.drawable.alpha_pack_00003,
            R.drawable.alpha_pack_00004,
            R.drawable.alpha_pack_00005,
            R.drawable.alpha_pack_00006,
            R.drawable.alpha_pack_00007,
            R.drawable.alpha_pack_00008,
            R.drawable.alpha_pack_00009,
            R.drawable.alpha_pack_00010,
            R.drawable.alpha_pack_00011,
            R.drawable.alpha_pack_00012,
            R.drawable.alpha_pack_00013,
            R.drawable.alpha_pack_00014,
            R.drawable.alpha_pack_00015,
            R.drawable.alpha_pack_00016,
            R.drawable.alpha_pack_00017,
            R.drawable.alpha_pack_00018,
            R.drawable.alpha_pack_00019,
            R.drawable.alpha_pack_00020,
            R.drawable.alpha_pack_00021,
            R.drawable.alpha_pack_00022,
            R.drawable.alpha_pack_00023,
            R.drawable.alpha_pack_00024,
            R.drawable.alpha_pack_00025,
            R.drawable.alpha_pack_00026,
            R.drawable.alpha_pack_00027,
            R.drawable.alpha_pack_00028,
            R.drawable.alpha_pack_00029,
            R.drawable.alpha_pack_00030,
            R.drawable.alpha_pack_00031,
            R.drawable.alpha_pack_00032,
            R.drawable.alpha_pack_00033,
            R.drawable.alpha_pack_00034,
            R.drawable.alpha_pack_00035,
            R.drawable.alpha_pack_00036,
            R.drawable.alpha_pack_00037,
            R.drawable.alpha_pack_00038,
            R.drawable.alpha_pack_00039,
            R.drawable.alpha_pack_00040,
            R.drawable.alpha_pack_00041,
            R.drawable.alpha_pack_00042,
            R.drawable.alpha_pack_00043,
            R.drawable.alpha_pack_00044,
            R.drawable.alpha_pack_00045,
            R.drawable.alpha_pack_00046,
            R.drawable.alpha_pack_00047,
            R.drawable.alpha_pack_00048,
            R.drawable.alpha_pack_00049,
            R.drawable.alpha_pack_00050,
            R.drawable.alpha_pack_00051,
            R.drawable.alpha_pack_00052,
            R.drawable.alpha_pack_00053,
            R.drawable.alpha_pack_00054
        ) // 55
        val glowResources = arrayOf(
            R.drawable.under_glow_00000,
            R.drawable.under_glow_00001,
            R.drawable.under_glow_00002,
            R.drawable.under_glow_00003,
            R.drawable.under_glow_00004,
            R.drawable.under_glow_00005,
            R.drawable.under_glow_00006,
            R.drawable.under_glow_00007,
            R.drawable.under_glow_00008,
            R.drawable.under_glow_00009,
            R.drawable.under_glow_00010,
            R.drawable.under_glow_00011,
            R.drawable.under_glow_00012,
            R.drawable.under_glow_00013,
            R.drawable.under_glow_00014,
            R.drawable.under_glow_00015,
            R.drawable.under_glow_00016,
            R.drawable.under_glow_00017,
            R.drawable.under_glow_00018,
            R.drawable.under_glow_00019,
            R.drawable.under_glow_00020,
            R.drawable.under_glow_00021,
            R.drawable.under_glow_00022,
            R.drawable.under_glow_00023,
            R.drawable.under_glow_00024,
            R.drawable.under_glow_00025,
            R.drawable.under_glow_00026,
            R.drawable.under_glow_00027,
            R.drawable.under_glow_00028,
            R.drawable.under_glow_00029,
            R.drawable.under_glow_00030,
            R.drawable.under_glow_00031,
            R.drawable.under_glow_00032,
            R.drawable.under_glow_00033,
            R.drawable.under_glow_00034,
            R.drawable.under_glow_00035,
            R.drawable.under_glow_00036,
            R.drawable.under_glow_00037,
            R.drawable.under_glow_00038,
            R.drawable.under_glow_00039,
            R.drawable.under_glow_00040,
            R.drawable.under_glow_00041,
            R.drawable.under_glow_00042,
            R.drawable.under_glow_00043,
            R.drawable.under_glow_00044,
            R.drawable.under_glow_00045,
            R.drawable.under_glow_00046,
            R.drawable.under_glow_00047,
            R.drawable.under_glow_00048,
            R.drawable.under_glow_00049,
            R.drawable.under_glow_00050,
            R.drawable.under_glow_00051,
            R.drawable.under_glow_00052,
            R.drawable.under_glow_00053,
            R.drawable.under_glow_00054,
            R.drawable.under_glow_00055,
            R.drawable.under_glow_00056,
            R.drawable.under_glow_00057,
            R.drawable.under_glow_00058,
            R.drawable.under_glow_00059,
            R.drawable.under_glow_00060,
            R.drawable.under_glow_00061,
            R.drawable.under_glow_00062,
            R.drawable.under_glow_00063,
            R.drawable.under_glow_00064,
            R.drawable.under_glow_00065,
            R.drawable.under_glow_00066,
            R.drawable.under_glow_00067,
            R.drawable.under_glow_00068,
            R.drawable.under_glow_00069,
            R.drawable.under_glow_00070,
            R.drawable.under_glow_00071,
            R.drawable.under_glow_00072,
            R.drawable.under_glow_00073,
            R.drawable.under_glow_00074,
            R.drawable.under_glow_00075,
            R.drawable.under_glow_00076,
            R.drawable.under_glow_00077,
            R.drawable.under_glow_00078,
            R.drawable.under_glow_00079,
            R.drawable.under_glow_00080,
            R.drawable.under_glow_00081,
            R.drawable.under_glow_00082,
            R.drawable.under_glow_00083,
            R.drawable.under_glow_00084,
            R.drawable.under_glow_00085,
            R.drawable.under_glow_00086,
            R.drawable.under_glow_00087,
            R.drawable.under_glow_00088,
            R.drawable.under_glow_00089,
            R.drawable.under_glow_00090,
            R.drawable.under_glow_00091,
            R.drawable.under_glow_00092,
            R.drawable.under_glow_00093,
            R.drawable.under_glow_00094,
            R.drawable.under_glow_00095,
            R.drawable.under_glow_00096,
            R.drawable.under_glow_00097,
            R.drawable.under_glow_00098,
            R.drawable.under_glow_00099,
            R.drawable.under_glow_00100,
            R.drawable.under_glow_00101,
            R.drawable.under_glow_00102,
            R.drawable.under_glow_00103,
            R.drawable.under_glow_00104,
            R.drawable.under_glow_00105,
            R.drawable.under_glow_00106,
            R.drawable.under_glow_00107,
            R.drawable.under_glow_00108,
            R.drawable.under_glow_00109,
            R.drawable.under_glow_00110,
            R.drawable.under_glow_00111,
            R.drawable.under_glow_00112,
            R.drawable.under_glow_00113,
            R.drawable.under_glow_00114,
            R.drawable.under_glow_00115,
            R.drawable.under_glow_00116,
            R.drawable.under_glow_00117,
            R.drawable.under_glow_00118,
            R.drawable.under_glow_00119,
            R.drawable.under_glow_00120,
            R.drawable.under_glow_00121,
            R.drawable.under_glow_00122,
            R.drawable.under_glow_00123,
            R.drawable.under_glow_00124,
            R.drawable.under_glow_00125,
            R.drawable.under_glow_00126
        ) // 127
        val scissorsResources = arrayOf(
            R.drawable.scissors_000,
            R.drawable.scissors_001,
            R.drawable.scissors_002,
            R.drawable.scissors_003,
            R.drawable.scissors_004,
            R.drawable.scissors_005,
            R.drawable.scissors_006,
            R.drawable.scissors_007,
            R.drawable.scissors_008,
            R.drawable.scissors_009,
            R.drawable.scissors_010,
            R.drawable.scissors_011,
            R.drawable.scissors_012,
            R.drawable.scissors_013,
            R.drawable.scissors_014,
            R.drawable.scissors_015,
            R.drawable.scissors_016,
            R.drawable.scissors_017,
            R.drawable.scissors_018,
            R.drawable.scissors_019,
            R.drawable.scissors_020,
            R.drawable.scissors_021,
            R.drawable.scissors_022,
            R.drawable.scissors_023,
            R.drawable.scissors_024,
            R.drawable.scissors_025,
            R.drawable.scissors_026,
            R.drawable.scissors_027,
            R.drawable.scissors_028,
            R.drawable.scissors_029,
            R.drawable.scissors_030,
            R.drawable.scissors_031,
            R.drawable.scissors_032,
            R.drawable.scissors_033,
            R.drawable.scissors_034,
            R.drawable.scissors_035,
            R.drawable.scissors_036,
            R.drawable.scissors_037,
            R.drawable.scissors_038,
            R.drawable.scissors_039,
            R.drawable.scissors_040,
            R.drawable.scissors_041,
            R.drawable.scissors_042,
            R.drawable.scissors_043,
            R.drawable.scissors_044,
            R.drawable.scissors_045,
            R.drawable.scissors_046,
            R.drawable.scissors_047,
            R.drawable.scissors_048,
            R.drawable.scissors_049,
            R.drawable.scissors_050,
            R.drawable.scissors_051
        ) // 52

        val imagesCount = alphaPackResources.size + glowResources.size + scissorsResources.size // 234
        val requiredSize = BitmapCompat.getAllocationByteCount(BitmapFactory.decodeResource(res, alphaPackResources[0]))
        val activityManager = context?.let { nonNullContext ->
            ContextCompat.getSystemService(nonNullContext, ActivityManager::class.java)
        }
        val options = BitmapFactory.Options().getFullOptimizedOptionsForMultipleImages(
            canvasSize, alphaPackResources[0],
            requiredSize * imagesCount, res,
            activityManager
        )
        val alphaPackBitmaps = alphaPackResources
            .map {
                BitmapFactory.decodeResource(res, it, options)
                    .getScaledBitmapToSaveSize(canvasSize.width, canvasSize.height)
            }
        val underGlowBitmaps = glowResources
            .map {
                BitmapFactory.decodeResource(res, it, options)
                    .getScaledBitmapToSaveSize(canvasSize.width, canvasSize.height)
            }
        val scissorsBitmaps = scissorsResources
            .map {
                BitmapFactory.decodeResource(res, it, options)
                .getScaledBitmapToSaveSize(canvasSize.width, canvasSize.height)
            }

        return Playlist(
            PlayRoad(underGlowBitmaps, true).apply { playbackStatus.postValue(PlaybackStatus.PLAYING) },
            PlayRoad(alphaPackBitmaps, false),
            PlayRoad(scissorsBitmaps, true).apply { playbackStatus.postValue(PlaybackStatus.PLAYING) },
            30L
        )
    }

    private fun draw(surfaceHolder: SurfaceHolder, bottomLayer: Bitmap?, middleLayer: Bitmap?, topLayer: Bitmap?, @ColorInt backgroundColor: Int) {
        surfaceHolder.lockCanvas()?.let { nonNullCanvas ->
            synchronized(nonNullCanvas) {
                nonNullCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                nonNullCanvas.drawARGB(
                    255,
                    Color.red(backgroundColor),
                    Color.green(backgroundColor),
                    Color.blue(backgroundColor)
                )

                if (bottomLayer != null)
                    nonNullCanvas.drawBitmap(
                        bottomLayer,
                        bottomLayer.createMatrix(canvasSize),
                        null
                    )

                if (middleLayer != null)
                    nonNullCanvas.drawBitmap(
                        middleLayer,
                        middleLayer.createMatrix(canvasSize),
                        null
                    )

                if (topLayer != null)
                    nonNullCanvas.drawBitmap(
                        topLayer,
                        topLayer.createMatrix(canvasSize),
                        null
                    )

                surfaceHolder.unlockCanvasAndPost(nonNullCanvas)
            }
        }
    }

}