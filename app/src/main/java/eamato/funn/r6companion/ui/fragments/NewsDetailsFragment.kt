package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.R
import eamato.funn.r6companion.databinding.FragmentNewsDetailsBinding
import eamato.funn.r6companion.entities.Updates
import eamato.funn.r6companion.entities.content_view.abstracts.AVideoContentView
import eamato.funn.r6companion.ui.fragments.abstracts.BaseInnerToolbarFragment
import eamato.funn.r6companion.utils.IDoAfterTerminateGlide
import eamato.funn.r6companion.utils.VideoInitializer
import eamato.funn.r6companion.utils.contentToViewList
import eamato.funn.r6companion.utils.glide.GlideApp

private const val SCREEN_NAME = "News details screen"

class NewsDetailsFragment : BaseInnerToolbarFragment() {

    private var fragmentNewsDetailsBinding: FragmentNewsDetailsBinding? = null

    private var selectedNews: Updates.Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { nonNullArguments ->
            selectedNews = NewsDetailsFragmentArgs.fromBundle(nonNullArguments).selectedNews
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentNewsDetailsBinding = FragmentNewsDetailsBinding.inflate(inflater, container, false)
        return fragmentNewsDetailsBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentNewsDetailsBinding?.clpbNewsImage?.hide()
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {

    }

    override fun onLiveDataObserversSet() {

    }

    override fun initViewWithInnerToolbar() {
        fragmentNewsDetailsBinding?.toolbar?.let {
            initViewWithInnerToolbar(it)
        }

        selectedNews?.let {
            fragmentNewsDetailsBinding?.ctl?.title = it.title ?: getString(R.string.news_details_label)

            fragmentNewsDetailsBinding?.ivNewsImage?.let { nonNullImageView ->
                fragmentNewsDetailsBinding?.clpbNewsImage?.show()
                GlideApp.with(nonNullImageView)
                    .load(it.thumbnail?.url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .error(R.drawable.no_data_placeholder)
                    .listener(object : IDoAfterTerminateGlide {
                        override fun doAfterTerminate() {
                            fragmentNewsDetailsBinding?.clpbNewsImage?.hide()
                        }
                    })
                    .dontAnimate()
                    .into(nonNullImageView)
            }

            fragmentNewsDetailsBinding?.llContent?.let { nonNullParent ->
                it.content?.contentToViewList()?.forEach { nonNullContentView ->
                    val view = nonNullContentView.createView(nonNullParent)
                    fragmentNewsDetailsBinding?.llContent?.addView(view)

                    if (nonNullContentView is AVideoContentView)
                        VideoInitializer(nonNullContentView, context).initializeYoutubeVideo(childFragmentManager)
                }
            }
        }
    }

    override fun initViewWithoutInnerToolbar() {

    }

}