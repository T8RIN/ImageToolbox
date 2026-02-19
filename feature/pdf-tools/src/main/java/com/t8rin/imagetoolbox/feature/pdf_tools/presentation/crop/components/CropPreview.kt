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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun CropPreview(
    uri: Uri?,
    cropRect: Rect
) {
    Box(
        modifier = Modifier
            .container()
            .padding(4.dp)
            .animateContentSizeNoClip(
                alignment = Alignment.Center
            ),
        contentAlignment = Alignment.Center
    ) {
        var aspectRatio by rememberSaveable {
            mutableFloatStateOf(1f)
        }

        Box(
            modifier = Modifier
                .aspectRatio(aspectRatio)
                .clip(MaterialTheme.shapes.medium)
        ) {
            Picture(
                model = uri,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
                onSuccess = {
                    aspectRatio = it.result.image.safeAspectRatio
                },
                shape = RectangleShape
            )

            SimpleCropFrame(
                modifier = Modifier.matchParentSize(),
                cropRect = cropRect
            )
        }
    }
}

@Composable
private fun SimpleCropFrame(
    modifier: Modifier = Modifier,
    cropRect: Rect
) {
    Canvas(
        modifier = modifier.graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            size = size
        )

        drawRect(
            color = Color.Transparent,
            blendMode = BlendMode.Clear,
            topLeft = Offset(
                x = cropRect.left * canvasWidth,
                y = cropRect.top * canvasHeight
            ),
            size = Size(
                width = (cropRect.right - cropRect.left) * canvasWidth,
                height = (cropRect.bottom - cropRect.top) * canvasHeight
            )
        )

        drawRect(
            color = Color.White,
            style = Stroke(width = 1.dp.toPx()),
            topLeft = Offset(
                x = cropRect.left * canvasWidth,
                y = cropRect.top * canvasHeight
            ),
            size = Size(
                width = (cropRect.right - cropRect.left) * canvasWidth,
                height = (cropRect.bottom - cropRect.top) * canvasHeight
            )
        )
    }
}

@EnPreview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true) {
    CropPreview(
        "111".toUri(),
        Rect(
            left = 0.4f,
            top = 0.4f,
            right = 0.9f,
            bottom = 0.9f
        )
    )
}