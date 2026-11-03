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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientBackgroundPreset

internal data class CodePreviewStyles(
    val styles: Map<String, CodePreviewStyle> = emptyMap()
)

internal data class CodePreviewStyle(
    val themeAssetName: String = DefaultParams.theme.assetName,
    val backgroundPreset: GradientBackgroundPreset = DefaultParams.backgroundPreset,
    val backgroundColors: List<Int?> = DefaultParams.backgroundColors.map(Color::toArgb),
    val fontSize: Int = DefaultParams.fontSize,
    val outerPadding: Int = DefaultParams.outerPadding,
    val innerPadding: Int = DefaultParams.innerPadding,
    val cornerRadius: Int = DefaultParams.cornerRadius,
    val canvasCornerRadius: Int = DefaultParams.canvasCornerRadius,
    val rotation: Float = DefaultParams.rotation,
    val showCardShadow: Boolean = DefaultParams.showCardShadow,
    val cardShadowColor: Int = DefaultParams.cardShadowColor.toArgb(),
    val cardShadowBlurRadius: Int = DefaultParams.cardShadowBlurRadius,
    val cardShadowOffsetX: Int = DefaultParams.cardShadowOffsetX,
    val cardShadowOffsetY: Int = DefaultParams.cardShadowOffsetY,
    val showCanvasBackground: Boolean = DefaultParams.showCanvasBackground,
    val showWindowControls: Boolean = DefaultParams.showWindowControls,
    val showLineNumbers: Boolean = DefaultParams.showLineNumbers,
    val showTitle: Boolean = DefaultParams.showTitle,
    val wrapLongLines: Boolean = DefaultParams.wrapLongLines
) {
    fun applyTo(params: CodePreviewParams): CodePreviewParams = params.copy(
        theme = CodePreviewTheme.entries.firstOrNull { it.assetName == themeAssetName }
            ?: DefaultParams.theme,
        backgroundPreset = backgroundPreset,
        backgroundColors = backgroundColors.filterNotNull()
            .takeIf { it.size >= 2 }
            ?.map(::Color)
            ?: DefaultParams.backgroundColors,
        fontSize = fontSize,
        outerPadding = outerPadding,
        innerPadding = innerPadding,
        cornerRadius = cornerRadius,
        canvasCornerRadius = canvasCornerRadius,
        rotation = rotation,
        showCardShadow = showCardShadow,
        cardShadowColor = Color(cardShadowColor),
        cardShadowBlurRadius = cardShadowBlurRadius,
        cardShadowOffsetX = cardShadowOffsetX,
        cardShadowOffsetY = cardShadowOffsetY,
        showCanvasBackground = showCanvasBackground,
        showWindowControls = showWindowControls,
        showLineNumbers = showLineNumbers,
        showTitle = showTitle,
        wrapLongLines = wrapLongLines
    )
}

internal fun CodePreviewParams.toStyle() = CodePreviewStyle(
    themeAssetName = theme.assetName,
    backgroundPreset = backgroundPreset,
    backgroundColors = backgroundColors.map(Color::toArgb),
    fontSize = fontSize,
    outerPadding = outerPadding,
    innerPadding = innerPadding,
    cornerRadius = cornerRadius,
    canvasCornerRadius = canvasCornerRadius,
    rotation = rotation,
    showCardShadow = showCardShadow,
    cardShadowColor = cardShadowColor.toArgb(),
    cardShadowBlurRadius = cardShadowBlurRadius,
    cardShadowOffsetX = cardShadowOffsetX,
    cardShadowOffsetY = cardShadowOffsetY,
    showCanvasBackground = showCanvasBackground,
    showWindowControls = showWindowControls,
    showLineNumbers = showLineNumbers,
    showTitle = showTitle,
    wrapLongLines = wrapLongLines
)

private val DefaultParams = CodePreviewParams.Default
