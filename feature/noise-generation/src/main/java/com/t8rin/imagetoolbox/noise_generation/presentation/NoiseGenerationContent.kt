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

package com.t8rin.imagetoolbox.noise_generation.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.animation.animate
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.utils.state.derivedValueOf
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ResizeImageField
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.noise_generation.presentation.components.NoiseParamsSelection
import com.t8rin.imagetoolbox.noise_generation.presentation.screenLogic.NoiseGenerationComponent

@Composable
fun NoiseGenerationContent(
    component: NoiseGenerationComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveNoise(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    val shareButton: @Composable () -> Unit = {
        var editSheetData by remember {
            mutableStateOf(listOf<Uri>())
        }
        ShareButton(
            onShare = {
                component.shareNoise(showConfetti)
            },
            onCopy = {
                component.cacheCurrentNoise(essentials::copyToClipboard)
            },
            onEdit = {
                component.cacheCurrentNoise {
                    editSheetData = listOf(it)
                }
            }
        )
        ProcessImagesPreferenceSheet(
            uris = editSheetData,
            visible = editSheetData.isNotEmpty(),
            onDismiss = {
                editSheetData = emptyList()
            },
            onNavigate = component.onNavigate
        )
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            Text(
                text = stringResource(R.string.noise_generation),
                textAlign = TextAlign.Center,
                modifier = Modifier.marquee()
            )
        },
        onGoBack = component.onGoBack,
        actions = {},
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        imagePreview = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Picture(
                    model = component.previewBitmap,
                    modifier = Modifier
                        .container(MaterialTheme.shapes.medium)
                        .aspectRatio(component.noiseSize.safeAspectRatio.animate()),
                    shape = MaterialTheme.shapes.medium,
                    contentScale = ContentScale.FillBounds
                )
                if (component.isImageLoading) EnhancedLoadingIndicator()
            }
        },
        controls = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResizeImageField(
                    imageInfo = derivedValueOf(component.noiseSize) {
                        ImageInfo(component.noiseSize.width, component.noiseSize.height)
                    },
                    originalSize = null,
                    onWidthChange = component::setNoiseWidth,
                    onHeightChange = component::setNoiseHeight
                )
                NoiseParamsSelection(
                    value = component.noiseParams,
                    onValueChange = component::updateParams
                )
                Spacer(Modifier.height(4.dp))
                ImageFormatSelector(
                    value = component.imageFormat,
                    onValueChange = component::setImageFormat,
                    forceEnabled = true,
                    quality = component.quality,
                )
                QualitySelector(
                    quality = component.quality,
                    imageFormat = component.imageFormat,
                    onQualityChange = component::setQuality
                )
            }
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = false,
                isSecondaryButtonVisible = false,
                onSecondaryButtonClick = {},
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    shareButton()
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmap,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
        },
        canShowScreenData = true
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )
}