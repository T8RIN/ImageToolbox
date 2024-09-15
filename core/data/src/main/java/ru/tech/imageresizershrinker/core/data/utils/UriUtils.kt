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

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import okio.use
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.resources.R

fun ImageModel.toUri(): Uri? = when (data) {
    is Uri -> data as Uri
    is String -> (data as String).toUri()
    else -> null
}

fun Uri.fileSize(context: Context): Long? {
    runCatching {
        context.contentResolver
            .query(this, null, null, null, null, null)
            .use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (!cursor.isNull(sizeIndex)) {
                        return cursor.getLong(sizeIndex)
                    }
                }
            }
    }
    return null
}

fun String?.getPath(
    context: Context
) = this?.takeIf { it.isNotEmpty() }?.toUri().toUiPath(
    context = context,
    default = context.getString(R.string.default_folder)
)

fun Uri?.toUiPath(
    context: Context,
    default: String,
    isTreeUri: Boolean = true
): String = this?.let { uri ->
    runCatching {
        val document = if (isTreeUri) DocumentFile.fromTreeUri(context, uri)
        else DocumentFile.fromSingleUri(context, uri)

        document?.uri?.path?.split(":")
            ?.lastOrNull()?.let { p ->
                val endPath = p.takeIf {
                    it.isNotEmpty()
                }?.let { "/$it" } ?: ""
                val startPath = if (
                    uri.toString()
                        .split("%")[0]
                        .contains("primary")
                ) context.getString(R.string.device_storage)
                else context.getString(R.string.external_storage)

                startPath + endPath
            }
    }.getOrNull()
} ?: default

fun Context.getFileDescriptorFor(
    uri: Uri?
): ParcelFileDescriptor? = uri?.let {
    runCatching {
        contentResolver.openFileDescriptor(uri, "rw")
    }.getOrNull()
}

internal fun Uri.tryGetLocation(context: Context): Uri {
    val tempUri = this
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        runCatching {
            MediaStore.setRequireOriginal(this).also {
                context.contentResolver.openFileDescriptor(it, "r")?.close()
            }
        }.getOrNull() ?: tempUri
    } else this
}

fun Uri.getFilename(
    context: Context
): String? = DocumentFile.fromSingleUri(context, this)?.name