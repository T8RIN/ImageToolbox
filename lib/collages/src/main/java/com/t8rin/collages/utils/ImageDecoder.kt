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
import android.net.Uri
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.t8rin.collages.public.CollageConstants.requestMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object ImageDecoder {
    var SAMPLER_SIZE = 1536

    suspend fun decodeFileToBitmap(
        context: Context,
        pathName: Uri
    ): Bitmap? = withContext(Dispatchers.IO) {
        val stringKey = pathName.toString() + SAMPLER_SIZE + "ImageDecoder"
        val key = MemoryCache.Key(stringKey)

        context.imageLoader.memoryCache?.get(key)?.image?.toBitmap() ?: context.imageLoader.execute(
            ImageRequest.Builder(context)
                .allowHardware(false)
                .diskCacheKey(stringKey)
                .memoryCacheKey(key)
                .data(pathName)
                .size(SAMPLER_SIZE)
                .run(requestMapper)
                .build()
        ).image?.toBitmap()?.apply {
            if (config != Bitmap.Config.ARGB_8888) {
                setConfig(Bitmap.Config.ARGB_8888)
            }
        }
    }

}
