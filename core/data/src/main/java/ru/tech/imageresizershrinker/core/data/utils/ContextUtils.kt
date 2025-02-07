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
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.coroutineScope
import ru.tech.imageresizershrinker.core.domain.image.model.MetadataTag
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.domain.utils.runSuspendCatching
import java.io.OutputStream
import kotlin.io.use

fun Context.isExternalStorageWritable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
    else ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

suspend fun Context.copyMetadata(
    initialExif: ExifInterface?,
    fileUri: Uri?,
    keepMetadata: Boolean,
    originalUri: Uri
) = runSuspendCatching {
    if (initialExif != null) {
        getFileDescriptorFor(fileUri)?.use {
            val ex = ExifInterface(it.fileDescriptor)
            initialExif.copyTo(ex)
            ex.saveAttributes()
        }
    } else if (keepMetadata) {
        val newUri = originalUri.tryRequireOriginal(this)
        val exif = contentResolver
            .openFileDescriptor(newUri, "r")
            ?.use { ExifInterface(it.fileDescriptor) }

        getFileDescriptorFor(fileUri)?.use {
            val ex = ExifInterface(it.fileDescriptor)
            exif?.copyTo(ex)
            ex.saveAttributes()
        }
    } else {
        getFileDescriptorFor(fileUri)?.use {
            val ex = ExifInterface(it.fileDescriptor)
            MetadataTag.entries.forEach { tag ->
                ex.setAttribute(tag.key, null)
            }
            ex.saveAttributes()
        }
    }
}

suspend infix fun ExifInterface.copyTo(
    newExif: ExifInterface
) = coroutineScope {
    MetadataTag.entries.forEach { attr ->
        getAttribute(attr.key).let { newExif.setAttribute(attr.key, it) }
    }
    newExif.saveAttributes()
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
    readableByteCount(size)
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