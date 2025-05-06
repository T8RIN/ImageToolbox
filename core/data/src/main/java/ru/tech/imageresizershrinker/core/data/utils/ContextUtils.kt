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

package ru.tech.imageresizershrinker.core.data.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.ui.unit.Density
import androidx.core.content.ContextCompat
import kotlinx.coroutines.coroutineScope
import ru.tech.imageresizershrinker.core.data.image.toMetadata
import ru.tech.imageresizershrinker.core.domain.image.Metadata
import ru.tech.imageresizershrinker.core.domain.image.clearAllAttributes
import ru.tech.imageresizershrinker.core.domain.image.copyTo
import ru.tech.imageresizershrinker.core.domain.utils.humanFileSize
import ru.tech.imageresizershrinker.core.domain.utils.runSuspendCatching
import java.io.OutputStream

fun Context.isExternalStorageWritable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
    else ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

suspend fun Context.copyMetadata(
    initialExif: Metadata?,
    fileUri: Uri?,
    keepOriginalMetadata: Boolean,
    originalUri: Uri
) = runSuspendCatching {
    if (initialExif != null) {
        getFileDescriptorFor(fileUri)?.use {
            initialExif.copyTo(it.fileDescriptor.toMetadata())
        }
    } else if (keepOriginalMetadata) {
        val newUri = originalUri.tryRequireOriginal(this)
        val exif = contentResolver
            .openFileDescriptor(newUri, "r")
            ?.use { it.fileDescriptor.toMetadata() }

        getFileDescriptorFor(fileUri)?.use {
            exif?.copyTo(it.fileDescriptor.toMetadata())
        }
    } else {
        getFileDescriptorFor(fileUri)?.use {
            it.fileDescriptor.toMetadata().apply {
                clearAllAttributes()
                saveAttributes()
            }
        }
    }
}

fun Context.openWriteableStream(
    uri: Uri?,
    onFailure: (Throwable) -> Unit = {}
): OutputStream? = uri?.let {
    runCatching {
        contentResolver.openOutputStream(uri, "rw")
    }.getOrElse {
        runCatching {
            contentResolver.openOutputStream(uri, "w")
        }.onFailure(onFailure).getOrNull()
    }
}

internal suspend fun Context.clearCache() = coroutineScope {
    runCatching {
        cacheDir?.deleteRecursively()
        codeCacheDir?.deleteRecursively()
        externalCacheDir?.deleteRecursively()
        externalCacheDirs?.forEach {
            it.deleteRecursively()
        }
    }
}

internal fun Context.cacheSize(): String = runCatching {
    val cache =
        cacheDir?.walkTopDown()?.sumOf { if (it.isFile) it.length() else 0 } ?: 0
    val code =
        codeCacheDir?.walkTopDown()?.sumOf { if (it.isFile) it.length() else 0 } ?: 0
    var size = cache + code
    externalCacheDirs?.forEach { file ->
        size += file?.walkTopDown()?.sumOf { if (it.isFile) it.length() else 0 } ?: 0
    }
    humanFileSize(size)
}.getOrNull() ?: "0 B"


fun Context.isInstalledFromPlayStore(): Boolean = verifyInstallerId(
    listOf(
        "com.android.vending",
        "com.google.android.feedback"
    )
)

private fun Context.verifyInstallerId(
    validInstallers: List<String>,
): Boolean = validInstallers.contains(getInstallerPackageName(packageName))

private fun Context.getInstallerPackageName(
    packageName: String
): String? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        packageManager.getInstallSourceInfo(packageName).installingPackageName
    } else {
        @Suppress("DEPRECATION")
        packageManager.getInstallerPackageName(packageName)
    }
}.getOrNull()

val Context.density: Density
    get() = object : Density {
        override val density: Float
            get() = resources.displayMetrics.density
        override val fontScale: Float
            get() = resources.configuration.fontScale
    }