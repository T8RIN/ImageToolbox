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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.pdf

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.feature.palette_tools.domain.model.PalettePdfSourceType
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.PalettePdfControls
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.pdf.screenLogic.PalettePdfToolComponent

@Composable
fun PalettePdfToolContent(
    component: PalettePdfToolComponent
) {
    val pdfSaver = rememberFileCreator(onSuccess = component::saveTo)
    val imagePicker = rememberImagePicker(onSuccess = component::updateSourceUri)
    val paletteFilePicker = rememberFilePicker(onSuccess = component::updateSourceUri)

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var editSheetData by remember { mutableStateOf(emptyList<Uri>()) }

    ZoomModalSheet(
        data = component.sourceUri.takeIf { component.hasSourceImage },
        visible = showZoomSheet,
        onDismiss = { showZoomSheet = false }
    )
    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.palette_pdf),
                input = component.sourceUri,
                isLoading = false,
                size = null
            )
        },
        onGoBack = component.onGoBack,
        actions = {
            ShareButton(
                enabled = true,
                onShare = component::share,
                onEdit = {
                    component.prepareForEditing {
                        editSheetData = listOf(it)
                    }
                },
                dialogTitle = "PDF",
                dialogIcon = Icons.Outlined.Pdf
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = { editSheetData = emptyList() },
                extraDataType = ExtraDataType.Pdf,
                onNavigate = component.onNavigate
            )
        },
        topAppBarPersistentActions = {
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.hasSourceImage
            )
        },
        imagePreview = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                var aspectRatio by remember(component.sourceUri) {
                    mutableFloatStateOf(1f)
                }
                Picture(
                    model = component.sourceUri,
                    modifier = Modifier
                        .container(MaterialTheme.shapes.medium)
                        .aspectRatio(aspectRatio),
                    onSuccess = {
                        aspectRatio = it.result.image.toBitmap().safeAspectRatio
                    },
                    isLoadingFromDifferentPlace = component.isImageLoading,
                    shape = MaterialTheme.shapes.medium,
                    contentScale = ContentScale.FillBounds
                )
                if (component.isImageLoading) EnhancedLoadingIndicator()
            }
        },
        placeImagePreview = component.hasSourceImage,
        showImagePreviewAsStickyHeader = component.hasSourceImage,
        controls = {
            if (!component.hasSourceImage && isPortrait) {
                Spacer(Modifier.height(20.dp))
            }
            PalettePdfControls(
                params = component.params,
                onParamsChange = component::updateParams,
                hasSourceImage = component.hasSourceImage
            )
        },
        buttons = { actions ->
            BottomButtonsBlock(
                isNoData = false,
                onSecondaryButtonClick = {
                    when (component.sourceType) {
                        PalettePdfSourceType.Image -> imagePicker.pickImage()
                        PalettePdfSourceType.PaletteFile -> paletteFilePicker.pickFile()
                    }
                },
                secondaryButtonIcon = if (component.hasSourceImage) {
                    Icons.Rounded.AddPhotoAlt
                } else {
                    Icons.Rounded.FileOpen
                },
                secondaryButtonText = stringResource(
                    if (component.hasSourceImage) R.string.pick_image_alt else R.string.pick_file
                ),
                onPrimaryButtonClick = {
                    pdfSaver.make(component.outputFilename)
                },
                isPrimaryButtonVisible = true,
                isPrimaryButtonEnabled = !component.isSaving,
                actions = actions
            )
        },
        canShowScreenData = true
    )

    LoadingDialog(
        visible = component.isSaving,
        canCancel = false
    )
}
