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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.watermark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextRotationAngleup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.controls.page.PageSelectionItem
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.AlphaSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.watermark.screenLogic.WatermarkPdfToolComponent
import kotlin.math.roundToInt

@Composable
fun WatermarkPdfToolContent(
    component: WatermarkPdfToolComponent
) {
    val params = component.params

    val pagesCount by rememberPdfPages(component.uri)

    LaunchedEffect(pagesCount, params.pages) {
        if (pagesCount > 0 && params.pages.isEmpty()) {
            component.updateParams(
                params.copy(
                    pages = List(pagesCount) { it }
                )
            )
        }
    }

    BasePdfToolContent(
        component = component,
        pdfPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.watermarking),
        actions = {},
        imagePreview = {
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
                        model = component.uri,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize(),
                        onSuccess = {
                            aspectRatio = it.result.image.safeAspectRatio
                        },
                        shape = MaterialTheme.shapes.medium,
                        isLoadingFromDifferentPlace = component.isImageLoading
                    )

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
                            text = component.watermarkText,
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
        },
        placeImagePreview = true,
        showImagePreviewAsStickyHeader = true,
        controls = {
            Spacer(Modifier.height(20.dp))

            RoundedTextField(
                modifier = Modifier
                    .container(
                        shape = MaterialTheme.shapes.large,
                        resultPadding = 8.dp
                    ),
                value = component.watermarkText,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = false,
                onValueChange = component::updateWatermarkText,
                label = {
                    Text(stringResource(R.string.text))
                }
            )
            Spacer(Modifier.height(16.dp))
            PageSelectionItem(
                value = params.pages,
                onValueChange = {
                    component.updateParams(params.copy(pages = it))
                },
                pagesCount = pagesCount
            )
            Spacer(Modifier.height(8.dp))
            EnhancedSliderItem(
                value = params.rotation,
                icon = Icons.Outlined.TextRotationAngleup,
                title = stringResource(id = R.string.angle),
                valueRange = 0f..360f,
                internalStateTransformation = Float::roundToInt,
                onValueChange = {
                    component.updateParams(params.copy(rotation = it))
                },
                shape = ShapeDefaults.large
            )
            Spacer(Modifier.height(8.dp))
            AlphaSelector(
                value = params.opacity,
                onValueChange = {
                    component.updateParams(params.copy(opacity = it))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeDefaults.large
            )
            Spacer(Modifier.height(8.dp))
            EnhancedSliderItem(
                value = params.fontSize,
                title = stringResource(R.string.watermark_size),
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    component.updateParams(params.copy(fontSize = it))
                },
                valueRange = 5f..100f,
                shape = ShapeDefaults.large
            )
            Spacer(modifier = Modifier.height(8.dp))
            ColorRowSelector(
                value = params.color.toColor(),
                onValueChange = {
                    component.updateParams(params.copy(color = it.toArgb()))
                },
                title = stringResource(R.string.text_color),
                modifier = Modifier.container(
                    shape = ShapeDefaults.large
                ),
                allowAlpha = false
            )

            Spacer(Modifier.height(20.dp))
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}
