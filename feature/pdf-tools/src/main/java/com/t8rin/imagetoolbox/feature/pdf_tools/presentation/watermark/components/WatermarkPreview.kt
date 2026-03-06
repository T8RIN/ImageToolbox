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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.watermark.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.data.coil.PdfImageRequest
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfWatermarkParams
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PageSwitcher

@Composable
internal fun WatermarkPreview(
    uri: Uri?,
    params: PdfWatermarkParams,
    pageCount: Int,
    watermarkText: String
) {
    PageSwitcher(
        activePages = params.pages,
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
                    },
                    shape = MaterialTheme.shapes.medium
                )

                if (page in params.pages) {
                    BoxWithConstraints(
                        modifier = Modifier.matchParentSize()
                    ) {
                        val scaledFontSize = remember(
                            params.fontSize,
                            maxWidth
                        ) {
                            val scaleFactor = maxWidth.value / 300f
                            (params.fontSize * scaleFactor).sp
                        }

                        AutoSizeText(
                            key = { maxWidth },
                            text = watermarkText,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .graphicsLayer {
                                    rotationZ = params.rotation
                                    alpha = params.opacity
                                },
                            color = Color(params.color),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = scaledFontSize
                            )
                        )
                    }
                }
            }
        }
    }
}