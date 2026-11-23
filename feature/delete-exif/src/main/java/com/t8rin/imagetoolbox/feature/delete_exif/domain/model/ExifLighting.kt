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

internal enum class ExifLighting(
    val shutterSpeeds: List<Int>,
    val isoValues: List<Int>,
    val maxAperture: Int
) {
    Auto(emptyList(), emptyList(), 0),
    Daylight(listOf(250, 500, 1000), listOf(100, 125, 200), 1100),
    Indoors(listOf(60, 80, 125), listOf(400, 640, 800), 800),
    Night(listOf(15, 30, 60), listOf(800, 1600, 3200), 560)
}