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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.page_numbers.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.data.coil.PdfImageRequest
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfPageNumbersParams
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PageSwitcher
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PdfTextStyle
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun PageNumbersPreview(
    uri: Uri?,
    params: PdfPageNumbersParams,
    pageCount: Int
) {
    PageSwitcher(
        activePages = null,
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
            var pageWidth by rememberSaveable {
                mutableIntStateOf(1)
            }

            val previewText = params.labelFormat
                .replace("{n}", (page + 1).toString())
                .replace("{total}", pageCount.toString())

            val previewAlignment = when (params.position) {
                Position.TopLeft -> Alignment.TopStart
                Position.TopCenter -> Alignment.TopCenter
                Position.TopRight -> Alignment.TopEnd
                Position.CenterLeft -> Alignment.CenterStart
                Position.Center -> Alignment.Center
                Position.CenterRight -> Alignment.CenterEnd
                Position.BottomLeft -> Alignment.BottomStart
                Position.BottomCenter -> Alignment.BottomCenter
                Position.BottomRight -> Alignment.BottomEnd
            }

            Box(
                modifier = Modifier.aspectRatio(aspectRatio)
            ) {
                Picture(
                    model = remember(uri, page) {
                        PdfImageRequest(
                            data = uri,
                            pdfPage = page
                        )
                    },
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize(),
                    onSuccess = {
                        aspectRatio = it.result.image.safeAspectRatio
                        pageWidth = it.result.image.width
                    },
                    shape = MaterialTheme.shapes.medium
                )

                BoxWithConstraints(
                    modifier = Modifier.matchParentSize()
                ) {
                    val density = LocalDensity.current
                    val textMeasurer = rememberTextMeasurer()
                    val pageScale = maxWidth.value / pageWidth.coerceAtLeast(1)
                    val horizontalPadding = 10f * pageScale
                    val verticalPadding = 20f * pageScale
                    val targetWidth = maxWidth * params.fontSize / 100f
                    val textLayoutWidth = targetWidth + 8.dp
                    val scaledFontSize = remember(
                        density,
                        params.fontSize,
                        previewText,
                        targetWidth
                    ) {
                        val baseFontSize = 100.sp
                        val textWidth = textMeasurer.measure(
                            text = previewText,
                            style = PdfTextStyle.copy(fontSize = baseFontSize),
                            maxLines = 1,
                            softWrap = false
                        ).size.width.coerceAtLeast(1)

                        with(density) {
                            baseFontSize * (targetWidth.toPx() / textWidth)
                        }
                    }
                    val textVerticalOffsetPx = remember(
                        params.position,
                        previewText,
                        scaledFontSize
                    ) {
                        val textLayout = textMeasurer.measure(
                            text = previewText,
                            style = PdfTextStyle.copy(fontSize = scaledFontSize),
                            maxLines = 1,
                            softWrap = false
                        )
                        var top = Float.POSITIVE_INFINITY
                        var bottom = Float.NEGATIVE_INFINITY

                        previewText.indices.forEach { index ->
                            val bounds = textLayout.getBoundingBox(index)
                            top = min(top, bounds.top)
                            bottom = max(bottom, bounds.bottom)
                        }

                        if (!top.isFinite() || !bottom.isFinite()) {
                            0f
                        } else {
                            val visualCenter = (top + bottom) / 2f
                            val layoutHeight = textLayout.size.height.toFloat()

                            when (params.position) {
                                Position.TopLeft,
                                Position.TopCenter,
                                Position.TopRight -> -top

                                Position.CenterLeft,
                                Position.Center,
                                Position.CenterRight -> layoutHeight / 2f - visualCenter

                                Position.BottomLeft,
                                Position.BottomCenter,
                                Position.BottomRight -> layoutHeight - bottom
                            }
                        }
                    }

                    Text(
                        text = previewText,
                        modifier = Modifier
                            .align(previewAlignment)
                            .requiredWidth(textLayoutWidth)
                            .padding(
                                horizontal = horizontalPadding.dp,
                                vertical = verticalPadding.dp
                            )
                            .graphicsLayer {
                                translationY = textVerticalOffsetPx
                            },
                        color = Color(params.color),
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center,
                        style = PdfTextStyle.copy(fontSize = scaledFontSize)
                    )
                }
            }
        }
    }
}