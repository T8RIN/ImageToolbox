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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.resize_pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Receipt
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PageSize
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PdfPreviewItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.resize_pages.components.PdfPageResizeModeSelector
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.resize_pages.screenLogic.ResizePdfPagesToolComponent

@Composable
fun ResizePdfPagesToolContent(component: ResizePdfPagesToolComponent) {
    BasePdfToolContent(
        component = component,
        contentPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.resize_pdf_pages),
        controls = {
            component.uri?.let { uri ->
                PdfPreviewItem(uri = uri, onRemove = { component.setUri(null) })
                Spacer(Modifier.height(12.dp))
            }

            val params = component.params
            DataSelector(
                value = params.pageSize,
                onValueChange = { component.updateParams(params.copy(pageSize = it)) },
                entries = remember { PageSize.entries },
                title = stringResource(R.string.page_size),
                titleIcon = Icons.Outlined.Receipt,
                itemContentText = { it.name },
                spanCount = 3,
                shape = ShapeDefaults.large
            )
            Spacer(Modifier.height(8.dp))
            PdfPageResizeModeSelector(
                value = params.mode,
                onValueChange = { component.updateParams(params.copy(mode = it)) }
            )
        },
        onFilledPassword = { component.setUri(component.uri) }
    )
}
