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

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat

@Immutable
data class CodePreviewParams(
    val code: String = DefaultCode,
    val language: CodeLanguage = CodeLanguage.Kotlin,
    val theme: CodePreviewTheme = CodePreviewTheme.Dracula,
    val backgroundPreset: CodeBackgroundPreset = CodeBackgroundPreset.Aurora,
    val backgroundStartColor: Color = CodeBackgroundPreset.Aurora.startColor,
    val backgroundEndColor: Color = CodeBackgroundPreset.Aurora.endColor,
    val title: String = "Main.kt",
    val fontSize: Int = 15,
    val outerPadding: Int = 28,
    val innerPadding: Int = 20,
    val cornerRadius: Int = 18,
    val canvasCornerRadius: Int = 24,
    val rotation: Float = 0f,
    val showCardShadow: Boolean = true,
    val cardShadowColor: Color = Color(0x66000000),
    val cardShadowBlurRadius: Int = 14,
    val cardShadowOffsetX: Int = 0,
    val cardShadowOffsetY: Int = 5,
    val showCanvasBackground: Boolean = true,
    val showWindowControls: Boolean = true,
    val showLineNumbers: Boolean = true,
    val showTitle: Boolean = true,
    val wrapLongLines: Boolean = true,
    val outputFormat: ImageFormat = ImageFormat.Png.Lossless
) {
    companion object {
        val Default = CodePreviewParams()

        const val DefaultCode = """fun main() {
    val toolbox = ImageToolbox(
        fast = true,
        private = true
    )

    println(toolbox.create())
}"""
    }
}
