package eamato.funn.r6companion.ui.fragments.abstracts

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar

abstract class BaseInnerToolbarFragment : BaseFragment() {

    private var parentToolbarHidden = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentToolbarHidden = hideParentToolbar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (parentToolbarHidden)
            initViewWithInnerToolbar()
        else
            initViewWithoutInnerToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        showParentToolbar()
    }

    protected fun initViewWithInnerToolbar(toolbar: Toolbar) {
        showParentToolbar(toolbar)
    }

    private fun hideParentToolbar(): Boolean {
        return mainActivity?.supportActionBar?.let { nonNullSupportActionBar ->
            nonNullSupportActionBar.hide()
            true
        } ?: false
    }

    private fun showParentToolbar(toolbar: Toolbar? = null) {
        if (toolbar == null)
            mainActivity?.setParentToolbar()
        else
            mainActivity?.let { nonNullMainActivity ->
                nonNullMainActivity.setParentToolbar(toolbar)
                nonNullMainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                toolbar.setNavigationOnClickListener {
                    mainActivity?.onBackPressed()
                }
            }
    }

    abstract fun initViewWithInnerToolbar()
    abstract fun initViewWithoutInnerToolbar()

}