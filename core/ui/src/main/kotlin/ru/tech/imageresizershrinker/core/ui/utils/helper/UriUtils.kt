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

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.coroutineScope
import ru.tech.imageresizershrinker.core.domain.model.FileModel
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.model.SortType
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import java.net.URLDecoder


fun Uri?.toUiPath(
    context: Context,
    default: String
): String = this?.let { uri ->
    DocumentFile
        .fromTreeUri(context, uri)
        ?.uri?.path?.split(":")
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
} ?: default

fun Uri.lastModified(context: Context): Long? = with(context.contentResolver) {
    val query = query(this@lastModified, null, null, null, null)

    query?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnNames = listOf(
                DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                "datetaken", // When sharing an Image from Google Photos into the app.
            )

            val millis = columnNames.firstNotNullOfOrNull {
                val index = cursor.getColumnIndex(it)
                if (!cursor.isNull(index)) {
                    cursor.getLong(index)
                } else {
                    null
                }
            }

            return millis
        }
    }

    return null
}

suspend fun List<Uri>.sortedByType(
    sortType: SortType,
    context: Context
): List<Uri> = coroutineScope {
    when (sortType) {
        SortType.Date -> sortedByDate(context)
        SortType.DateReversed -> sortedByDate(context).reversed()
        SortType.Name -> sortedByName(context)
        SortType.NameReversed -> sortedByName(context).reversed()
    }
}

fun List<Uri>.sortedByDate(
    context: Context
): List<Uri> = sortedBy {
    it.lastModified(context)
}

fun List<Uri>.sortedByName(
    context: Context
): List<Uri> = sortedBy {
    context.getFilename(it)
}

fun ImageModel.toUri(): Uri? = when (data) {
    is Uri -> data as Uri
    is String -> (data as String).toUri()
    else -> null
}

fun Any.toImageModel() = ImageModel(this)

fun String.toFileModel() = FileModel(this)

fun String.decodeEscaped(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        URLDecoder.decode(URLDecoder.decode(this, Charsets.UTF_8), Charsets.UTF_8)
    } else {
        @Suppress("DEPRECATION")
        URLDecoder.decode(URLDecoder.decode(this))
    }
}

fun Uri.isApng(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".png")
        .or(context.contentResolver.getType(this)?.contains("png") == true)
        .or(context.contentResolver.getType(this)?.contains("apng") == true)
}

fun Uri.isWebp(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".webp")
        .or(context.contentResolver.getType(this)?.contains("webp") == true)
}

fun Uri.isJxl(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".jxl")
        .or(context.contentResolver.getType(this)?.contains("jxl") == true)
}