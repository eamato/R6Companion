package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.News
import eamato.funn.r6companion.ui.fragments.abstracts.BaseInnerToolbarFragment
import eamato.funn.r6companion.utils.IDoAfterTerminateGlide
import eamato.funn.r6companion.utils.getDisplayMetrics
import eamato.funn.r6companion.utils.glide.GlideApp
import eamato.funn.r6companion.utils.glide.ImageGetter
import kotlinx.android.synthetic.main.fragment_news_details.*

private const val SCREEN_NAME = "News details screen"

class NewsDetailsFragment : BaseInnerToolbarFragment() {

    private var selectedNews: News.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { nonNullArguments ->
            selectedNews = NewsDetailsFragmentArgs.fromBundle(nonNullArguments).selectedNews
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clpb_news_image?.hide()
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {

    }

    override fun onLiveDataObserversSet() {

    }

    override fun initViewWithInnerToolbar() {
        initViewWithInnerToolbar(toolbar)

        selectedNews?.let {
            ctl?.title = it.title ?: getString(R.string.news_details_label)

            iv_news_image?.let { nonNullImageView ->
                clpb_news_image?.show()
                GlideApp.with(nonNullImageView)
                    .load(it.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .error(R.drawable.no_data_placeholder)
                    .listener(object : IDoAfterTerminateGlide {
                        override fun doAfterTerminate() {
                            clpb_news_image?.hide()
                        }
                    })
                    .dontAnimate()
                    .into(nonNullImageView)
            }

            val displayMetrics = activity.getDisplayMetrics()

            tv_content?.text = HtmlCompat.fromHtml(it.body ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY,
                ImageGetter(tv_content, displayMetrics.widthPixels), null)
            tv_content?.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun initViewWithoutInnerToolbar() {

    }

}