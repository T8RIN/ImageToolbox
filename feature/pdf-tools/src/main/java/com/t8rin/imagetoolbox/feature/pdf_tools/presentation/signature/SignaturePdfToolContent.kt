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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.widget.controls.page.PageSelectionItem
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.AlphaSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.components.SignaturePreview
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.components.SignatureSelector
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.screenLogic.SignaturePdfToolComponent

@Composable
fun SignaturePdfToolContent(
    component: SignaturePdfToolComponent
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

    val savedSignatures by component.savedSignatures.collectAsStateWithLifecycle()

    BasePdfToolContent(
        component = component,
        pdfPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.signature),
        actions = {},
        imagePreview = {
            SignaturePreview(
                uri = component.uri,
                params = component.params,
                pageCount = pagesCount
            )
        },
        placeImagePreview = true,
        showImagePreviewAsStickyHeader = true,
        controls = {
            ImageSelector(
                value = params.signatureImage,
                onValueChange = component::updateSignature,
                subtitle = stringResource(R.string.will_be_for_signature)
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
            SignatureSelector(
                savedSignatures = savedSignatures,
                onSelect = {
                    component.updateParams(params.copy(opacity = 1f))
                    component.updateSignature(it)
                },
                onAdd = {
                    component.updateSignature(
                        data = it,
                        save = true
                    )
                }
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
                value = params.x,
                title = stringResource(id = R.string.offset_x),
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    component.updateParams(params.copy(x = it))
                },
                valueRange = 0f..1f,
                shape = ShapeDefaults.large
            )
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedSliderItem(
                value = params.y,
                title = stringResource(id = R.string.offset_y),
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    component.updateParams(params.copy(y = it))
                },
                valueRange = 0f..1f,
                shape = ShapeDefaults.large
            )
            Spacer(modifier = Modifier.height(8.dp))
            EnhancedSliderItem(
                value = params.size,
                title = stringResource(R.string.just_size),
                internalStateTransformation = {
                    it.roundToTwoDigits()
                },
                onValueChange = {
                    component.updateParams(params.copy(size = it))
                },
                valueRange = 0.01f..1f,
                shape = ShapeDefaults.large
            )
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}
