package eamato.funn.r6companion.entities.content_view

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat

class ContentVideoView(val videoUrl: String) : IContentView {

//    AIzaSyDEX14GWMffqOGbR9uSmraoL5P8OjrCnk8

    override fun createView(context: Context): View {
        return View(context)
//        return PlayerView(context).apply {
//            layoutParams = LinearLayoutCompat.LayoutParams(
//                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//                LinearLayoutCompat.LayoutParams.MATCH_PARENT
//            )
//
//            val mPlayer =  SimpleExoPlayer.Builder(context).build()
//            this.player = mPlayer
//
//            val dataSourceFactory = DefaultDataSourceFactory(
//                context, Util.getUserAgent(context, "player")
//            )
//            val extractorMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))
//
//            mPlayer.setMediaSource(extractorMediaSource)
//            mPlayer.addListener(object : Player.EventListener {
//                override fun onPlayerError(error: ExoPlaybackException) {
//                    super.onPlayerError(error)
//                }
//            })
//            mPlayer.prepare()
//            mPlayer.playWhenReady = true
//        }
//        return Button(context, null, 0, R.style.AppTheme_ContentButtonStyle).apply {
//            text = "Play me"
//        }
    }
}