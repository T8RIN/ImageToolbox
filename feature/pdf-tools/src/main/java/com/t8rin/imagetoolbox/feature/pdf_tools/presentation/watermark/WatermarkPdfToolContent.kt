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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.TextRotationAngleup
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.widget.controls.page.PageSelectionItem
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.AlphaSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.watermark.components.WatermarkPreview
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
        contentPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.watermarking),
        actions = {},
        imagePreview = {
            WatermarkPreview(
                uri = component.uri,
                params = params,
                pageCount = pagesCount
            )
        },
        placeImagePreview = true,
        showImagePreviewAsStickyHeader = true,
        controls = {
            RoundedTextField(
                modifier = Modifier
                    .container(
                        shape = MaterialTheme.shapes.large,
                        resultPadding = 8.dp
                    ),
                value = params.text,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = false,
                onValueChange = {
                    component.updateParams(params.copy(text = it))
                },
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
                pageCount = pagesCount
            )
            Spacer(Modifier.height(8.dp))
            EnhancedSliderItem(
                value = params.rotation,
                icon = Icons.Rounded.TextRotationAngleup,
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
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}
