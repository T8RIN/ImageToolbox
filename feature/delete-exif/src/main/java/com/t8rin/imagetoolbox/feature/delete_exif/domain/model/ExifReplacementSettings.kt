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

package com.t8rin.imagetoolbox.feature.delete_exif.domain.model

import com.t8rin.imagetoolbox.core.domain.utils.toCoordinateOrNull
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random

internal data class ExifReplacementSettings(
    val lighting: ExifLighting = ExifLighting.Auto,
    val metering: ExifMetering = ExifMetering.MultiSegment,
    val includeSoftware: Boolean = true,
    val location: ExifLocation = ExifLocation.None,
    val latitude: String = "",
    val longitude: String = "",
    val captureTime: LocalDateTime? = null,
    val sameForBatch: Boolean = false,
    val seed: Long = Random.nextLong(),
    val now: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
) {
    val coordinates: Pair<Double, Double>?
        get() {
            val latitude = latitude.toCoordinateOrNull(90) ?: return null
            val longitude = longitude.toCoordinateOrNull(180) ?: return null
            return latitude to longitude
        }
}