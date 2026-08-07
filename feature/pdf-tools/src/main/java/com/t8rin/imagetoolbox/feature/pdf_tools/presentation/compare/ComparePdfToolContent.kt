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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Pix
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compare.components.PdfCompareSlot
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compare.screenLogic.ComparePdfToolComponent
import com.t8rin.opencv_tools.image_comparison.model.ComparisonType

@Composable
fun ComparePdfToolContent(component: ComparePdfToolComponent) {
    val firstPicker = rememberFilePicker(
        mimeType = MimeType.Pdf,
        onSuccess = component::setFirstUri
    )
    val secondPicker = rememberFilePicker(
        mimeType = MimeType.Pdf,
        onSuccess = component::setSecondUri
    )

    BasePdfToolContent(
        component = component,
        contentPicker = firstPicker,
        isPickedAlready = component.firstUri != null,
        canShowScreenData = component.firstUri != null,
        canSave = component.canProcess,
        canShare = component.canProcess,
        title = stringResource(R.string.compare_pdf),
        controls = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PdfCompareSlot(
                    title = stringResource(R.string.first_pdf),
                    uri = component.firstUri,
                    index = 0,
                    onPick = firstPicker::pickFile,
                    onRemove = { component.setFirstUri(null) }
                )
                PdfCompareSlot(
                    title = stringResource(R.string.second_pdf),
                    uri = component.secondUri,
                    index = 1,
                    onPick = secondPicker::pickFile,
                    onRemove = { component.setSecondUri(null) }
                )
            }
            Spacer(Modifier.height(12.dp))
            InfoContainer(text = stringResource(R.string.compare_pdf_info))
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorRowSelector(
                    value = Color(component.params.highlightColor),
                    onValueChange = {
                        component.updateParams(
                            component.params.copy(highlightColor = it.toArgb())
                        )
                    },
                    title = stringResource(R.string.highlight_color),
                    allowAlpha = false,
                    modifier = Modifier.container(shape = ShapeDefaults.top)
                )
                DataSelector(
                    value = component.params.comparisonType,
                    onValueChange = {
                        component.updateParams(
                            component.params.copy(comparisonType = it)
                        )
                    },
                    entries = ComparisonType.entries,
                    title = stringResource(R.string.pixel_comparison_type),
                    titleIcon = Icons.Rounded.Pix,
                    spanCount = 1,
                    shape = ShapeDefaults.bottom,
                    itemContentText = { it.name },
                    containerColor = Color.Unspecified
                )
            }
        }
    )
}