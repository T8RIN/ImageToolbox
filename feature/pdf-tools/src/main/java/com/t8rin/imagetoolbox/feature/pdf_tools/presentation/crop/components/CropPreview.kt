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
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.Black
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
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
                .clip(MaterialTheme.shapes.small)
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

            CropFrameBorder(
                modifier = Modifier.matchParentSize(),
                cropRect = cropRect
            )
        }
    }
}

@Composable
private fun CropFrameBorder(
    modifier: Modifier = Modifier,
    cropRect: Rect
) {
    val isNightMode = LocalSettingsState.current.isNightMode
    val black = Black
    val colorScheme = MaterialTheme.colorScheme
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val cropRect = remember(cropRect, isRtl) {
        if (isRtl) {
            cropRect.copy(
                left = 1f - cropRect.left,
                right = 1f - cropRect.right
            )
        } else {
            cropRect
        }
    }

    val transition: InfiniteTransition = rememberInfiniteTransition()

    val phase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = modifier.graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRect(
            color = black.copy(alpha = if (isNightMode) 0.5f else 0.3f),
            size = size
        )

        val topLeft = Offset(
            x = cropRect.left * canvasWidth,
            y = cropRect.top * canvasHeight
        )
        val size = Size(
            width = (cropRect.right - cropRect.left) * canvasWidth,
            height = (cropRect.bottom - cropRect.top) * canvasHeight
        )

        drawRect(
            color = Color.Transparent,
            blendMode = BlendMode.Clear,
            topLeft = topLeft,
            size = size
        )

        drawRect(
            color = colorScheme.primary,
            style = Stroke(
                width = 1.5.dp.toPx()
            ),
            topLeft = topLeft,
            size = size
        )

        drawRect(
            color = colorScheme.primaryContainer,
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(20f, 20f),
                    phase.value
                )
            ),
            topLeft = topLeft,
            size = size
        )
    }
}

@EnPreview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(false) {
    LocalLayoutDirection.ProvidesValue(LayoutDirection.Ltr) {
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
}