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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode.components.PdfDarkModeCustomControls
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode.components.PdfDarkModePreview
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode.components.PdfDarkModeThemeSelector
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode.screenLogic.PdfDarkModeToolComponent

@Composable
fun PdfDarkModeToolContent(
    component: PdfDarkModeToolComponent
) {
    val pageCount by rememberPdfPages(component.uri)

    BasePdfToolContent(
        component = component,
        contentPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.pdf_dark_mode),
        imagePreview = {
            PdfDarkModePreview(
                uri = component.uri,
                params = component.params,
                pageCount = pageCount
            )
        },
        placeImagePreview = true,
        showImagePreviewAsStickyHeader = true,
        controls = {
            PdfDarkModeThemeSelector(
                value = component.params.theme,
                customColor = component.params.customColor,
                onValueChange = component::updateTheme
            )
            PdfDarkModeCustomControls(
                params = component.params,
                onColorChange = component::updateCustomColor,
                onBlendModeChange = component::updateCustomBlendMode
            )
            Spacer(Modifier.height(8.dp))
            InfoContainer(
                text = stringResource(R.string.pdf_dark_mode_info)
            )
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}
