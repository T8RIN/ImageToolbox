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

package com.t8rin.imagetoolbox.feature.multi_frame_fusion.data

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.FusionParams
import com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain.MultiFrameFusionProcessor
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidMultiFrameFusionProcessor @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    private val engine: MultiFrameFusionEngine,
    dispatchersHolder: DispatchersHolder
) : MultiFrameFusionProcessor<Bitmap>, DispatchersHolder by dispatchersHolder {

    override suspend fun fuse(
        imageUris: List<String>,
        params: FusionParams,
        preview: Boolean,
        onProgress: (done: Int, total: Int) -> Unit
    ): Bitmap? = withContext(defaultDispatcher) {
        val uris = imageUris.distinct().take(FusionParams.MAX_IMAGES)
        if (uris.size < FusionParams.MIN_IMAGES) return@withContext null

        val total = uris.size * 2 + 1
        val bitmaps = mutableListOf<Bitmap>()

        uris.forEachIndexed { index, uri ->
            coroutineContext.ensureActive()
            val bitmap = if (preview) {
                imageGetter.getImage(uri, size = PREVIEW_SIZE)
            } else {
                imageGetter.getImage(data = uri, originalSize = true)
            }
            bitmap?.let(bitmaps::add)
            onProgress(index + 1, total)
        }
        if (bitmaps.size < FusionParams.MIN_IMAGES) return@withContext null

        engine.fuse(
            bitmaps = bitmaps,
            params = params,
            onFrameAligned = { aligned ->
                onProgress(uris.size + aligned, total)
            }
        ).also {
            onProgress(total, total)
        }
    }

    private companion object {
        const val PREVIEW_SIZE = 1024
    }
}
