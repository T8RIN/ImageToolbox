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

package com.t8rin.imagetoolbox.feature.ascii_art.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeAspectRatio
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.ascii_art.presentation.components.AsciiArtControls
import com.t8rin.imagetoolbox.feature.ascii_art.presentation.screenLogic.AsciiArtComponent

@Composable
fun AsciiArtContent(
    component: AsciiArtComponent
) {
    val essentials = rememberLocalEssentials()

    AutoContentBasedColors(component.uri)

    val imagePicker = rememberImagePicker(onSuccess = component::setUri)
    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = component.initialUri != null
    )

    val isPortrait by isPortraitOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }

    val transformations by remember(
        component.uri,
        component.gradient,
        component.fontSize,
        component.isInvertImage
    ) {
        derivedStateOf {
            component.getAsciiTransformations()
        }
    }

    ZoomModalSheet(
        data = component.uri,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        },
        transformations = transformations
    )

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.ascii_art),
                input = component.uri.takeIf { it != Uri.EMPTY },
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = component.onGoBack,
        topAppBarPersistentActions = {
            if (component.uri == Uri.EMPTY) {
                TopAppBarEmoji()
            }
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.uri != Uri.EMPTY
            )
        },
        actions = {
            ShareButton(
                enabled = component.uri != Uri.EMPTY,
                onShare = {
                    component.convertToAsciiString(essentials::shareText)
                },
            )
        },
        imagePreview = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                var aspectRatio by remember {
                    mutableFloatStateOf(1f)
                }

                Picture(
                    model = component.uri,
                    modifier = Modifier
                        .container(MaterialTheme.shapes.medium)
                        .aspectRatio(aspectRatio),
                    onSuccess = {
                        aspectRatio = it.result.image.toBitmap().safeAspectRatio
                    },
                    isLoadingFromDifferentPlace = component.isImageLoading,
                    shape = MaterialTheme.shapes.medium,
                    contentScale = ContentScale.FillBounds,
                    transformations = transformations
                )
                if (component.isImageLoading) EnhancedLoadingIndicator()
            }
        },
        controls = {
            AsciiArtControls(component)
        },
        buttons = {
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.uri == Uri.EMPTY,
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                primaryButtonIcon = Icons.Rounded.ContentCopy,
                onPrimaryButtonClick = {
                    component.convertToAsciiString(essentials::copyToClipboard)
                },
                actions = {
                    if (isPortrait) it()
                }
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = component.uri != Uri.EMPTY,
        noDataControls = {
            if (!component.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        }
    )
}