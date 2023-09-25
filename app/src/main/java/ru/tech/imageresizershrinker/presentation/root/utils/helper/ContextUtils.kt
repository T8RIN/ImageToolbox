@file:Suppress("SameParameterValue", "KotlinConstantConditions")

package ru.tech.imageresizershrinker.presentation.root.utils.helper

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.utils.helper.IntentUtils.parcelable
import ru.tech.imageresizershrinker.presentation.root.utils.helper.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.utils.permission.PermissionStatus
import ru.tech.imageresizershrinker.presentation.root.utils.permission.PermissionUtils.askUserToRequestPermissionExplicitly
import ru.tech.imageresizershrinker.presentation.root.utils.permission.PermissionUtils.checkPermissions
import ru.tech.imageresizershrinker.presentation.root.utils.permission.PermissionUtils.setPermissionsAllowed
import java.io.BufferedReader
import java.io.InputStreamReader


object ContextUtils {

    fun Activity.requestStoragePermission() {
        val state = checkPermissions(
            listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        when (state.permissionStatus.values.first()) {
            PermissionStatus.NOT_GIVEN -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
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

    fun Context.needToShowStoragePermissionRequest(): Boolean {
        val show = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) false
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) false
        else !(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)

        if (!show) {
            setPermissionsAllowed(
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }

        return show
    }

    fun Context.adjustFontSize(
        scale: Float?
    ): Context {
        val configuration = resources.configuration
        configuration.fontScale = scale ?: resources.configuration.fontScale
        return createConfigurationContext(configuration)
    }

    fun Context.isInstalledFromPlayStore(): Boolean = verifyInstallerId(
        listOf(
            "com.android.vending",
            "com.google.android.feedback"
        )
    )

    fun Context.verifyInstallerId(
        validInstallers: List<String>
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

    fun Context.getFileName(uri: Uri): String? = DocumentFile.fromSingleUri(this, uri)?.name

    fun Context.parseImageFromIntent(
        intent: Intent?,
        onStart: () -> Unit,
        onColdStart: () -> Unit,
        showToast: (message: String, icon: ImageVector) -> Unit,
        navigate: (Screen) -> Unit,
        onGetUris: (List<Uri>) -> Unit,
        notHasUris: Boolean
    ) {
        fun ClipData.clipList() = List(
            size = itemCount,
            init = {
                getItemAt(it).uri
            }
        )
        onStart()
        if (intent?.type != null && notHasUris) onColdStart()

        runCatching {
            if (intent?.type?.startsWith("image/") == true || (intent?.clipData?.clipList()
                    ?.any { it.toString().endsWith(".jxl") } == true && BuildConfig.FLAVOR == "jxl")
            ) {
                when (intent.action) {
                    Intent.ACTION_VIEW -> {
                        val data = intent.data
                        val clipData = intent.clipData
                        if (clipData != null) {
                            navigate(Screen.ImagePreview(intent.clipData!!.clipList()))
                        } else if (data != null) {
                            navigate(Screen.ImagePreview(listOf(data)))
                        } else null
                    }

                    Intent.ACTION_SEND -> {
                        intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                            if (intent.getStringExtra("screen") == Screen.PickColorFromImage::class.simpleName) {
                                navigate(Screen.PickColorFromImage(it))
                            } else {
                                onGetUris(listOf(it))
                            }
                        }
                    }

                    Intent.ACTION_SEND_MULTIPLE -> {
                        intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                            onGetUris(it)
                        }
                    }

                    else -> {
                        intent.data?.let { onGetUris(listOf(it)) }
                    }
                }
            } else if (intent?.type != null) {
                if (intent.type?.contains("text") == true) {
                    navigate(Screen.LoadNetImage(intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""))
                } else {
                    intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                        navigate(Screen.Cipher(it))
                    } ?: showToast(
                        getString(R.string.unsupported_type, intent.type),
                        Icons.Rounded.ErrorOutline
                    )
                }
            } else null
        }.getOrNull() ?: showToast(
            getString(R.string.something_went_wrong),
            Icons.Rounded.ErrorOutline
        )
    }

    tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    fun Context.clearCache(onComplete: (cache: String) -> Unit = {}) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                cacheDir.deleteRecursively()
                codeCacheDir.deleteRecursively()
                externalCacheDir?.deleteRecursively()
                externalCacheDirs.forEach {
                    it.deleteRecursively()
                }
            }
            onComplete(cacheSize())
        }
    }

    fun Context.cacheSize(): String {
        return kotlin.runCatching {
            val cache = cacheDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            val code = codeCacheDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            var size = cache + code
            externalCacheDirs.forEach { file ->
                size += file.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            }
            readableByteCount(size)
        }.getOrNull() ?: "0 B"
    }

    /** Save a text into the clipboard. */
    fun Context.copyColorIntoClipboard(label: String, value: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
    }

    /** Receive the clipboard data. */
    fun Context.pasteColorFromClipboard(
        onPastedColor: (Int) -> Unit,
        onPastedColorFailure: (String) -> Unit,
    ) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text?.toString()
        text?.let {
            runCatching {
                onPastedColor(android.graphics.Color.parseColor(it))
            }.getOrElse {
                onPastedColorFailure(getString(R.string.clipboard_paste_invalid_color_code))
            }
        } ?: run {
            onPastedColorFailure(getString(R.string.clipboard_paste_invalid_empty))
        }
    }

    fun isMiUi(): Boolean {
        return !getSystemProperty("ro.miui.ui.version.name").isNullOrBlank()
    }

    private fun getSystemProperty(name: String): String? {
        return kotlin.runCatching {
            val p = Runtime.getRuntime().exec("getprop $name")
            BufferedReader(InputStreamReader(p.inputStream), 1024).use {
                return@runCatching it.readLine()
            }
        }.getOrNull()
    }
}