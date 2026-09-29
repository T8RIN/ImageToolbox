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

package com.t8rin.collages.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Precision
import coil3.size.Size
import coil3.toBitmap
import com.t8rin.collages.public.CollageConstants.requestMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object ImageDecoder {
    var SAMPLER_SIZE = 1536

    suspend fun sourceMaxDimension(context: Context, pathName: Uri): Int =
        withContext(Dispatchers.IO) {
            val bounds = runCatching {
                context.contentResolver.openInputStream(pathName)?.use { input ->
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(input, null, options)
                    maxOf(options.outWidth, options.outHeight)
                }
            }.getOrNull()?.takeIf { it > 0 }
            bounds ?: runCatching {
                context.imageLoader.execute(
                    ImageRequest.Builder(context)
                        .data(pathName)
                        .allowHardware(true)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .size(Size.ORIGINAL)
                        .run(requestMapper)
                        .build()
                ).image?.let { maxOf(it.width, it.height) }
            }.getOrNull()?.takeIf { it > 0 } ?: 0
        }

    suspend fun decodeFileToBitmap(
        context: Context,
        pathName: Uri,
        size: Int = SAMPLER_SIZE,
        cache: Boolean = true
    ): Bitmap? = withContext(Dispatchers.IO) {
        val stringKey = pathName.toString() + size + "ImageDecoder"
        val key = MemoryCache.Key(stringKey)

        (if (cache) context.imageLoader.memoryCache?.get(key)?.image?.toBitmap() else null)
            ?: context.imageLoader.execute(
                ImageRequest.Builder(context)
                    .allowHardware(false)
                    .diskCacheKey(stringKey)
                    .memoryCacheKey(key)
                    .memoryCachePolicy(if (cache) CachePolicy.ENABLED else CachePolicy.DISABLED)
                    .precision(Precision.INEXACT)
                    .data(pathName)
                    .size(size)
                    .run(requestMapper)
                    .build()
            ).image?.toBitmap()?.apply {
                if (config != Bitmap.Config.ARGB_8888) {
                    setConfig(Bitmap.Config.ARGB_8888)
                }
            }
    }

}
