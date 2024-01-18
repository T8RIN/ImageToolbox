/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("SameParameterValue", "KotlinConstantConditions")

package ru.tech.imageresizershrinker.core.ui.utils.helper

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
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelable
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionStatus
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.askUserToRequestPermissionExplicitly
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.checkPermissions
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.setPermissionsAllowed
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
        onHasPdfUri: (Uri) -> Unit,
        notHasUris: Boolean,
        onWantGithubReview: () -> Unit
    ) {
        fun ClipData.clipList() = List(
            size = itemCount,
            init = {
                getItemAt(it).uri
            }
        ).filterNotNull()

        onStart()
        if (intent?.type != null && notHasUris) onColdStart()

        if (intent?.action == Intent.ACTION_BUG_REPORT) {
            onWantGithubReview()
            return
        }

        runCatching {
            val startsWithImage = intent?.type?.startsWith("image/") == true
            val hasJxl = intent?.clipData?.clipList()
                ?.any { it.toString().endsWith(".jxl") } == true
            val dataHasJxl = intent?.data.toString().endsWith(".jxl")

            if ((startsWithImage || hasJxl || dataHasJxl) && intent != null) {
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
                if (
                    intent.type?.contains("pdf") == true
                ) {
                    val uri = intent.data ?: intent.parcelable<Uri>(Intent.EXTRA_STREAM)
                    uri?.let {
                        if (intent.action == Intent.ACTION_VIEW) {
                            navigate(Screen.PdfTools(Screen.PdfTools.Type.Preview(it)))
                        } else onHasPdfUri(uri)
                    }
                } else {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        navigate(Screen.LoadNetImage(it))
                    } ?: intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                        navigate(Screen.Cipher(it))
                    } ?: showToast(
                        getString(R.string.unsupported_type, intent.type),
                        Icons.Rounded.ErrorOutline
                    )
                }
            } else Unit
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

    /** Save a text into the clipboard. */
    fun Context.copyToClipboard(label: String, value: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
    }

    fun Context.shareText(value: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, value)
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(shareIntent)
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