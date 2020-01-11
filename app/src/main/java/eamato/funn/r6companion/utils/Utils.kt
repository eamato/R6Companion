package eamato.funn.r6companion.utils

import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.io.File

fun createScreenshotAndGetItsUri(view: View, window: Window?, file: File, displayMetrics: DisplayMetrics): Single<File> {
    return if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.createScreenshot(displayMetrics).flatMap { it.toFileInInternalStorage(file) }
    } else {
        view.createScreenshot().flatMap { it.toFileInInternalStorage(file) }
    }
}

fun <T> getAsyncSingleTransformer(): SingleTransformer<T, T>? {
    return SingleTransformer { upstream: Single<T> ->
        upstream
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}

fun <U, D> getAsyncSingleTransformer(mapper: Function<in U, out D>): SingleTransformer<U, D>? {
    return SingleTransformer { upstream: Single<U> ->
        upstream
            .subscribeOn(Schedulers.io())
            .map(mapper)
            .observeOn(AndroidSchedulers.mainThread())
    }
}