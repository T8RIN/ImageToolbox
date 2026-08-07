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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.contact_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.TableRows
import com.t8rin.imagetoolbox.core.resources.icons.ViewColumn
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.contact_sheet.components.PdfContactSheetCaptionSelector
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.contact_sheet.screenLogic.PdfContactSheetToolComponent
import kotlin.math.roundToInt

@Composable
fun PdfContactSheetToolContent(
    component: PdfContactSheetToolComponent
) {
    val params = component.params
    val addImagesPicker = rememberImagePicker(onSuccess = component::addUris)

    BasePdfToolContent(
        component = component,
        contentPicker = rememberImagePicker(onSuccess = component::setUris),
        secondaryButtonIcon = Icons.Rounded.AddPhotoAlt,
        secondaryButtonText = stringResource(R.string.pick_image_alt),
        noDataText = stringResource(R.string.pick_image),
        isPickedAlready = component.initialUris != null,
        canShowScreenData = !component.uris.isNullOrEmpty(),
        title = stringResource(R.string.pdf_contact_sheet),
        controls = {
            ImageReorderCarousel(
                images = component.uris,
                onReorder = component::setUris,
                onNeedToAddImage = addImagesPicker::pickImage,
                onNeedToRemoveImageAt = component::removeAt,
                onNavigate = component.onNavigate
            )
            Spacer(Modifier.height(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EnhancedSliderItem(
                    value = params.columns,
                    title = stringResource(R.string.columns_count),
                    icon = Icons.Rounded.ViewColumn,
                    valueRange = 1f..8f,
                    steps = 6,
                    internalStateTransformation = Float::roundToInt,
                    onValueChange = {
                        component.updateParams(params.copy(columns = it.roundToInt()))
                    },
                    shape = ShapeDefaults.top,
                    valueSuffix = ""
                )
                EnhancedSliderItem(
                    value = params.rows,
                    title = stringResource(R.string.rows_count),
                    icon = Icons.Rounded.TableRows,
                    valueRange = 1f..10f,
                    steps = 8,
                    internalStateTransformation = Float::roundToInt,
                    onValueChange = {
                        component.updateParams(params.copy(rows = it.roundToInt()))
                    },
                    shape = ShapeDefaults.center,
                    valueSuffix = ""
                )
                EnhancedSliderItem(
                    value = params.margin,
                    title = stringResource(R.string.margin),
                    valueRange = 0f..72f,
                    internalStateTransformation = Float::roundToInt,
                    onValueChange = {
                        component.updateParams(params.copy(margin = it))
                    },
                    shape = ShapeDefaults.center,
                    valueSuffix = " pt"
                )
                EnhancedSliderItem(
                    value = params.spacing,
                    title = stringResource(R.string.spacing),
                    valueRange = 0f..36f,
                    internalStateTransformation = Float::roundToInt,
                    onValueChange = {
                        component.updateParams(params.copy(spacing = it))
                    },
                    shape = ShapeDefaults.bottom,
                    valueSuffix = " pt"
                )
            }
            Spacer(Modifier.height(8.dp))
            PdfContactSheetCaptionSelector(
                fields = params.captionFields,
                customText = params.customCaptionText,
                onFieldsChange = {
                    component.updateParams(params.copy(captionFields = it))
                },
                onCustomTextChange = {
                    component.updateParams(params.copy(customCaptionText = it))
                }
            )
            Spacer(Modifier.height(8.dp))
            QualitySelector(
                imageFormat = ImageFormat.Jpg,
                quality = Quality.Base((params.quality * 100f).roundToInt()),
                onQualityChange = {
                    component.updateParams(params.copy(quality = it.qualityValue / 100f))
                },
                autoCoerce = false
            )
        }
    )
}
