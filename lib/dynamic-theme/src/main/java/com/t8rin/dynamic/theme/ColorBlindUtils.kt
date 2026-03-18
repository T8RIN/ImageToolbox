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

package com.t8rin.dynamic.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

fun Color.toColorBlind(type: ColorBlindType): Color {
    val (r, g, b) = listOf(this.red, this.green, this.blue)
    val newColor = when (type) {
        ColorBlindType.Protanomaly -> {
            val matrix = listOf(
                0.817, 0.183, 0.0,
                0.333, 0.667, 0.0,
                0.0, 0.125, 0.875
            )
            transformColor(r, g, b, matrix)
        }

        ColorBlindType.Deuteranomaly -> {
            val matrix = listOf(
                0.8, 0.2, 0.0,
                0.258, 0.742, 0.0,
                0.0, 0.142, 0.858
            )
            transformColor(r, g, b, matrix)
        }

        ColorBlindType.Tritanomaly -> {
            val matrix = listOf(
                0.967, 0.033, 0.0,
                0.0, 0.733, 0.267,
                0.0, 0.183, 0.817
            )
            transformColor(r, g, b, matrix)
        }

        ColorBlindType.Protanopia -> {
            val matrix = listOf(
                0.567, 0.433, 0.0,
                0.558, 0.442, 0.0,
                0.0, 0.242, 0.758
            )
            transformColor(r, g, b, matrix)
        }

        ColorBlindType.Deuteranopia -> {
            val matrix = listOf(
                0.625, 0.375, 0.0,
                0.7, 0.3, 0.0,
                0.0, 0.3, 0.7
            )
            transformColor(r, g, b, matrix)
        }

        ColorBlindType.Tritanopia -> {
            val matrix = listOf(
                0.95, 0.05, 0.0,
                0.0, 0.433, 0.567,
                0.0, 0.475, 0.525
            )
            transformColor(r, g, b, matrix)
        }

        ColorBlindType.Achromatomaly -> {
            val matrix = listOf(
                0.618, 0.320, 0.062,
                0.163, 0.775, 0.062,
                0.163, 0.320, 0.516
            )
            transformColor(r, g, b, matrix)
        }

        ColorBlindType.Achromatopsia -> {
            val matrix = listOf(
                0.299, 0.587, 0.114,
                0.299, 0.587, 0.114,
                0.299, 0.587, 0.114
            )
            transformColor(r, g, b, matrix)
        }
    }
    return Color(newColor[0], newColor[1], newColor[2], this.alpha)
}

private fun transformColor(r: Float, g: Float, b: Float, matrix: List<Double>): List<Float> {
    val newR = (matrix[0] * r + matrix[1] * g + matrix[2] * b).toFloat()
    val newG = (matrix[3] * r + matrix[4] * g + matrix[5] * b).toFloat()
    val newB = (matrix[6] * r + matrix[7] * g + matrix[8] * b).toFloat()
    return listOf(newR.coerceIn(0f, 1f), newG.coerceIn(0f, 1f), newB.coerceIn(0f, 1f))
}

enum class ColorBlindType {
    Protanomaly,
    Deuteranomaly,
    Tritanomaly,
    Protanopia,
    Deuteranopia,
    Tritanopia,
    Achromatomaly,
    Achromatopsia
}

fun ColorScheme.toColorBlind(type: ColorBlindType?): ColorScheme {
    if (type == null) return this

    return this.copy(
        primary = primary.toColorBlind(type),
        onPrimary = onPrimary.toColorBlind(type),
        primaryContainer = primaryContainer.toColorBlind(type),
        onPrimaryContainer = onPrimaryContainer.toColorBlind(type),
        inversePrimary = inversePrimary.toColorBlind(type),
        secondary = secondary.toColorBlind(type),
        onSecondary = onSecondary.toColorBlind(type),
        secondaryContainer = secondaryContainer.toColorBlind(type),
        onSecondaryContainer = onSecondaryContainer.toColorBlind(type),
        tertiary = tertiary.toColorBlind(type),
        onTertiary = onTertiary.toColorBlind(type),
        tertiaryContainer = tertiaryContainer.toColorBlind(type),
        onTertiaryContainer = onTertiaryContainer.toColorBlind(type),
        background = background.toColorBlind(type),
        onBackground = onBackground.toColorBlind(type),
        surface = surface.toColorBlind(type),
        onSurface = onSurface.toColorBlind(type),
        surfaceVariant = surfaceVariant.toColorBlind(type),
        onSurfaceVariant = onSurfaceVariant.toColorBlind(type),
        surfaceTint = surfaceTint.toColorBlind(type),
        inverseSurface = inverseSurface.toColorBlind(type),
        inverseOnSurface = inverseOnSurface.toColorBlind(type),
        error = error.toColorBlind(type),
        onError = onError.toColorBlind(type),
        errorContainer = errorContainer.toColorBlind(type),
        onErrorContainer = onErrorContainer.toColorBlind(type),
        outline = outline.toColorBlind(type),
        outlineVariant = outlineVariant.toColorBlind(type),
        surfaceBright = surfaceBright.toColorBlind(type),
        surfaceDim = surfaceDim.toColorBlind(type),
        surfaceContainer = surfaceContainer.toColorBlind(type),
        surfaceContainerHigh = surfaceContainerHigh.toColorBlind(type),
        surfaceContainerHighest = surfaceContainerHighest.toColorBlind(type),
        surfaceContainerLow = surfaceContainerLow.toColorBlind(type),
        surfaceContainerLowest = surfaceContainerLowest.toColorBlind(type),
        primaryFixed = primaryFixed.toColorBlind(type),
        primaryFixedDim = primaryFixedDim.toColorBlind(type),
        onPrimaryFixed = onPrimaryFixed.toColorBlind(type),
        onPrimaryFixedVariant = onPrimaryFixedVariant.toColorBlind(type),
        secondaryFixed = secondaryFixed.toColorBlind(type),
        secondaryFixedDim = secondaryFixedDim.toColorBlind(type),
        onSecondaryFixed = onSecondaryFixed.toColorBlind(type),
        onSecondaryFixedVariant = onSecondaryFixedVariant.toColorBlind(type),
        tertiaryFixed = tertiaryFixed.toColorBlind(type),
        tertiaryFixedDim = tertiaryFixedDim.toColorBlind(type),
        onTertiaryFixed = onTertiaryFixed.toColorBlind(type),
        onTertiaryFixedVariant = onTertiaryFixedVariant.toColorBlind(type),
    )
}