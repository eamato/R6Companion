package eamato.funn.r6companion.entities.content_view

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ContentVideoView(val videoUrl: String) : IContentView {

    override fun createView(context: Context): View {
        return PlayerView(context).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT
            )

            val mPlayer =  SimpleExoPlayer.Builder(context).build()
            this.player = mPlayer

            val dataSourceFactory = DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "player")
            )
            val extractorMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))

            mPlayer.setMediaSource(extractorMediaSource)
            mPlayer.addListener(object : Player.EventListener {
                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                }
            })
            mPlayer.prepare()
            mPlayer.playWhenReady = true
        }
//        return Button(context, null, 0, R.style.AppTheme_ContentButtonStyle).apply {
//            text = "Play me"
//        }
    }
}