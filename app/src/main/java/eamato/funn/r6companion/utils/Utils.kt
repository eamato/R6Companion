package eamato.funn.r6companion.utils

import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import io.reactivex.Single
import java.io.File

fun createScreenshotAndGetItsUri(view: View, window: Window?, file: File, displayMetrics: DisplayMetrics): Single<File> {
    return if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.createScreenshot(displayMetrics).flatMap { it.toFileInInternalStorage(file) }
    } else {
        view.createScreenshot().flatMap { it.toFileInInternalStorage(file) }
    }
}