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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.sanitize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PdfPreviewItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.sanitize.screenLogic.SanitizePdfToolComponent

@Composable
fun SanitizePdfToolContent(component: SanitizePdfToolComponent) {
    BasePdfToolContent(
        component = component,
        contentPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.sanitize_pdf),
        controls = {
            component.uri?.let { uri ->
                PdfPreviewItem(uri = uri, onRemove = { component.setUri(null) })
                Spacer(Modifier.height(12.dp))
            }

            val params = component.params
            val optionCount = 5
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PreferenceRowSwitch(
                    title = stringResource(id = R.string.metadata),
                    checked = params.metadata,
                    shape = ShapeDefaults.byIndex(0, optionCount),
                    applyHorizontalPadding = false,
                    onClick = {
                        component.updateParams(params.copy(metadata = it))
                    }
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.annotations),
                    checked = params.annotations,
                    shape = ShapeDefaults.byIndex(1, optionCount),
                    applyHorizontalPadding = false,
                    onClick = {
                        component.updateParams(params.copy(annotations = it))
                    }
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.pdf_forms),
                    checked = params.forms,
                    shape = ShapeDefaults.byIndex(2, optionCount),
                    applyHorizontalPadding = false,
                    onClick = {
                        component.updateParams(params.copy(forms = it))
                    }
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.pdf_attachments),
                    checked = params.attachments,
                    shape = ShapeDefaults.byIndex(3, optionCount),
                    applyHorizontalPadding = false,
                    onClick = {
                        component.updateParams(params.copy(attachments = it))
                    }
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.pdf_scripts),
                    checked = params.scripts,
                    shape = ShapeDefaults.byIndex(4, optionCount),
                    applyHorizontalPadding = false,
                    onClick = {
                        component.updateParams(params.copy(scripts = it))
                    }
                )
            }
        },
        onFilledPassword = { component.setUri(component.uri) }
    )
}