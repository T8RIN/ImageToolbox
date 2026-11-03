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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import android.util.LruCache
import com.t8rin.gmic.Gmic
import com.t8rin.gmic.filters.Displacement
import com.t8rin.gmic.filters.DisplacementBoundary as GmicDisplacementBoundary
import com.t8rin.gmic.filters.DisplacementInterpolation as GmicDisplacementInterpolation
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Displacement.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.DisplacementBoundary
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.DisplacementInterpolation
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.image.loadBitmap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class DisplacementFilter(
    override val value: Value
) : FilterTransformation, Filter.Displacement {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val requestedSize = maxOf(input.width, input.height)
        val horizontalMap = DisplacementMapCache.load(value.horizontalMap, requestedSize)
            ?: return input
        val verticalMap = value.verticalMap?.let {
            DisplacementMapCache.load(it, requestedSize) ?: return input
        }

        return Gmic.runCancellable(
            input = input,
            filter = Displacement(
                horizontalMap = horizontalMap,
                verticalMap = verticalMap,
                strengthX = value.strengthX,
                strengthY = value.strengthY,
                interpolation = value.interpolation.toGmicInterpolation(),
                boundary = value.boundary.toGmicBoundary()
            )
        )
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.displacement, R.string.filter_help_displacement)

        override val parameters = structuredParameters {
            Value::horizontalMap.image(R.string.horizontal_displacement_map) { ImageModel("") }
            Value::verticalMap.editor(control(R.string.vertical_displacement_map, 0f..0f)) { null }
            Value::strengthX.float(R.string.displacement_strength_x, 0.1f, -1f..1f, roundTo = 3)
            Value::strengthY.float(R.string.displacement_strength_y, 0.1f, -1f..1f, roundTo = 3)
            Value::interpolation.enum(
                R.string.gmic_param_interpolation,
                label = {
                    when (it) {
                        DisplacementInterpolation.Nearest -> R.string.nearest
                        DisplacementInterpolation.Linear -> R.string.linear
                        DisplacementInterpolation.Cubic -> R.string.cubic
                    }
                }
            ) { DisplacementInterpolation.Linear }
            Value::boundary.enum(
                R.string.gmic_param_boundary,
                label = {
                    when (it) {
                        DisplacementBoundary.Transparent -> R.string.displacement_boundary_transparent
                        DisplacementBoundary.Clamp -> R.string.tile_mode_clamp
                        DisplacementBoundary.Wrap -> R.string.wrap
                        DisplacementBoundary.Mirror -> R.string.tile_mode_mirror
                    }
                }
            ) { DisplacementBoundary.Clamp }
        }
    }
}

private object DisplacementMapCache {
    private val cache = object : LruCache<String, Bitmap>(32 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.allocationByteCount
    }
    private val mutex = Mutex()

    suspend fun load(model: ImageModel, requestedSize: Int): Bitmap? = mutex.withLock {
        val key = "${model.data}@$requestedSize"
        cache.get(key)?.takeUnless(Bitmap::isRecycled)?.let { return it }

        val bitmap = try {
            model.data.loadBitmap(requestedSize)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Throwable) {
            null
        } ?: return null

        cache.put(key, bitmap)
        bitmap
    }
}

private fun DisplacementInterpolation.toGmicInterpolation() =
    when (this) {
        DisplacementInterpolation.Nearest -> GmicDisplacementInterpolation.Nearest

        DisplacementInterpolation.Linear -> GmicDisplacementInterpolation.Linear

        DisplacementInterpolation.Cubic -> GmicDisplacementInterpolation.Cubic
    }

private fun DisplacementBoundary.toGmicBoundary() =
    when (this) {
        DisplacementBoundary.Transparent -> GmicDisplacementBoundary.Transparent

        DisplacementBoundary.Clamp -> GmicDisplacementBoundary.Clamp

        DisplacementBoundary.Wrap -> GmicDisplacementBoundary.Wrap

        DisplacementBoundary.Mirror -> GmicDisplacementBoundary.Mirror
    }