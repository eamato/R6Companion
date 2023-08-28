package eamato.funn.r6companion.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class UIText {
    data class SimpleString(val value: String?): UIText()
    class ResourceString(@StringRes val resId: Int, vararg val args: Any) : UIText()

    fun asString(context: Context): String? {
        return when (this) {
            is SimpleString -> value
            is ResourceString -> context.getString(resId, args)
        }
    }
}