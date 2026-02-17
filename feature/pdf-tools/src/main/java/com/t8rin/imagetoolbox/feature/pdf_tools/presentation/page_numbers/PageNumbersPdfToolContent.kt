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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.page_numbers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.PositionSelector
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.page_numbers.screenLogic.PageNumbersPdfToolComponent

@Composable
fun PageNumbersPdfToolContent(
    component: PageNumbersPdfToolComponent
) {
    val pageCount by rememberPdfPages(component.uri)

    BasePdfToolContent(
        component = component,
        pdfPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.page_numbers),
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

                val previewText = component.pageNumberFormat
                    .replace("{n}", "1")
                    .replace("{total}", pageCount.toString())

                val previewAlignment = when (component.pageNumberPosition) {
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
                        AutoSizeText(
                            key = { maxWidth },
                            text = previewText,
                            modifier = Modifier
                                .align(previewAlignment)
                                .padding(8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            lineHeight = 11.sp,
                            color = component.pageNumberColor
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
                    .padding(top = 8.dp)
                    .container(
                        shape = MaterialTheme.shapes.large,
                        resultPadding = 8.dp
                    ),
                value = component.pageNumberFormat,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = false,
                onValueChange = component::updatePageNumberFormat,
                label = {
                    Text(stringResource(R.string.label_format))
                }
            )
            Spacer(Modifier.height(8.dp))
            PositionSelector(
                value = component.pageNumberPosition,
                onValueChange = component::updatePageNumberPosition,
                color = Color.Unspecified
            )
            Spacer(Modifier.height(8.dp))
            ColorRowSelector(
                value = component.pageNumberColor,
                onValueChange = component::updatePageNumberColor,
                title = stringResource(R.string.text_color),
                modifier = Modifier.container(
                    shape = ShapeDefaults.large
                )
            )

            Spacer(Modifier.height(20.dp))
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}
