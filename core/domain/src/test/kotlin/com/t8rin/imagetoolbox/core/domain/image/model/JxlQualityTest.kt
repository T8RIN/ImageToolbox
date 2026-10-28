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

package com.t8rin.imagetoolbox.core.domain.image.model

import org.junit.Assert.assertEquals
import org.junit.Test

class JxlQualityTest {

    @Test
    fun jixelAcceptsAllSixEffortsWithoutChangingExistingValues() {
        listOf(ImageFormat.Jxl.JixelLossless, ImageFormat.Jxl.JixelLossy).forEach { format ->
            (1..6).forEach { effort ->
                val quality = Quality.Jxl(effort = effort)
                assertEquals(quality, quality.coerceIn(format))
            }
            assertEquals(1, (Quality.Jxl(effort = 0).coerceIn(format) as Quality.Jxl).effort)
            assertEquals(6, (Quality.Jxl(effort = 10).coerceIn(format) as Quality.Jxl).effort)
        }
        assertEquals(2, Quality.Jxl().effort)
    }

    @Test
    fun libjxlRetainsItsOwnEffortRange() {
        listOf(ImageFormat.Jxl.Lossless, ImageFormat.Jxl.Lossy).forEach { format ->
            (1..10).forEach { effort ->
                val quality = Quality.Jxl(effort = effort)
                assertEquals(quality, quality.coerceIn(format))
            }
        }
    }
}