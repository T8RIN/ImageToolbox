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

package com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.Explore
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.UndoRedoButtons
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.components.EmojiKitchenControls
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.components.EmojiKitchenExploreSheet
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.components.EmojiKitchenFavoritesSheet
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.components.EmojiKitchenPreview
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.components.EmojiKitchenSetup
import com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.screenLogic.EmojiKitchenComponent

@Composable
fun EmojiKitchenContent(
    component: EmojiKitchenComponent
) {
    var showFolderSelection by rememberSaveable { mutableStateOf(false) }
    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showExplore by rememberSaveable { mutableStateOf(false) }
    var showFavorites by rememberSaveable { mutableStateOf(false) }
    var editSheetData by remember { mutableStateOf(emptyList<Uri>()) }
    var showEditSheet by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()
    val state = component.collectionState
    val filePicker = rememberFilePicker(
        mimeType = MimeType.Zip
    ) { uri: Uri ->
        if (state.collection == null) {
            component.importCollection(uri)
        }
    }

    val preview = @Composable {
        EmojiKitchenPreview(
            component = component,
            modifier = Modifier
        )
    }

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.emoji_kitchen),
                input = Unit,
                isLoading = false,
                size = null
            )
        },
        onGoBack = component.onGoBack,
        shouldDisableBackHandler = true,
        actions = {
            ShareButton(
                enabled = component.canExport,
                onShare = component::shareImage,
                onCopy = { component.cacheImage(Clipboard::copy) },
                onEdit = {
                    component.cacheImage {
                        editSheetData = listOf(it)
                        showEditSheet = true
                    }
                }
            )
            if (state.collection != null) {
                if (!isPortrait) {
                    UndoRedoButtons(
                        canUndo = component.canUndo,
                        canRedo = component.canRedo,
                        onUndo = component::undo,
                        onRedo = component::redo,
                        modifier = Modifier.padding(2.dp)
                    )
                }
                EnhancedIconButton(
                    onClick = { showFavorites = true },
                    enabled = state.progress == null && !component.isSaving
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Bookmark,
                        contentDescription = null
                    )
                }
                EnhancedIconButton(
                    onClick = {
                        component.openExplore()
                        showExplore = true
                    },
                    enabled = state.progress == null && !component.isSaving
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Explore,
                        contentDescription = null
                    )
                }
                if (isPortrait) {
                    UndoRedoButtons(
                        canUndo = component.canUndo,
                        canRedo = component.canRedo,
                        onUndo = component::undo,
                        onRedo = component::redo,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        },
        topAppBarPersistentActions = {
            ZoomButton(
                visible = component.selectedVariant != null,
                onClick = { showZoomSheet = true }
            )
            if (state.collection == null) {
                TopAppBarEmoji()
            }
        },
        imagePreview = preview,
        forceImagePreviewToMax = !isPortrait && state.collection != null,
        placeControlsSeparately = true,
        useRegularStickyHeader = true,
        controls = { listState ->
            EmojiKitchenControls(
                component = component,
                listState = listState,
                isPortrait = isPortrait,
                imagePreview = preview.takeIf { isPortrait }
            )
        },
        noDataControls = {
            if (!state.isLoading && state.collection == null) {
                EmojiKitchenSetup(
                    component = component,
                    onDownload = component::downloadCollection,
                    onImport = filePicker::pickFile
                )
            }
        },
        buttons = { actions ->
            if (state.collection != null) {
                BottomButtonsBlock(
                    isNoData = false,
                    onSecondaryButtonClick = {},
                    isSecondaryButtonVisible = false,
                    onPrimaryButtonClick = { component.saveImage(null) },
                    onPrimaryButtonLongClick = { showFolderSelection = true },
                    isPrimaryButtonEnabled = component.canExport,
                    actions = {
                        if (isPortrait) actions()
                    }
                )
            }
        },
        canShowScreenData = state.collection != null,
        isLoading = state.isLoading,
        contentPadding = 16.dp
    )

    EmojiKitchenExploreSheet(
        component = component,
        visible = showExplore && state.collection != null,
        onDismiss = { showExplore = false }
    )

    EmojiKitchenFavoritesSheet(
        component = component,
        visible = showFavorites && state.collection != null,
        onDismiss = { showFavorites = false }
    )

    ZoomModalSheet(
        data = component.selectedVariant?.uri,
        visible = showZoomSheet,
        onDismiss = { showZoomSheet = false }
    )

    OneTimeSaveLocationSelectionDialog(
        visible = showFolderSelection,
        onDismiss = { showFolderSelection = false },
        onSaveRequest = component::saveImage,
        formatForFilenameSelection = ImageFormat.Png.Lossless,
        hasOriginalUri = false
    )

    ProcessImagesPreferenceSheet(
        uris = editSheetData,
        visible = showEditSheet,
        onDismiss = { showEditSheet = false },
        onNavigate = component.onNavigate
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )
}
