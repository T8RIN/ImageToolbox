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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.split

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.data.coil.PdfImageRequest
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberPdfPages
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagesPreviewWithSelection
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.PdfPreviewItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.PageSelectionItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.split.screenLogic.SplitPdfToolComponent

@Composable
fun SplitPdfToolContent(
    component: SplitPdfToolComponent
) {
    val pagesCount by rememberPdfPages(component.uri)
    val selectedPagesSize = component.pages?.size ?: 0
    val isPortrait by isPortraitOrientationAsState()

    var trigger by remember {
        mutableIntStateOf(0)
    }

    BasePdfToolContent(
        component = component,
        pdfPicker = rememberFilePicker(
            mimeType = MimeType.Pdf,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.split_pdf),
        topAppBarPersistentActions = {
            AnimatedVisibility(
                visible = selectedPagesSize != pagesCount,
                enter = fadeIn() + scaleIn() + expandHorizontally(),
                exit = fadeOut() + scaleOut() + shrinkHorizontally()
            ) {
                EnhancedIconButton(
                    onClick = {
                        component.updatePages(
                            List(pagesCount) { it }
                        )
                        trigger++
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SelectAll,
                        contentDescription = "Select All"
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier
                    .padding(8.dp)
                    .container(
                        shape = ShapeDefaults.circle,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        resultPadding = 0.dp
                    ),
                visible = selectedPagesSize != 0
            ) {
                Row(
                    modifier = Modifier.padding(start = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    selectedPagesSize.takeIf { it != 0 }?.let {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = selectedPagesSize.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    EnhancedIconButton(
                        onClick = {
                            component.updatePages(emptyList())
                            trigger++
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
            }
        },
        canSave = !component.pages.isNullOrEmpty(),
        actions = {},
        imagePreview = {
            key(trigger, pagesCount, component.uri) {
                ImagesPreviewWithSelection(
                    imageUris = remember(pagesCount, component.uri) {
                        List(pagesCount) {
                            PdfImageRequest(
                                data = component.uri,
                                pdfPage = it
                            )
                        }
                    },
                    imageFrames = remember(component.pages) {
                        ImageFrames.ManualSelection(
                            component.pages.orEmpty().map { it + 1 }
                        )
                    },
                    onFrameSelectionChange = { frames ->
                        component.updatePages(
                            frames.getFramePositions(pagesCount).map { it - 1 }
                        )
                    },
                    isPortrait = isPortrait,
                    isLoadingImages = false
                )
            }
        },
        placeImagePreview = true,
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

            PageSelectionItem(
                value = component.pages,
                onValueChange = {
                    component.updatePages(it)
                    trigger++
                },
                pagesCount = pagesCount
            )
            Spacer(Modifier.height(20.dp))
        },
        onFilledPassword = {
            component.setUri(component.uri)
        }
    )
}