package eamato.funn.r6companion.utils.open_pack

import android.graphics.Bitmap
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class Player2(playlist: Playlist, private val onPlaybackFinished: (() -> Unit)? = null) {

    val bottomPlayRoadPlayer = PlayRoadPlayer(playlist.bottomLayerRoad)
    val middlePlayRoadPlayer = PlayRoadPlayer(playlist.middleLayerRoad)
    val topPlayRoadPlayer = PlayRoadPlayer(playlist.topLayerRoad)

    val playback = Flowable.interval(playlist.duration, TimeUnit.MILLISECONDS, Schedulers.single())
        .onBackpressureDrop()
        .map {
            PlaybackIteration(
                bottomPlayRoadPlayer.getFrame(),
                middlePlayRoadPlayer.getFrame(),
                topPlayRoadPlayer.getFrame()
            )
        }

    inner class PlayRoadPlayer(val playRoad: PlayRoad) {
        val lastGivenFrameIndex = AtomicInteger(0)

        val playbackMode = AtomicReference<PlaybackMode>(PlaybackMode.STRAIGHT)
        val isFrameVisible = AtomicBoolean(true)

        fun getFrame(): Bitmap? {
            if (!isFrameVisible.get())
                return null
            return when (playRoad.playbackStatus.value) {
                PlaybackStatus.PLAYING -> {
                    playRoad.slides[lastGivenFrameIndex.get()].also {

                        val last = lastGivenFrameIndex.get()

                        if (playbackMode.get() == PlaybackMode.REVERSED)
                            if (last == 0)
                                if (playRoad.isLooped)
                                    lastGivenFrameIndex.set(playRoad.slides.lastIndex)
                                else
                                    playRoad.playbackStatus.postValue(PlaybackStatus.PAUSED)
                            else
                                lastGivenFrameIndex.set(last + playbackMode.get().addition)
                        else
                            if (last == playRoad.slides.lastIndex)
                                if (playRoad.isLooped)
                                    lastGivenFrameIndex.set(0)
                                else {
                                    playRoad.playbackStatus.postValue(PlaybackStatus.PAUSED)
                                    onPlaybackFinished?.invoke()
                                }
                            else
                                lastGivenFrameIndex.set(last + playbackMode.get().addition)

                    }
                }
                PlaybackStatus.PLAYINGFBF -> {
                    playRoad.slides[lastGivenFrameIndex.get()]
                }
                PlaybackStatus.PAUSED -> {
                    playRoad.slides[lastGivenFrameIndex.get()]
                }
                PlaybackStatus.STOPPED -> {
                    lastGivenFrameIndex.set(0)
                    playRoad.slides[lastGivenFrameIndex.get()]
                }
                else -> {
                    lastGivenFrameIndex.set(0)
                    playRoad.slides[lastGivenFrameIndex.get()]
                }
            }
        }
    }

}