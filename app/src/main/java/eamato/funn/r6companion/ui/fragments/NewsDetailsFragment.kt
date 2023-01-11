package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.R
import eamato.funn.r6companion.databinding.FragmentNewsDetailsBinding
import eamato.funn.r6companion.entities.content_view.abstracts.AVideoContentView
import eamato.funn.r6companion.entities.dto.UpdateDTO
import eamato.funn.r6companion.ui.fragments.abstracts.BaseInnerToolbarFragment
import eamato.funn.r6companion.utils.IDoAfterTerminateGlide
import eamato.funn.r6companion.utils.VideoInitializer
import eamato.funn.r6companion.utils.contentToViewList
import eamato.funn.r6companion.utils.glide.GlideApp

private const val SCREEN_NAME = "News details screen"

class NewsDetailsFragment : BaseInnerToolbarFragment() {

    private var fragmentNewsDetailsBinding: FragmentNewsDetailsBinding? = null

    private var selectedNews: UpdateDTO? = null

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

    override fun onDestroyView() {
        super.onDestroyView()

        fragmentNewsDetailsBinding = null
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {

    }

    override fun onLiveDataObserversSet() {

    }

    override fun initViewWithInnerToolbar() {
        fragmentNewsDetailsBinding?.toolbar?.run {
            initViewWithInnerToolbar(this)
        }

        selectedNews?.run {
            fragmentNewsDetailsBinding?.ctl?.title = title

            fragmentNewsDetailsBinding?.ivNewsImage?.let { nonNullImageView ->
                fragmentNewsDetailsBinding?.clpbNewsImage?.show()
                GlideApp.with(nonNullImageView)
                    .load(thumbnail.url)
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
        }

        initContent()
    }

    override fun initViewWithoutInnerToolbar() {
        initContent()
    }

    private fun initContent() {
        selectedNews?.run {
            fragmentNewsDetailsBinding?.llContent?.run {
                content
                    .contentToViewList()
                    .forEach { nonNullContentView ->
                        val view = nonNullContentView.createView(this)
                        addView(view)

                        if (nonNullContentView is AVideoContentView)
                            VideoInitializer(nonNullContentView, context).initializeYoutubeVideo(childFragmentManager)
                    }
            }
        }
    }
}