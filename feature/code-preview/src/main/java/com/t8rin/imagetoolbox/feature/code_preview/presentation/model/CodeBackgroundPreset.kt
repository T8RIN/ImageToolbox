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

package com.t8rin.imagetoolbox.feature.code_preview.presentation.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.core.resources.R

enum class CodeBackgroundPreset(
    @StringRes val title: Int,
    val startColor: Color,
    val endColor: Color
) {
    Aurora(
        title = R.string.code_background_aurora,
        startColor = Color(0xFF6D5DFB),
        endColor = Color(0xFF24C6DC)
    ),
    Sunset(
        title = R.string.code_background_sunset,
        startColor = Color(0xFFFF6B6B),
        endColor = Color(0xFFFFC371)
    ),
    Candy(
        title = R.string.code_background_candy,
        startColor = Color(0xFFB24592),
        endColor = Color(0xFFF15F79)
    ),
    Ocean(
        title = R.string.code_background_ocean,
        startColor = Color(0xFF005AA7),
        endColor = Color(0xFFFFFDE4)
    ),
    Lime(
        title = R.string.code_background_lime,
        startColor = Color(0xFF11998E),
        endColor = Color(0xFF38EF7D)
    ),
    Ember(
        title = R.string.code_background_ember,
        startColor = Color(0xFF8E2DE2),
        endColor = Color(0xFF4A00E0)
    ),
    Graphite(
        title = R.string.code_background_graphite,
        startColor = Color(0xFF232526),
        endColor = Color(0xFF414345)
    ),
    Paper(
        title = R.string.code_background_paper,
        startColor = Color(0xFFF8FAFC),
        endColor = Color(0xFFE2E8F0)
    ),
    Electric(
        title = R.string.code_background_electric,
        startColor = Color(0xFF7F00FF),
        endColor = Color(0xFFE100FF)
    ),
    Midnight(
        title = R.string.code_background_midnight,
        startColor = Color(0xFF0F2027),
        endColor = Color(0xFF2C5364)
    ),
    Synthwave(
        title = R.string.code_background_synthwave,
        startColor = Color(0xFFFF00CC),
        endColor = Color(0xFF333399)
    ),
    Peach(
        title = R.string.code_background_peach,
        startColor = Color(0xFFED4264),
        endColor = Color(0xFFFFB88C)
    ),
    Mango(
        title = R.string.code_background_mango,
        startColor = Color(0xFFF09819),
        endColor = Color(0xFFEDDE5D)
    ),
    Forest(
        title = R.string.code_background_forest,
        startColor = Color(0xFF134E5E),
        endColor = Color(0xFF71B280)
    ),
    Arctic(
        title = R.string.code_background_arctic,
        startColor = Color(0xFF74EBD5),
        endColor = Color(0xFFACB6E5)
    ),
    Rose(
        title = R.string.code_background_rose,
        startColor = Color(0xFFF4C4F3),
        endColor = Color(0xFFFC67FA)
    ),
    Custom(
        title = R.string.code_background_custom,
        startColor = Color(0xFF6D5DFB),
        endColor = Color(0xFF24C6DC)
    )
}
