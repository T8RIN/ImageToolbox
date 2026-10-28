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

class AvifQualityTest {

    @Test
    fun allAvifFormatsAcceptFourEfforts() {
        ImageFormat.entries.filterIsInstance<ImageFormat.Avif>().forEach { format ->
            (0..3).forEach { effort ->
                val quality = Quality.Avif(effort = effort)
                assertEquals(quality, quality.coerceIn(format))
            }
            assertEquals(0, (Quality.Avif(effort = -1).coerceIn(format) as Quality.Avif).effort)
            assertEquals(3, (Quality.Avif(effort = 9).coerceIn(format) as Quality.Avif).effort)
            assertEquals(
                0..3,
                format.compressionTypes.filterIsInstance<ImageFormat.CompressionType.Effort>()
                    .single().compressionRange
            )
        }
    }
}