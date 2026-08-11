/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t8rin.imagetoolbox.feature.photomosaic.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PhotomosaicParamsTest {

    @Test
    fun valuesBelowLimitsAreClamped() {
        val result = PhotomosaicParams(
            columns = Int.MIN_VALUE,
            colorBlend = -1f,
            repeatDistance = Int.MIN_VALUE,
            maxTiles = Int.MIN_VALUE
        ).normalized()

        assertEquals(PhotomosaicParams.MIN_COLUMNS, result.columns)
        assertEquals(0f, result.colorBlend)
        assertEquals(0, result.repeatDistance)
        assertEquals(PhotomosaicParams.MIN_TILES, result.maxTiles)
    }

    @Test
    fun valuesAboveLimitsAreClamped() {
        val result = PhotomosaicParams(
            columns = Int.MAX_VALUE,
            colorBlend = Float.MAX_VALUE,
            repeatDistance = Int.MAX_VALUE,
            maxTiles = Int.MAX_VALUE
        ).normalized()

        assertEquals(PhotomosaicParams.MAX_COLUMNS, result.columns)
        assertEquals(PhotomosaicParams.MAX_COLOR_BLEND, result.colorBlend)
        assertEquals(PhotomosaicParams.MAX_REPEAT_DISTANCE, result.repeatDistance)
        assertEquals(PhotomosaicParams.MAX_TILES, result.maxTiles)
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
}
