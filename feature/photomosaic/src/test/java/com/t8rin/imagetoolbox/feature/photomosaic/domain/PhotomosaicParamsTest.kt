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

package com.t8rin.imagetoolbox.feature.photomosaic.domain

import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import org.junit.Assert.assertEquals
import org.junit.Test

class PhotomosaicParamsTest {

    @Test
    fun valuesBelowLimitsAreClamped() {
        val result = PhotomosaicParams(
            columns = Int.MIN_VALUE,
            colorBlend = -1f,
            repeatDistance = Int.MIN_VALUE,
            maxTiles = Int.MIN_VALUE,
            outputScaleRatio = 0f
        ).normalized()

        assertEquals(PhotomosaicParams.MIN_COLUMNS, result.columns)
        assertEquals(0f, result.colorBlend)
        assertEquals(0, result.repeatDistance)
        assertEquals(PhotomosaicParams.MIN_TILES, result.maxTiles)
        assertEquals(0.1f, result.outputScaleRatio)
    }

    @Test
    fun valuesAboveLimitsAreClamped() {
        val result = PhotomosaicParams(
            columns = Int.MAX_VALUE,
            colorBlend = Float.MAX_VALUE,
            repeatDistance = Int.MAX_VALUE,
            maxTiles = Int.MAX_VALUE,
            outputScaleRatio = Float.MAX_VALUE
        ).normalized()

        assertEquals(PhotomosaicParams.MAX_COLUMNS, result.columns)
        assertEquals(PhotomosaicParams.MAX_COLOR_BLEND, result.colorBlend)
        assertEquals(PhotomosaicParams.MAX_REPEAT_DISTANCE, result.repeatDistance)
        assertEquals(PhotomosaicParams.MAX_TILES, result.maxTiles)
        assertEquals(1f, result.outputScaleRatio)
    }

    @Test
    fun validValuesRemainUnchanged() {
        val params = PhotomosaicParams(
            columns = 73,
            colorBlend = 0.37f,
            repeatDistance = 4,
            maxTiles = 240
        )

        assertEquals(params, params.normalized())
    }

    @Test
    fun outputScaleUpscalesSmallTargetsPreservingAspectRatio() {
        val target = IntegerSize(1021, 2227)

        assertEquals(IntegerSize(1878, 4096), PhotomosaicParams().outputSize(target))
        assertEquals(
            IntegerSize(939, 2048),
            PhotomosaicParams(outputScaleRatio = 0.5f).outputSize(target)
        )
    }

    @Test
    fun outputScaleDownscalesLargeTargetsPreservingAspectRatio() {
        val target = IntegerSize(8000, 4000)

        assertEquals(IntegerSize(4096, 2048), PhotomosaicParams().outputSize(target))
        assertEquals(
            IntegerSize(2048, 1024),
            PhotomosaicParams(outputScaleRatio = 0.5f).outputSize(target)
        )
    }
}
