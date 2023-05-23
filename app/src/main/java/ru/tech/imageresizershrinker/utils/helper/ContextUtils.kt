package ru.tech.imageresizershrinker.utils.helper

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Save
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.utils.permission.PermissionStatus
import ru.tech.imageresizershrinker.utils.permission.PermissionUtils.askUserToRequestPermissionExplicitly
import ru.tech.imageresizershrinker.utils.permission.PermissionUtils.checkPermissions
import ru.tech.imageresizershrinker.utils.permission.PermissionUtils.setPermissionsAllowed
import ru.tech.imageresizershrinker.widget.ToastDuration
import ru.tech.imageresizershrinker.widget.ToastHostState
import java.io.File
import kotlin.math.min


object ContextUtils {

    fun Activity.requestStoragePermission() {
        val state = checkPermissions(
            listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
        )
        when (state.permissionStatus.values.first()) {
            PermissionStatus.NOT_GIVEN -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    ),
                    0
                )
            }

            PermissionStatus.DENIED_PERMANENTLY -> {
                askUserToRequestPermissionExplicitly()
                Toast.makeText(this, R.string.grant_permission_manual, Toast.LENGTH_LONG).show()
            }

            PermissionStatus.ALLOWED -> Unit
        }
    }

    fun Context.isExternalStorageWritable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
        else ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun Context.needToShowStoragePermissionRequest(): Boolean {
        val show = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) false
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) false
        else !(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)

        if (!show) {
            setPermissionsAllowed(
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                )
            )
        }

        return show
    }

    fun Activity.failedToSaveImages(
        scope: CoroutineScope,
        failed: Int,
        done: Int,
        toastHostState: ToastHostState,
        savingPathString: String,
        showConfetti: () -> Unit
    ) {
        if (failed == -1) requestStoragePermission()
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

    fun Context.verifyInstallerId(
        validInstallers: List<String> = listOf(
            "com.android.vending",
            "com.google.android.feedback"
        )
    ): Boolean = validInstallers.contains(getInstallerPackageName(packageName))

    private fun Context.getInstallerPackageName(packageName: String): String? {
        kotlin.runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                return packageManager.getInstallSourceInfo(packageName).installingPackageName
            @Suppress("DEPRECATION")
            return packageManager.getInstallerPackageName(packageName)
        }
        return null
    }

    fun Context.getFileName(uri: Uri): String? = when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> getContentFileName(uri)
        else -> uri.path?.let(::File)?.name
    }

    private fun Context.getContentFileName(uri: Uri): String? = runCatching {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            return@use cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE)
                .let(cursor::getString)
        }
    }.getOrNull()
}