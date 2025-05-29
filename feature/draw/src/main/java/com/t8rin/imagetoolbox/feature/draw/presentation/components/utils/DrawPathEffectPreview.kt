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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.graphics.Bitmap
import android.graphics.PorterDuff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.createScaledBitmap
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import android.graphics.Path as NativePath

@Composable
internal fun DrawPathEffectPreview(
    drawPathCanvas: Canvas,
    drawMode: DrawMode.PathEffect,
    canvasSize: IntegerSize,
    imageWidth: Int,
    imageHeight: Int,
    outputImage: ImageBitmap,
    onRequestFiltering: suspend (Bitmap, List<Filter<*>>) -> Bitmap?,
    paths: List<UiPathPaint>,
    drawPath: Path,
    backgroundColor: Color,
    strokeWidth: Pt,
    drawPathMode: DrawPathMode
) {
    var shaderBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(outputImage, paths, backgroundColor, drawMode) {
        shaderBitmap = onRequestFiltering(
            outputImage.asAndroidBitmap(),
            transformationsForMode(
                drawMode = drawMode,
                canvasSize = canvasSize
            )
        )?.createScaledBitmap(
            width = imageWidth,
            height = imageHeight
        )?.asImageBitmap()
    }

    shaderBitmap?.let {
        with(drawPathCanvas) {
            with(nativeCanvas) {
                drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)

                val paint by rememberPathEffectPaint(
                    strokeWidth = strokeWidth,
                    drawPathMode = drawPathMode,
                    canvasSize = canvasSize
                )
                val newPath = drawPath.copyAsAndroidPath().apply {
                    fillType = NativePath.FillType.INVERSE_WINDING
                }
                val imagePaint = remember { Paint() }

                drawImage(
                    image = it,
                    topLeftOffset = Offset.Zero,
                    paint = imagePaint
                )
                drawPath(newPath, paint)
            }
        }
    }
}
