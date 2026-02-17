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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.unlock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.Green
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContentColor
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PdfPreviewItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.unlock.screenLogic.UnlockPdfToolComponent

@Composable
fun UnlockPdfToolContent(
    component: UnlockPdfToolComponent
) {
    BasePdfToolContent(
        component = component,
        pdfPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.unlock_pdf),
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

            AnimatedVisibility(
                visible = rememberPdfPages(component.uri).value != 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                val containerColor = Green.blend(
                    color = Color.Black,
                    fraction = 0.4f
                )

                val contentColor = containerColor.inverse(
                    fraction = { 1f },
                    darkMode = true
                )

                CompositionLocalProvider(
                    LocalIconShapeContentColor provides contentColor,
                    LocalIconShapeContainerColor provides containerColor.blend(
                        color = Color.Black,
                        fraction = 0.15f
                    )
                ) {
                    PreferenceItem(
                        title = stringResource(R.string.success),
                        subtitle = stringResource(R.string.pdf_unlocked),
                        startIcon = Icons.Outlined.CheckCircle,
                        contentColor = contentColor,
                        containerColor = containerColor,
                        overrideIconShapeContentColor = true,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = null
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}