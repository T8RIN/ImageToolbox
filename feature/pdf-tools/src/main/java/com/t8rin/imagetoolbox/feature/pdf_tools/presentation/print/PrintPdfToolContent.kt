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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.print

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.derivative.OnlyAllowedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PageOrientation
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PageSize
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PrintPdfParams
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PdfPreviewItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.print.screenLogic.PrintPdfToolComponent
import kotlin.math.roundToInt

@Composable
fun PrintPdfToolContent(
    component: PrintPdfToolComponent
) {
    val params = component.params

    BasePdfToolContent(
        component = component,
        pdfPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.print_pdf),
        actions = {},
        imagePreview = {},
        placeImagePreview = false,
        showImagePreviewAsStickyHeader = false,
        controls = {
            Spacer(Modifier.height(20.dp))

            component.uri?.let {
                PdfPreviewItem(
                    uri = it,
                    onRemove = {
                        component.setUri(null)
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
            QualitySelector(
                imageFormat = ImageFormat.Jpg,
                quality = Quality.Base((component.quality * 100).roundToInt()),
                onQualityChange = {
                    component.updateQuality(it.qualityValue / 100f)
                },
                autoCoerce = false
            )
            Spacer(Modifier.height(8.dp))
            OnlyAllowedSliderItem(
                label = stringResource(id = R.string.pages_per_sheet),
                icon = Icons.Outlined.ViewWeek,
                value = component.params.pagesPerSheet,
                allowed = PrintPdfParams.pagesMapping.keys,
                onValueChange = { component.updateParams(params.copy(pagesPerSheet = it)) },
                valueSuffix = ""
            )
            Spacer(Modifier.height(8.dp))
            DataSelector(
                value = component.params.pageSize,
                onValueChange = { component.updateParams(params.copy(pageSize = it)) },
                entries = remember {
                    listOf(PageSize.Auto) + PageSize.entries
                },
                spanCount = 3,
                initialExpanded = true,
                title = stringResource(R.string.page_size),
                titleIcon = Icons.Outlined.Receipt,
                itemContentText = { value ->
                    value.name.orEmpty().ifBlank {
                        stringResource(R.string.auto)
                    }
                },
                shape = ShapeDefaults.large
            )
            Spacer(Modifier.height(8.dp))
            EnhancedButtonGroup(
                modifier = Modifier
                    .container(ShapeDefaults.large),
                title = stringResource(id = R.string.orientation),
                entries = PageOrientation.entries,
                value = component.params.orientation,
                onValueChange = { component.updateParams(params.copy(orientation = it)) },
                itemContent = { value ->
                    Text(
                        stringResource(
                            when (value) {
                                PageOrientation.ORIGINAL -> R.string.original
                                PageOrientation.VERTICAL -> R.string.vertical
                                PageOrientation.HORIZONTAL -> R.string.horizontal
                            }
                        )
                    )
                }
            )
            Spacer(Modifier.height(8.dp))
            EnhancedSliderItem(
                value = params.marginPercent,
                title = stringResource(id = R.string.margin),
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChange = {
                    component.updateParams(params.copy(marginPercent = it))
                },
                valueRange = 0f..50f,
                shape = ShapeDefaults.large,
                valueSuffix = "%"
            )


            Spacer(Modifier.height(20.dp))
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}
