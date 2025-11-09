/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model

import com.smarttoolfactory.colordetector.ImageColorPaletteState
import com.smarttoolfactory.colordetector.PaletteData
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import org.json.JSONArray
import org.json.JSONObject

data class UiPalette(
    private val data: List<PaletteData>
) {
    private val jsonValue by lazy {
        JSONArray().apply {
            data.forEach {
                put(
                    JSONObject().apply {
                        put("name", it.colorData.name)
                        put("color", it.colorData.color.toHex())
                        put("population_percent", it.percent)
                    }
                )
            }
        }.toString()
    }

    fun asJson(): String = jsonValue
}

fun ImageColorPaletteState.toUiPalette(): UiPalette = UiPalette(paletteData)