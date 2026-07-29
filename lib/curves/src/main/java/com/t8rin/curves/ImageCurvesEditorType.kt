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

package com.t8rin.curves

enum class ImageCurvesEditorType(
    val title: String,
    internal val channelNames: List<String>,
    internal val curveOffset: Int,
    internal val usesHistogram: Boolean = false,
    internal val centeredCurve: Boolean = false
) {
    RGB(
        title = "RGB",
        channelNames = listOf("Luma", "R", "G", "B"),
        curveOffset = 0,
        usesHistogram = true
    ),
    CMYK(
        title = "CMYK",
        channelNames = listOf("C", "M", "Y", "K"),
        curveOffset = 4
    ),
    Lab(
        title = "Lab",
        channelNames = listOf("L", "a", "b"),
        curveOffset = 8
    ),
    HueVsSat(
        title = "Hue vs Sat",
        channelNames = listOf("Hue"),
        curveOffset = 11,
        centeredCurve = true
    ),
    HueVsHue(
        title = "Hue vs Hue",
        channelNames = listOf("Hue"),
        curveOffset = 12,
        centeredCurve = true
    ),
    HueVsLuma(
        title = "Hue vs Luma",
        channelNames = listOf("Hue"),
        curveOffset = 13,
        centeredCurve = true
    ),
    LumaVsSat(
        title = "Luma vs Sat",
        channelNames = listOf("Luma"),
        curveOffset = 14,
        centeredCurve = true
    ),
    LumaVsHue(
        title = "Luma vs Hue",
        channelNames = listOf("Luma"),
        curveOffset = 15,
        centeredCurve = true
    ),
    SatVsSat(
        title = "Sat vs Sat",
        channelNames = listOf("Sat"),
        curveOffset = 16,
        centeredCurve = true
    );

    internal val channelCount: Int
        get() = channelNames.size
}
