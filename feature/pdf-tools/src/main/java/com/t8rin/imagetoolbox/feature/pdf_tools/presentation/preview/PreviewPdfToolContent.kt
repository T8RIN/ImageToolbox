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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.preview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.canUseNewPdfFully
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.preview.screenLogic.PreviewPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.PdfViewer

@Composable
fun PreviewPdfToolContent(
    component: PreviewPdfToolComponent
) {
    val isPortrait by isPortraitOrientationAsState()

    var isSearching by remember(component.uri) {
        mutableStateOf(false)
    }

    BasePdfToolContent(
        component = component,
        contentPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = component.uri?.let {
            rememberFilename(it)
        }.orEmpty().ifEmpty { stringResource(R.string.preview_pdf) },
        actions = {},
        forceImagePreviewToMax = component.uri != null,
        placeControlsSeparately = true,
        canShare = true,
        canSave = false,
        topAppBarPersistentActions = {
            if (component.uri != null && canUseNewPdfFully()) {
                EnhancedIconButton(
                    onClick = {
                        isSearching = !isSearching
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.search_here)
                    )
                }
            }
        },
        drawBottomShadow = !isSearching,
        controls = {
            if (rememberPdfPages(component.uri).value > 0) {
                PdfViewer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (isPortrait) 104.dp else 0.dp),
                    uri = component.uri,
                    contentPadding = PaddingValues(),
                    isSearching = isSearching
                )
            }
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}