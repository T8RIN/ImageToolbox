package ru.tech.imageresizershrinker.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Save
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.widget.ToastDuration
import ru.tech.imageresizershrinker.widget.ToastHostState
import kotlin.math.min

object ContextUtils {
    fun Activity.requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1
        )
    }

    fun Context.isExternalStorageWritable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
        else ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun Context.needToShowStoragePermissionRequest(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) false
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) false
        else !(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun Activity.failedToSaveImages(
        scope: CoroutineScope,
        failed: Int,
        done: Int,
        toastHostState: ToastHostState,
        savingPathString: String,
        showConfetti: () -> Unit
    ) {
        if (failed == -1) requestPermission()
        else if (failed == 0) {
            scope.launch {
                toastHostState.showToast(
                    getString(
                        R.string.saved_to,
                        savingPathString
                    ),
                    Icons.Rounded.Save
                )
            }
            showConfetti()
        } else if (failed < done) {
            scope.launch {
                showConfetti()
                toastHostState.showToast(
                    getString(
                        R.string.saved_to,
                        savingPathString
                    ),
                    Icons.Rounded.Save
                )
                toastHostState.showToast(
                    getString(R.string.failed_to_save, failed),
                    Icons.Rounded.ErrorOutline,
                    ToastDuration.Long
                )
            }
        } else {
            scope.launch {
                toastHostState.showToast(
                    getString(R.string.failed_to_save, failed),
                    Icons.Rounded.ErrorOutline,
                    ToastDuration.Long
                )
            }
        }
    }

    fun Context.adjustFontSize(
        scale: Float = min(resources.configuration.fontScale, 1f)
    ): Context {
        val configuration = resources.configuration
        configuration.fontScale = scale
        return createConfigurationContext(configuration)
    }
}