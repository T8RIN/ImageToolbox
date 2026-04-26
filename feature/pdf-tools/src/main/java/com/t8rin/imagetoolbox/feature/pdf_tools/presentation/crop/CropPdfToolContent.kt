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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BorderHorizontal
import com.t8rin.imagetoolbox.core.resources.icons.BorderVertical
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.widget.controls.page.PageSelectionItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedRangeSliderItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop.components.CropPreview
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop.screenLogic.CropPdfToolComponent

@Composable
fun CropPdfToolContent(
    component: CropPdfToolComponent
) {
    val pageCount by rememberPdfPages(component.uri)
    val params = component.params

    LaunchedEffect(pageCount, params.pages) {
        if (params.pages == null && pageCount > 0) {
            component.updateParams(
                params.copy(
                    pages = List(pageCount) { it }
                )
            )
        }
    }

    BasePdfToolContent(
        component = component,
        contentPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canSave = !params.rect.isEmpty,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.crop_pdf),
        imagePreview = {
            CropPreview(
                uri = component.uri,
                params = params,
                pageCount = pageCount
            )
        },
        placeImagePreview = true,
        showImagePreviewAsStickyHeader = true,
        controls = {
            PageSelectionItem(
                value = params.pages,
                onValueChange = {
                    component.updateParams(params.copy(pages = it))
                },
                pageCount = pageCount
            )

            Spacer(Modifier.height(16.dp))

            EnhancedRangeSliderItem(
                value = params.rect.let { it.left..it.right },
                valueRange = 0f..1f,
                icon = Icons.Rounded.BorderVertical,
                title = stringResource(R.string.vertical_pivot_line),
                internalStateTransformation = {
                    it.start.roundTo(3)..it.endInclusive.roundTo(3)
                },
                onValueChange = {
                    component.updateParams(
                        params.copy(
                            rect = params.rect.copy(
                                left = it.start,
                                right = it.endInclusive
                            )
                        )
                    )
                }
            )
            Spacer(Modifier.height(8.dp))
            EnhancedRangeSliderItem(
                value = params.rect.let { it.top..it.bottom },
                valueRange = 0f..1f,
                icon = Icons.Rounded.BorderHorizontal,
                title = stringResource(R.string.horizontal_pivot_line),
                internalStateTransformation = {
                    it.start.roundTo(3)..it.endInclusive.roundTo(3)
                },
                onValueChange = {
                    component.updateParams(
                        params.copy(
                            rect = params.rect.copy(
                                top = it.start,
                                bottom = it.endInclusive
                            )
                        )
                    )
                }
            )
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}
