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

package com.t8rin.imagetoolbox.core.ui.widget.image

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.histogram.HistogramType
import com.t8rin.histogram.ImageHistogram
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun HistogramChart(
    model: Any?,
    modifier: Modifier,
    initialType: HistogramType = HistogramType.RGB,
    onSwapType: ((HistogramType) -> HistogramType)? = { type ->
        when (type) {
            HistogramType.RGB -> HistogramType.Brightness
            HistogramType.Brightness -> HistogramType.Camera
            HistogramType.Camera -> HistogramType.RGB
        }
    },
    harmonizationColor: Color = MaterialTheme.colorScheme.primary,
    linesThickness: Dp = 0.5.dp,
    bordersColor: Color = MaterialTheme.colorScheme.outline,
    bordersShape: Shape = ShapeDefaults.extraSmall
) {
    when (model) {
        is Bitmap -> {
            ImageHistogram(
                image = model,
                modifier = modifier,
                initialType = initialType,
                onSwapType = onSwapType,
                harmonizationColor = harmonizationColor,
                linesThickness = linesThickness,
                bordersColor = bordersColor,
                bordersShape = bordersShape
            )
        }

        else -> {
            ImageHistogram(
                model = model,
                modifier = modifier,
                initialType = initialType,
                onSwapType = onSwapType,
                harmonizationColor = harmonizationColor,
                linesThickness = linesThickness,
                bordersColor = bordersColor,
                bordersShape = bordersShape
            )
        }
    }
}