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

package com.t8rin.imagetoolbox.feature.screenshot_framing.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientBackgroundPreset

internal data class ScreenshotFramingStyles(
    val styles: Map<String, ScreenshotFramingStyle> = emptyMap()
)

internal data class ScreenshotFramingStyle(
    val preset: GradientBackgroundPreset = GradientBackgroundPreset.Custom,
    val backgroundColors: List<Int?> = DefaultParams.backgroundColors.map(Color::toArgb),
    val useGradient: Boolean = DefaultParams.useGradient,
    val aspectRatioWidth: Float = DefaultParams.aspectRatio.widthProportion,
    val aspectRatioHeight: Float = DefaultParams.aspectRatio.heightProportion,
    val customAspectRatio: Boolean = false,
    val savedAspectRatio: Boolean = false,
    val padding: Int = DefaultParams.padding,
    val cornerRadius: Int = DefaultParams.cornerRadius,
    val showShadow: Boolean = DefaultParams.showShadow,
    val shadowBlur: Int = DefaultParams.shadowBlur,
    val shadowOpacity: Int = DefaultParams.shadowOpacity
) {
    fun applyTo(params: ScreenshotFramingParams): ScreenshotFramingParams = params.copy(
        preset = preset,
        backgroundColors = backgroundColors.filterNotNull()
            .takeIf { it.size >= 2 }
            ?.map(::Color)
            ?: DefaultParams.backgroundColors,
        useGradient = useGradient,
        aspectRatio = aspectRatio(),
        padding = padding,
        cornerRadius = cornerRadius,
        showShadow = showShadow,
        shadowBlur = shadowBlur,
        shadowOpacity = shadowOpacity
    )

    private fun aspectRatio(): DomainAspectRatio = when (aspectRatioWidth) {
        DomainAspectRatio.Free.widthProportion -> DomainAspectRatio.Free
        DomainAspectRatio.Original.widthProportion -> DomainAspectRatio.Original
        else -> if (customAspectRatio) {
            DomainAspectRatio.Custom(aspectRatioWidth, aspectRatioHeight, savedAspectRatio)
        } else {
            DomainAspectRatio.Numeric(aspectRatioWidth, aspectRatioHeight)
        }
    }
}

internal fun ScreenshotFramingParams.toStyle() = ScreenshotFramingStyle(
    // Freeze the colors extracted from the source image.
    preset = preset ?: GradientBackgroundPreset.Custom,
    backgroundColors = backgroundColors.map(Color::toArgb),
    useGradient = useGradient,
    aspectRatioWidth = aspectRatio.widthProportion,
    aspectRatioHeight = aspectRatio.heightProportion,
    customAspectRatio = aspectRatio is DomainAspectRatio.Custom,
    savedAspectRatio = (aspectRatio as? DomainAspectRatio.Custom)?.isSaved == true,
    padding = padding,
    cornerRadius = cornerRadius,
    showShadow = showShadow,
    shadowBlur = shadowBlur,
    shadowOpacity = shadowOpacity
)

private val DefaultParams = ScreenshotFramingParams()
