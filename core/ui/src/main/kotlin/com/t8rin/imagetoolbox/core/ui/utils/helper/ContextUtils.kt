/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
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
import android.provider.Settings
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.Density
import androidx.core.app.ActivityCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import com.t8rin.imagetoolbox.core.domain.model.PerformanceClass
import com.t8rin.imagetoolbox.core.domain.utils.FileMode
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.image_vector.toImageBitmap
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionStatus
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.askUserToRequestPermissionExplicitly
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.checkPermissions
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.hasPermissionAllowed
import com.t8rin.imagetoolbox.core.ui.utils.permission.PermissionUtils.setPermissionsAllowed
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.logger.makeLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.math.ceil
import kotlin.random.Random


object ContextUtils {

    fun Activity.requestStoragePermission() = requestPermissions(
        permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    fun Activity.requestPermissions(permissions: List<String>) {
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

    @Composable
    fun rememberFilename(uri: Uri): String? {
        return remember(uri) {
            derivedStateOf {
                uri.getFilename()
            }
        }.value
    }

    @Composable
    fun rememberFileExtension(uri: Uri): String? {
        return remember(uri) {
            derivedStateOf {
                uri.getExtension()
            }
        }.value
    }


    val Context.performanceClass: PerformanceClass
        get() {
            val androidVersion = Build.VERSION.SDK_INT
            val cpuCount = Runtime.getRuntime().availableProcessors()
            val memoryClass = getSystemService<ActivityManager>()!!.memoryClass
            var totalCpuFreq = 0
            var freqResolved = 0
            for (i in 0 until cpuCount) {
                runCatching {
                    val reader = RandomAccessFile(
                        String.format(
                            Locale.ENGLISH,
                            "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq",
                            i
                        ), FileMode.Read.mode
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

    fun Context.getStringLocalized(
        @StringRes
        resId: Int,
        locale: Locale,
    ): String = createConfigurationContext(
        Configuration(resources.configuration).apply { setLocale(locale) }
    ).getText(resId).toString()

    fun Context.pasteColorFromClipboard(
        onPastedColor: (Color) -> Unit,
        onPastedColorFailure: (String) -> Unit,
    ) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text?.toString()
        text?.let {
            runCatching {
                onPastedColor(Color(it.toColorInt()))
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

    fun getDisplayName(
        lang: String?,
        useDefaultLocale: Boolean = false
    ): String {
        if (lang == null) {
            return ""
        }

        val locale = when (lang) {
            "" -> LocaleListCompat.getAdjustedDefault()[0]
            else -> Locale.forLanguageTag(lang)
        }
        return locale!!.getDisplayName(
            if (useDefaultLocale) Locale.getDefault()
            else locale
        ).replaceFirstChar { it.uppercase(locale) }
    }

    private const val SCREEN_ID_EXTRA = "screen_id"
    const val SHORTCUT_OPEN_ACTION = "shortcut"

    fun Intent?.getScreenExtra(): Screen? {
        if (this?.hasExtra(SCREEN_ID_EXTRA) != true) return null

        val screenIdExtra = getIntExtra(SCREEN_ID_EXTRA, -100).takeIf {
            it != -100
        } ?: return null

        return Screen.entries.find {
            it.id == screenIdExtra
        }
    }

    fun Intent.putScreenExtra(screen: Screen?) = apply {
        if (screen == null) {
            removeExtra(SCREEN_ID_EXTRA)
        } else {
            putExtra(SCREEN_ID_EXTRA, screen.id)
        }
    }

    fun Intent?.getScreenOpeningShortcut(
        onNavigate: (Screen) -> Unit,
    ): Boolean {
        if (this == null) return false

        val screenExtra = getScreenExtra()

        if (action == SHORTCUT_OPEN_ACTION && screenExtra != null) {
            onNavigate(screenExtra)

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
            if (context.canPinShortcuts() && screen.icon != null) {
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
                        context.buildIntent(AppActivityClass) {
                            action = SHORTCUT_OPEN_ACTION
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putScreenExtra(screen)
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
                onFailure(UnsupportedOperationException())
            }
        }.onFailure(onFailure)
    }

    fun Context.canPinShortcuts(): Boolean = runCatching {
        ShortcutManagerCompat.isRequestPinShortcutSupported(this)
    }.getOrNull() == true

    @SuppressLint("MissingPermission")
    fun Context.isNetworkAvailable(): Boolean {
        return getSystemService<ConnectivityManager>()?.run {
            val capabilities = getNetworkCapabilities(
                activeNetwork ?: return false
            ) ?: return false

            possibleCapabilities.any(capabilities::hasTransport)
        } ?: false
    }

    private val possibleCapabilities = listOf(
        NetworkCapabilities.TRANSPORT_WIFI,
        NetworkCapabilities.TRANSPORT_CELLULAR,
        NetworkCapabilities.TRANSPORT_ETHERNET,
        NetworkCapabilities.TRANSPORT_BLUETOOTH
    )

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
                    uris.first().getExtension()
                ) ?: "*/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(shareIntent)
    }

    fun Uri.getExtension(): String? = runCatching {
        val filename = getFilename().orEmpty()
        if (filename.endsWith(".qoi")) return "qoi"
        if (filename.endsWith(".jxl")) return "jxl"
        return if (ContentResolver.SCHEME_CONTENT == scheme) {
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(
                    appContext.contentResolver.getType(this@getExtension)
                )
        } else {
            MimeTypeMap.getFileExtensionFromUrl(this@getExtension.toString())
                .lowercase(Locale.getDefault())
        }
    }.getOrNull()

    val Context.density: Density
        get() = object : Density {
            override val density: Float
                get() = resources.displayMetrics.density
            override val fontScale: Float
                get() = resources.configuration.fontScale
        }

    @RequiresApi(Build.VERSION_CODES.R)
    fun manageAllFilesIntent() = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)

    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.manageAppAllFilesIntent(): Intent {
        return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            .setData("package:${packageName}".toUri())
    }

    fun Context.appSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData("package:${packageName}".toUri())
    }

    fun Uri.takePersistablePermission(): Uri = apply {
        runCatching {
            appContext.contentResolver.takePersistableUriPermission(
                this,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }.onFailure {
            it.makeLog("takePersistablePermission")
        }
    }

    fun Uri.moveToCache(): Uri? = appContext.run {
        contentResolver.openInputStream(this@moveToCache)?.use { stream ->
            val file = File(
                cacheDir,
                getFilename() ?: "cache_${Random.nextInt()}.tmp"
            ).apply { createNewFile() }

            file.outputStream().use { stream.copyTo(it) }

            file.toUri()
        }
    }

    fun Uri.isFromAppFileProvider() =
        toString().contains(appContext.getString(R.string.file_provider))

}