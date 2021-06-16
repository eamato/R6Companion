package eamato.funn.r6companion.utils

import androidx.fragment.app.FragmentManager
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.content_view.abstracts.AVideoContentView

class VideoInitializer(private val aVideoContentView: AVideoContentView) {

    fun initializeYoutubeVideo(childFragmentManager: FragmentManager) {
        val youtubePlayerFragment = YouTubePlayerSupportFragmentX()
        youtubePlayerFragment.initialize(YOUTUBE_API_KEY, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                wasRestored: Boolean
            ) {
                if (!wasRestored)
                    player?.cueVideo(aVideoContentView.getShortUrl())
            }

            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider?,
                result: YouTubeInitializationResult?
            ) {

            }
        })
        childFragmentManager.beginTransaction()
            .add(R.id.youtube_fragment, youtubePlayerFragment)
            .commit()
    }

}