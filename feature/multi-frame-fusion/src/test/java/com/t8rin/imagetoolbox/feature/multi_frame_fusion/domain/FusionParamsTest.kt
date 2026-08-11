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

package com.t8rin.imagetoolbox.feature.multi_frame_fusion.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class FusionParamsTest {
    @Test
    fun normalizedClampsValuesAndKeepsFocusRadiusOdd() {
        val normalized = FusionParams(
            contrastWeight = -1f,
            saturationWeight = 3f,
            exposureWeight = 4f,
            focusRadius = 8,
            focusStrength = 20f,
            lightTrailThreshold = 2f,
            trailStrength = -1f,
            trailPersistence = 4f
        ).normalized()

        assertEquals(FusionParams.MIN_WEIGHT, normalized.contrastWeight)
        assertEquals(FusionParams.MAX_WEIGHT, normalized.saturationWeight)
        assertEquals(FusionParams.MAX_WEIGHT, normalized.exposureWeight)
        assertEquals(9, normalized.focusRadius)
        assertEquals(FusionParams.MAX_FOCUS_STRENGTH, normalized.focusStrength)
        assertEquals(FusionParams.MAX_LIGHT_TRAIL_THRESHOLD, normalized.lightTrailThreshold)
        assertEquals(FusionParams.MIN_TRAIL_STRENGTH, normalized.trailStrength)
        assertEquals(FusionParams.MAX_TRAIL_PERSISTENCE, normalized.trailPersistence)
    }

}
