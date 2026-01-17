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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun EnhancedCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
    strokeCap: StrokeCap = StrokeCap.Round,
    gapSize: Dp = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize,
    type: EnhancedCircularProgressIndicatorType = EnhancedCircularProgressIndicatorType.Wavy()
) {
    when (type) {
        EnhancedCircularProgressIndicatorType.Normal -> {
            CircularProgressIndicator(
                modifier = modifier.background(Color.Red),
                color = color,
                strokeWidth = strokeWidth,
                trackColor = trackColor,
                strokeCap = strokeCap,
                gapSize = gapSize
            )
        }

        is EnhancedCircularProgressIndicatorType.Wavy -> {
            CircularWavyProgressIndicator(
                modifier = modifier,
                color = color,
                trackColor = trackColor,
                gapSize = gapSize,
                trackStroke = Stroke(
                    width = with(LocalDensity.current) { strokeWidth.toPx() },
                    cap = strokeCap
                ),
                stroke = Stroke(
                    width = with(LocalDensity.current) { strokeWidth.toPx() },
                    cap = strokeCap
                ),
                amplitude = type.amplitude(0.5f),
                wavelength = type.wavelength,
                waveSpeed = type.waveSpeed
            )
        }
    }
}

@Composable
fun EnhancedCircularProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
    strokeCap: StrokeCap = StrokeCap.Round,
    gapSize: Dp = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize,
    type: EnhancedCircularProgressIndicatorType = EnhancedCircularProgressIndicatorType.Wavy()
) {
    when (type) {
        EnhancedCircularProgressIndicatorType.Normal -> {
            CircularProgressIndicator(
                progress = progress,
                modifier = modifier,
                color = color,
                strokeWidth = strokeWidth,
                trackColor = trackColor,
                strokeCap = strokeCap,
                gapSize = gapSize
            )
        }

        is EnhancedCircularProgressIndicatorType.Wavy -> {
            CircularWavyProgressIndicator(
                progress = progress,
                modifier = modifier,
                color = color,
                trackColor = trackColor,
                gapSize = gapSize,
                trackStroke = Stroke(
                    width = with(LocalDensity.current) { strokeWidth.toPx() },
                    cap = strokeCap
                ),
                stroke = Stroke(
                    width = with(LocalDensity.current) { strokeWidth.toPx() },
                    cap = strokeCap
                ),
                amplitude = type.amplitude,
                wavelength = type.wavelength,
                waveSpeed = type.waveSpeed
            )
        }
    }
}

@Composable
fun EnhancedAutoCircularProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
    strokeCap: StrokeCap = StrokeCap.Round,
    gapSize: Dp = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize,
    type: EnhancedCircularProgressIndicatorType = EnhancedCircularProgressIndicatorType.Wavy()
) {
    if (progress() > 0f) {
        EnhancedCircularProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            strokeWidth = strokeWidth,
            trackColor = trackColor,
            strokeCap = strokeCap,
            gapSize = gapSize,
            type = type
        )
    } else {
        EnhancedCircularProgressIndicator(
            modifier = modifier,
            color = color,
            strokeWidth = strokeWidth,
            trackColor = trackColor,
            strokeCap = strokeCap,
            gapSize = gapSize,
            type = type
        )
    }
}

sealed class EnhancedCircularProgressIndicatorType {
    data object Normal : EnhancedCircularProgressIndicatorType()

    data class Wavy(
        val amplitude: (progress: Float) -> Float = { progress ->
            if (progress <= 0.1f || progress >= 0.95f) {
                0f
            } else {
                1f
            }
        },
        val wavelength: Dp = WavyProgressIndicatorDefaults.CircularWavelength,
        val waveSpeed: Dp = wavelength,
    ) : EnhancedCircularProgressIndicatorType()
}