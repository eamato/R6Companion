package eamato.funn.r6companion.utils.open_pack

import android.graphics.drawable.AnimationDrawable
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class Player(
    private val animationDrawable: AnimationDrawable,
    private val doOnDonePlaying: ((Boolean) -> Unit)? = null
) {

    companion object {
        const val straight = 1
        const val reversed = -1
    }

    private val atomicCurrentPosition = AtomicInteger(0)
    val atomicPlayFromPosition = AtomicInteger(0)

    private val frameCount: Int = animationDrawable.numberOfFrames
    private var animationDuration = 0
    private var frameDuration = animationDuration / frameCount

    private var disposable: Disposable? = null
    private var playback: Flowable<Int>? = null

    private val pPlaybackStatus = MutableLiveData<PlaybackStatus>()
    val playbackStatus: LiveData<PlaybackStatus> = pPlaybackStatus

    /**
     * Can be one of straight or reversed
     */
    var playMode = straight

    init {
        for (i in 0 until frameCount) {
            animationDuration = animationDuration.plus(animationDrawable.getDuration(i))
        }
        frameDuration = animationDuration / frameCount

        playback = Flowable.interval(frameDuration.toLong(), TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                var currentPosition = (it.toInt() * playMode + atomicPlayFromPosition.get()) % frameCount
                if (playMode == reversed)
                    currentPosition = (frameCount + currentPosition) % frameCount
                if (atomicCurrentPosition.get() != 0 && currentPosition == 0) {
                    doOnDonePlaying?.invoke(playMode == straight)
                    if (animationDrawable.isOneShot)
                        stop()
                }
                atomicCurrentPosition.set(currentPosition)
                currentPosition
            }

        pPlaybackStatus.value = PlaybackStatus.IDLE
    }

    fun play() {
        disposable?.dispose()
        atomicCurrentPosition.set(0)
        atomicPlayFromPosition.set(0)
        disposable = playback
            ?.subscribe({
                animationDrawable.selectDrawable(it)
            }, {
                stop()
                it.printStackTrace()
            })
        pPlaybackStatus.value = PlaybackStatus.AUTO_PLAYING
    }

    fun playFrameAtPosition(position: Int) {
        animationDrawable.selectDrawable(position)
        pPlaybackStatus.value = PlaybackStatus.PLAYING
    }

    fun pause() {
        disposable?.dispose()
        pPlaybackStatus.value = PlaybackStatus.IDLE
        atomicPlayFromPosition.set(atomicCurrentPosition.get())
        atomicCurrentPosition.set(0)
    }

    fun resume(): Boolean {
        if (atomicPlayFromPosition.get() == 0) {
            stop()
            return false
        }
        disposable?.dispose()
        disposable = playback?.subscribe({
            animationDrawable.selectDrawable(it)
        }, {
            stop()
            it.printStackTrace()
        })
        pPlaybackStatus.value = PlaybackStatus.AUTO_PLAYING
        return true
    }

    fun stop() {
        disposable?.dispose()
        pPlaybackStatus.value = PlaybackStatus.IDLE
        atomicCurrentPosition.set(0)
        atomicPlayFromPosition.set(0)
    }

    fun loop(loop: Boolean) {
        animationDrawable.isOneShot = !loop
    }

    data class Calculations(
        var frameCount: Int = 0,
        var animationDuration: Int = 0,
        var frameAnimationDuration: Int = 0,
        var frameByFrameThreshold: Float = 0f,
        var framePerPixel: Int = 0
    )

    enum class PlaybackStatus {
        AUTO_PLAYING, PLAYING, IDLE
    }

    fun getCalculations(view: View): Calculations {
        val viewWidth = view.width
        val calculations = Calculations()
        calculations.frameCount = frameCount
        calculations.animationDuration = animationDuration
        calculations.frameAnimationDuration = frameDuration
        calculations.frameByFrameThreshold = viewWidth - (viewWidth.toFloat() / 3)
        val fpp = viewWidth / frameCount
        if (fpp < 0)
            throw Exception("View width is less than frame count")
        calculations.framePerPixel = fpp
        return calculations
    }

}