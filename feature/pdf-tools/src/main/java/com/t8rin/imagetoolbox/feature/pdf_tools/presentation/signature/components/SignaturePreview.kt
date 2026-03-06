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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.t8rin.imagetoolbox.core.data.coil.PdfImageRequest
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.helper.toDp
import com.t8rin.imagetoolbox.core.ui.utils.helper.toPx
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfSignatureParams
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PageSwitcher
import kotlin.math.roundToInt

@Composable
internal fun SignaturePreview(
    uri: Uri?,
    params: PdfSignatureParams,
    pageCount: Int
) {
    PageSwitcher(
        activePages = params.pages,
        pageCount = pageCount
    ) { page ->
        Box(
            modifier = Modifier
                .container()
                .padding(4.dp)
                .animateContentSizeNoClip(alignment = Alignment.Center),
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
                        var imageAspect by remember {
                            mutableFloatStateOf(1f)
                        }

                        val boxWidthPx = maxWidth.toPx()
                        val boxHeightPx = maxHeight.toPx()

                        val targetWidthPx = boxWidthPx * params.size
                        val targetHeightPx = targetWidthPx / imageAspect

                        val centerXPx = boxWidthPx * params.x
                        val centerYPx = boxHeightPx * params.y

                        var offsetXPx = centerXPx - targetWidthPx / 2f
                        var offsetYPx = centerYPx - targetHeightPx / 2f

                        offsetXPx = offsetXPx.coerceIn(0f, boxWidthPx - targetWidthPx)
                        offsetYPx = offsetYPx.coerceIn(0f, boxHeightPx - targetHeightPx)

                        AsyncImage(
                            model = params.signatureImage,
                            contentDescription = null,
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = offsetXPx.roundToInt(),
                                        y = (boxHeightPx - offsetYPx - targetHeightPx).roundToInt()
                                    )
                                }
                                .width(targetWidthPx.toDp())
                                .aspectRatio(imageAspect)
                                .graphicsLayer {
                                    alpha = params.opacity
                                },
                            contentScale = ContentScale.Fit,
                            onSuccess = {
                                imageAspect = it.result.image.safeAspectRatio
                            }
                        )
                    }
                }
            }
        }
    }
}