package eamato.funn.r6companion.entities.content_view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.content_view.abstracts.AVideoContentView

class ContentVideoView(val videoUrl: String) : AVideoContentView() {

    override fun createView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.fragment_youtube_video, parent, false)
    }

    override fun getShortUrl(): String {
        return Uri.parse(videoUrl).lastPathSegment ?: ""
    }
}