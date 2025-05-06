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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.core.app.ActivityCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import androidx.core.os.LocaleListCompat
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.BackupFileExtension
import ru.tech.imageresizershrinker.core.domain.model.ExtraDataType
import ru.tech.imageresizershrinker.core.domain.model.PerformanceClass
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelable
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.core.ui.utils.helper.image_vector.toImageBitmap
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionStatus
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.askUserToRequestPermissionExplicitly
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.checkPermissions
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.hasPermissionAllowed
import ru.tech.imageresizershrinker.core.ui.utils.permission.PermissionUtils.setPermissionsAllowed
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.math.ceil


object ContextUtils {

    fun Activity.requestStoragePermission() {
        val permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val state = checkPermissions(permissions)
        when (state.permissionStatus.values.first()) {
            PermissionStatus.NOT_GIVEN -> {
                ActivityCompat.requestPermissions(
                    this,
                    permissions.toTypedArray(),
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

    fun Context.startActivity(
        clazz: Class<*>,
        intentBuilder: Intent.() -> Unit,
    ) {
        startActivity(buildIntent(clazz, intentBuilder))
    }

    fun Context.buildIntent(
        clazz: Class<*>,
        intentBuilder: Intent.() -> Unit,
    ): Intent = Intent(applicationContext, clazz).apply(intentBuilder)

    fun Context.postToast(
        textRes: Int,
        vararg formatArgs: Any,
    ) {
        mainLooperAction {
            Toast.makeText(
                applicationContext,
                getString(
                    textRes,
                    *formatArgs
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun Context.postToast(
        textRes: Int,
        isLong: Boolean = false,
        vararg formatArgs: Any,
    ) {
        mainLooperAction {
            Toast.makeText(
                applicationContext,
                getString(
                    textRes,
                    *formatArgs
                ),
                if (isLong) {
                    Toast.LENGTH_LONG
                } else Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun Context.needToShowStoragePermissionRequest(): Boolean {
        val permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val show = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) false
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) false
        else !permissions.all { (this as Activity).hasPermissionAllowed(it) }

        if (!show) setPermissionsAllowed(permissions)

        return show
    }

    fun Context.adjustFontSize(
        scale: Float?,
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

    private fun Context.verifyInstallerId(
        validInstallers: List<String>,
    ): Boolean = validInstallers.contains(getInstallerPackageName(packageName))

    private fun Context.getInstallerPackageName(packageName: String): String? {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                return packageManager.getInstallSourceInfo(packageName).installingPackageName
            @Suppress("DEPRECATION")
            return packageManager.getInstallerPackageName(packageName)
        }
        return null
    }

    fun Context.getFilename(
        uri: Uri
    ): String? = if (uri.toString().startsWith("file:///")) {
        uri.toString().takeLastWhile { it != '/' }
    } else {
        DocumentFile.fromSingleUri(this, uri)?.name
    }?.decodeEscaped()

    @Composable
    fun rememberFilename(uri: Uri): String? {
        val context = LocalContext.current

        return remember(context, uri) {
            derivedStateOf {
                context.getFilename(uri)
            }
        }.value
    }

    @Composable
    fun rememberFileExtension(uri: Uri): String? {
        val context = LocalContext.current

        return remember(context, uri) {
            derivedStateOf {
                context.getExtension(uri)
            }
        }.value
    }

    fun Activity.parseImageFromIntent(
        intent: Intent?,
        onStart: () -> Unit,
        onColdStart: () -> Unit,
        onShowToast: (message: String, icon: ImageVector) -> Unit,
        onNavigate: (Screen) -> Unit,
        onGetUris: (List<Uri>) -> Unit,
        onHasExtraDataType: (ExtraDataType) -> Unit,
        isHasUris: Boolean,
        onWantGithubReview: () -> Unit,
        isOpenEditInsteadOfPreview: Boolean,
    ) {
        if (intent == null) return

        onStart()
        if (intent.type != null && !isHasUris) onColdStart()

        if (intent.action == Intent.ACTION_BUG_REPORT) {
            onWantGithubReview()
            return
        }

        if (intent.getScreenOpeningShortcut(onNavigate)) return

        runCatching {
            val startsWithImage = intent.type?.startsWith("image/") == true
            val hasExtraFormats = intent.clipData?.clipList()
                ?.any {
                    it.toString().endsWith(".jxl") || it.toString().endsWith(".qoi")
                } == true
            val dataHasExtraFormats = intent.data.toString().let {
                it.endsWith(".jxl") || it.endsWith(".qoi")
            }

            if ((startsWithImage || hasExtraFormats || dataHasExtraFormats)) {
                when (intent.action) {
                    Intent.ACTION_VIEW -> {
                        val data = intent.data
                        val clipData = intent.clipData
                        val uris =
                            clipData?.clipList() ?: data?.let { listOf(it) } ?: return@runCatching

                        if (isOpenEditInsteadOfPreview) {
                            onGetUris(uris)
                        } else {
                            onNavigate(Screen.ImagePreview(uris))
                        }
                    }

                    Intent.ACTION_SEND -> {
                        intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                            if (intent.getTileScreenAction() == PickColorAction) {
                                onNavigate(Screen.PickColorFromImage(it))
                            } else {
                                if (intent.type?.contains("gif") == true) {
                                    onHasExtraDataType(ExtraDataType.Gif)
                                }
                                onGetUris(listOf(it))
                            }
                        }
                    }

                    Intent.ACTION_SEND_MULTIPLE -> {
                        intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                            if (intent.type?.contains("gif") == true) {
                                onHasExtraDataType(ExtraDataType.Gif)
                                it.firstOrNull()?.let { uri ->
                                    onGetUris(listOf(uri))
                                }
                            } else onGetUris(it)
                        }
                    }

                    else -> {
                        intent.data?.let {
                            if (intent.type?.contains("gif") == true) {
                                onHasExtraDataType(ExtraDataType.Gif)
                            }
                            onGetUris(listOf(it))
                        }
                    }
                }
            } else if (intent.type != null) {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                val multiplePdfs = intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM) != null

                if (
                    intent.type?.contains("pdf") == true && !multiplePdfs
                ) {
                    val uri = intent.data ?: intent.parcelable<Uri>(Intent.EXTRA_STREAM)
                    uri?.let {
                        if (intent.action == Intent.ACTION_VIEW) {
                            onNavigate(Screen.PdfTools(Screen.PdfTools.Type.Preview(it)))
                        } else {
                            onHasExtraDataType(ExtraDataType.Pdf)
                            onGetUris(listOf(uri))
                        }
                    }
                } else if (text != null) {
                    onHasExtraDataType(ExtraDataType.Text(text))
                    onGetUris(listOf())
                } else {
                    val isAudio = intent.type?.startsWith("audio/") == true

                    when (intent.action) {
                        Intent.ACTION_SEND_MULTIPLE -> {
                            intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                                if (isAudio) {
                                    onHasExtraDataType(ExtraDataType.Audio)
                                    onGetUris(it)
                                } else {
                                    onNavigate(Screen.Zip(it))
                                }
                            }
                        }

                        Intent.ACTION_SEND -> {
                            intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                                if (it.toString().contains(BackupFileExtension, true)) {
                                    onHasExtraDataType(ExtraDataType.Backup(it.toString()))
                                    return
                                }
                                if (isAudio) {
                                    onHasExtraDataType(ExtraDataType.Audio)
                                } else {
                                    onHasExtraDataType(ExtraDataType.File)
                                }

                                onGetUris(listOf(it))
                            }
                        }

                        Intent.ACTION_VIEW -> {
                            val data = intent.data
                            val clipData = intent.clipData
                            val uris =
                                clipData?.clipList() ?: data?.let { listOf(it) } ?: emptyList()

                            if (uris.size == 1) {
                                val uri = uris.first()

                                if (uri.toString().contains(BackupFileExtension, true)) {
                                    onHasExtraDataType(ExtraDataType.Backup(uri.toString()))
                                    return
                                }

                                if (isAudio) {
                                    onHasExtraDataType(ExtraDataType.Audio)
                                } else {
                                    onHasExtraDataType(ExtraDataType.File)
                                }

                                onGetUris(uris)
                            } else if (uris.isNotEmpty()) {
                                if (isAudio) {
                                    onHasExtraDataType(ExtraDataType.Audio)
                                    onGetUris(uris)
                                } else {
                                    onNavigate(Screen.Zip(uris))
                                }
                            } else {
                                Unit
                            }
                        }

                        else -> null
                    } ?: onShowToast(
                        getString(R.string.unsupported_type, intent.type),
                        Icons.Rounded.ErrorOutline
                    )
                }
            } else Unit
        }.getOrNull() ?: onShowToast(
            getString(R.string.something_went_wrong),
            Icons.Rounded.ErrorOutline
        )
    }

    val Context.performanceClass: PerformanceClass
        get() {
            val androidVersion = Build.VERSION.SDK_INT
            val cpuCount = Runtime.getRuntime().availableProcessors()
            val memoryClass =
                (applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass
            var totalCpuFreq = 0
            var freqResolved = 0
            for (i in 0 until cpuCount) {
                runCatching {
                    val reader = RandomAccessFile(
                        String.format(
                            Locale.ENGLISH,
                            "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq",
                            i
                        ), "r"
                    )
                    val line = reader.readLine()
                    if (line != null) {
                        totalCpuFreq += line.toInt() / 1000
                        freqResolved++
                    }
                    reader.close()
                }
            }
            val maxCpuFreq =
                if (freqResolved == 0) -1 else ceil((totalCpuFreq / freqResolved.toFloat()).toDouble())
                    .toInt()

            return if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250 || cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21 || cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24) {
                PerformanceClass.Low
            } else if (cpuCount < 8 || memoryClass <= 160 || maxCpuFreq != -1 && maxCpuFreq <= 2050 || maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23) {
                PerformanceClass.Average
            } else {
                PerformanceClass.High
            }
        }

    @Suppress("unused", "MemberVisibilityCanBePrivate")
    tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    fun Context.copyToClipboard(
        value: String,
    ) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("plain text", value)
        clipboard.setPrimaryClip(clip)
    }

    fun Context.getStringLocalized(
        @StringRes
        resId: Int,
        locale: Locale,
    ): String = createConfigurationContext(
        Configuration(resources.configuration).apply { setLocale(locale) }
    ).getText(resId).toString()

    fun Context.pasteColorFromClipboard(
        onPastedColor: (Int) -> Unit,
        onPastedColorFailure: (String) -> Unit,
    ) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text?.toString()
        text?.let {
            runCatching {
                onPastedColor(it.toColorInt())
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

    fun isRedMagic(): Boolean {
        val osName = runCatching {
            System.getProperty("os.name")
        }.getOrNull() ?: getSystemProperty("os.name")
        return listOf("redmagic", "magic", "red").all {
            osName?.contains(it, true) == true
        }
    }

    private fun getSystemProperty(name: String): String? {
        return runCatching {
            val p = Runtime.getRuntime().exec("getprop $name")
            BufferedReader(InputStreamReader(p.inputStream), 1024).use {
                return@runCatching it.readLine()
            }
        }.getOrNull()
    }

    fun Context.getLanguages(): Map<String, String> {
        val languages = mutableListOf("" to getString(R.string.system)).apply {
            addAll(
                LocaleConfigCompat(this@getLanguages)
                    .supportedLocales!!.toList()
                    .map {
                        it.toLanguageTag() to it.getDisplayName(it)
                            .replaceFirstChar(Char::uppercase)
                    }
            )
        }

        return languages.let { tags ->
            listOf(tags.first()) + tags.drop(1).sortedBy { it.second }
        }.toMap()
    }

    fun Context.getCurrentLocaleString(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales == LocaleListCompat.getEmptyLocaleList()) {
            return getString(R.string.system)
        }
        return locales.getDisplayName()
    }

    fun LocaleListCompat.getDisplayName(): String = getDisplayName(toLanguageTags())

    private fun getDisplayName(lang: String?): String {
        if (lang == null) {
            return ""
        }

        val locale = when (lang) {
            "" -> LocaleListCompat.getAdjustedDefault()[0]
            else -> Locale.forLanguageTag(lang)
        }
        return locale!!.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
    }

    private const val SCREEN_ID_EXTRA = "screen_id"
    private const val SHORTCUT_OPEN_ACTION = "shortcut"
    private fun Intent?.getScreenOpeningShortcut(
        onNavigate: (Screen) -> Unit,
    ): Boolean {
        if (this == null) return false

        if (action == SHORTCUT_OPEN_ACTION && hasExtra(SCREEN_ID_EXTRA)) {
            Screen.entries.find {
                it.id == getIntExtra(SCREEN_ID_EXTRA, -100)
            }?.let(onNavigate) ?: return false

            return true
        }

        return false
    }

    suspend fun Context.createScreenShortcut(
        screen: Screen,
        tint: Color = Color.Unspecified,
        onFailure: (Throwable) -> Unit = {},
    ) = withContext(Dispatchers.Main.immediate) {
        runCatching {
            val context = this@createScreenShortcut
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(context) && screen.icon != null) {
                val imageBitmap = screen.icon!!.toImageBitmap(
                    context = context,
                    width = 256,
                    height = 256,
                    tint = tint.takeOrElse { Color(0xFF5F823E) }
                )

                val info = ShortcutInfoCompat.Builder(context, screen.id.toString())
                    .setShortLabel(getString(screen.title))
                    .setLongLabel(getString(screen.subtitle))
                    .setIcon(IconCompat.createWithBitmap(imageBitmap.asAndroidBitmap()))
                    .setIntent(
                        Intent(context, AppActivityClass).apply {
                            action = SHORTCUT_OPEN_ACTION
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putExtra(SCREEN_ID_EXTRA, screen.id)
                        }
                    )
                    .build()

                val callbackIntent =
                    ShortcutManagerCompat.createShortcutResultIntent(context, info)

                val successCallback = PendingIntentCompat.getBroadcast(
                    context, 0, callbackIntent, 0, false
                )

                ShortcutManagerCompat.requestPinShortcut(
                    context,
                    info,
                    successCallback?.intentSender
                )
            } else {
                throw UnsupportedOperationException()
            }
        }.onFailure {
            onFailure(it)
        }
    }

    fun Context.canPinShortcuts(): Boolean =
        ShortcutManagerCompat.isRequestPinShortcutSupported(this)

    @SuppressLint("MissingPermission")
    fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            return connectivityManager.activeNetworkInfo?.isConnected == true
        }
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

    fun Context.shareUris(uris: List<Uri>) {
        if (uris.isEmpty()) return

        val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(
                    getExtension(uris.first())
                ) ?: "*/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(shareIntent)
    }

    fun Context.getExtension(uri: Uri): String? {
        val filename = getFilename(uri) ?: ""
        if (filename.endsWith(".qoi")) return "qoi"
        if (filename.endsWith(".jxl")) return "jxl"
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(
                    contentResolver.getType(uri)
                )
        } else {
            MimeTypeMap.getFileExtensionFromUrl(uri.toString()).lowercase(Locale.getDefault())
        }
    }

    val Context.density: Density
        get() = object : Density {
            override val density: Float
                get() = resources.displayMetrics.density
            override val fontScale: Float
                get() = resources.configuration.fontScale
        }
}