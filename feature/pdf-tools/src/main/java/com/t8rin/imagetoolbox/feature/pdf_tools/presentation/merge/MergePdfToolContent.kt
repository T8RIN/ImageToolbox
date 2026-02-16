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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.merge

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.controls.FileReorderVerticalList
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.merge.screenLogic.MergePdfToolComponent

@Composable
fun MergePdfToolContent(
    component: MergePdfToolComponent
) {
    BasePdfToolContent(
        component = component,
        pdfPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUris
        ),
        isPickedAlready = !component.initialUris.isNullOrEmpty(),
        canShowScreenData = component.uris.isNotEmpty(),
        title = stringResource(R.string.merge_pdf),
        actions = {},
        imagePreview = {},
        placeImagePreview = false,
        showImagePreviewAsStickyHeader = false,
        controls = {
            val addFilesPicker = rememberFilePicker(
                mimeType = MimeType.Pdf,
                onSuccess = component::addUris
            )

            Spacer(Modifier.height(20.dp))
            FileReorderVerticalList(
                files = component.uris,
                onReorder = component::setUris,
                onNeedToAddFile = addFilesPicker::pickFile,
                onNeedToRemoveFileAt = component::removeAt
            )
            Spacer(Modifier.height(20.dp))
        }
    )
}