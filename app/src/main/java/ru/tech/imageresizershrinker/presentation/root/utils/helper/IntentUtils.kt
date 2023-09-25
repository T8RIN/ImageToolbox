package ru.tech.imageresizershrinker.presentation.root.utils.helper

import android.content.Intent
import android.os.Build
import android.os.Parcelable

object IntentUtils {
    inline fun <reified T : Parcelable> Intent.parcelable(
        key: String
    ): T? = runCatching {
        when {
            Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
        }
    }.getOrNull()

    inline fun <reified T : Parcelable> Intent.parcelableArrayList(
        key: String
    ): ArrayList<T>? = runCatching {
        when {
            Build.VERSION.SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
            else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
        }
    }.getOrNull()
}