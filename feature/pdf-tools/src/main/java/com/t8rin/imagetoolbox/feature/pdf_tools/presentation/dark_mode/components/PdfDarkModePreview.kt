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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode.components

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.data.coil.PdfImageRequest
import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfDarkModeParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfDarkModeTheme
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PageSwitcher

@Composable
internal fun PdfDarkModePreview(
    uri: Uri?,
    params: PdfDarkModeParams,
    pageCount: Int
) {
    PageSwitcher(
        pageCount = pageCount
    ) { page ->
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
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .background(
                        Color(params.overlayColor ?: 0xFF000000.toInt())
                    )
            ) {
                Picture(
                    model = remember(uri, page) {
                        PdfImageRequest(
                            data = uri,
                            pdfPage = page
                        )
                    },
                    contentScale = ContentScale.FillBounds,
                    colorFilter = remember(params) {
                        ColorFilter.colorMatrix(params.previewColorMatrix())
                    },
                    modifier = Modifier.matchParentSize(),
                    onSuccess = {
                        aspectRatio = it.result.image.safeAspectRatio
                    },
                    shape = RectangleShape
                )

                if (params.theme == PdfDarkModeTheme.Custom) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                blendMode = params.customBlendMode.toComposeBlendMode()
                            }
                            .background(Color(params.customColor))
                    )
                }
            }
        }
    }
}

private const val COLOR_MATRIX_OFFSET = 255f
private const val LUMINANCE_RED = 0.299f
private const val LUMINANCE_GREEN = 0.587f
private const val LUMINANCE_BLUE = 0.114f

private fun PdfDarkModeParams.previewColorMatrix(): ColorMatrix {
    if (theme == PdfDarkModeTheme.Negative) {
        return ColorMatrix(
            floatArrayOf(
                -1f, 0f, 0f, 0f, COLOR_MATRIX_OFFSET,
                0f, -1f, 0f, 0f, COLOR_MATRIX_OFFSET,
                0f, 0f, -1f, 0f, COLOR_MATRIX_OFFSET,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    val background = if (theme == PdfDarkModeTheme.Custom) {
        Color.Black
    } else {
        Color(requireNotNull(overlayColor))
    }
    val redScale = 1f - background.red
    val greenScale = 1f - background.green
    val blueScale = 1f - background.blue

    return ColorMatrix(
        floatArrayOf(
            -LUMINANCE_RED * redScale,
            -LUMINANCE_GREEN * redScale,
            -LUMINANCE_BLUE * redScale,
            0f,
            COLOR_MATRIX_OFFSET,
            -LUMINANCE_RED * greenScale,
            -LUMINANCE_GREEN * greenScale,
            -LUMINANCE_BLUE * greenScale,
            0f,
            COLOR_MATRIX_OFFSET,
            -LUMINANCE_RED * blueScale,
            -LUMINANCE_GREEN * blueScale,
            -LUMINANCE_BLUE * blueScale,
            0f,
            COLOR_MATRIX_OFFSET,
            0f,
            0f,
            0f,
            1f,
            0f
        )
    )
}

private fun BlendingMode.toComposeBlendMode(): BlendMode = when (this) {
    BlendingMode.Multiply -> BlendMode.Multiply
    BlendingMode.Screen -> BlendMode.Screen
    BlendingMode.Overlay -> BlendMode.Overlay
    BlendingMode.Darken -> BlendMode.Darken
    BlendingMode.Lighten -> BlendMode.Lighten
    BlendingMode.Softlight -> BlendMode.Softlight
    BlendingMode.Difference -> BlendMode.Difference
    BlendingMode.Exclusion -> BlendMode.Exclusion
    BlendingMode.Hue -> BlendMode.Hue
    BlendingMode.Saturation -> BlendMode.Saturation
    BlendingMode.Color -> BlendMode.Color
    BlendingMode.Luminosity -> BlendMode.Luminosity
    else -> BlendMode.Screen
}
