/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.Eyedropper
import com.t8rin.imagetoolbox.core.resources.icons.PaletteSwatch
import com.t8rin.imagetoolbox.core.resources.icons.Theme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.SimplePicture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withModifier
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.PaletteToolsScreenControls
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.PaletteType
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.screenLogic.PaletteToolsComponent
import com.t8rin.imagetoolbox.feature.pick_color.presentation.components.PickColorFromImageSheet

@Composable
fun PaletteToolsContent(
    component: PaletteToolsComponent
) {
    val paletteType = component.paletteType
    val essentials = rememberLocalEssentials()

    var showPreferencePicker by rememberSaveable(component.initialUri) {
        mutableStateOf(component.initialUri != null && paletteType == PaletteType.Default || paletteType == PaletteType.MaterialYou)
    }

    AutoContentBasedColors(
        model = component.bitmap,
        allowChangeColor = paletteType == PaletteType.Default
    )

    val imagePicker = rememberImagePicker { uri: Uri ->
        showPreferencePicker = true
        component.setUri(uri)
    }

    AutoFilePicker(
        onAutoPick = imagePicker::pickImage,
        isPickedAlready = component.initialUri != null
    )

    val paletteImageLauncher = rememberImagePicker { uri: Uri ->
        component.setPaletteType(PaletteType.Default)
        component.setUri(uri)
    }

    val materialYouImageLauncher = rememberImagePicker { uri: Uri ->
        component.setPaletteType(PaletteType.MaterialYou)
        component.setUri(uri)
    }

    val paletteFormatPicker = rememberFilePicker { uri: Uri ->
        component.setPaletteType(PaletteType.Edit)
        component.setUri(uri)
    }

    val pickImage = when (paletteType) {
        PaletteType.MaterialYou -> materialYouImageLauncher::pickImage
        PaletteType.Default -> paletteImageLauncher::pickImage
        PaletteType.Edit -> paletteFormatPicker::pickFile
        null -> imagePicker::pickImage
    }

    val paletteSaver = rememberFileCreator { uri ->
        component.savePaletteTo(
            uri = uri,
            onResult = essentials::parseFileSaveResult
        )
    }

    val isPortrait by isPortraitOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }

    ZoomModalSheet(
        data = component.bitmap,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        }
    )

    var showColorPickerSheet by rememberSaveable { mutableStateOf(false) }

    val preferences: @Composable () -> Unit = {
        val preference1 = @Composable {
            PreferenceItem(
                title = stringResource(R.string.generate_palette),
                subtitle = stringResource(R.string.palette_sub),
                startIcon = Icons.Outlined.PaletteSwatch,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (component.bitmap == null) {
                        paletteImageLauncher.pickImage()
                    } else {
                        component.setPaletteType(PaletteType.Default)
                    }
                    showPreferencePicker = false
                }
            )
        }
        val preference2 = @Composable {
            PreferenceItem(
                title = stringResource(R.string.material_you),
                subtitle = stringResource(R.string.material_you_sub),
                startIcon = Icons.Outlined.Theme,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (component.bitmap == null) {
                        materialYouImageLauncher.pickImage()
                    } else {
                        component.setPaletteType(PaletteType.MaterialYou)
                    }
                    showPreferencePicker = false
                }
            )
        }
        val preference3 = @Composable {
            PreferenceItem(
                title = stringResource(R.string.edit_palette),
                subtitle = stringResource(R.string.edit_palette_sub),
                startIcon = Icons.AutoMirrored.Outlined.InsertDriveFile,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    component.setPaletteType(PaletteType.Edit)
                    showPreferencePicker = false
                }
            )
        }
        if (isPortrait) {
            Column {
                preference1()
                Spacer(modifier = Modifier.height(8.dp))
                preference2()
                Spacer(modifier = Modifier.height(8.dp))
                preference3()
            }
        } else {
            val direction = LocalLayoutDirection.current
            Column(
                Modifier.padding(
                    WindowInsets.displayCutout.asPaddingValues()
                        .let {
                            PaddingValues(
                                start = it.calculateStartPadding(direction),
                                end = it.calculateEndPadding(direction)
                            )
                        }
                )
            ) {
                Row {
                    preference1.withModifier(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    preference2.withModifier(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    preference3()
                    Spacer(modifier = Modifier.width(8.dp))
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = paletteType == null,
        title = {
            TopAppBarTitle(
                title = when (paletteType) {
                    PaletteType.MaterialYou -> stringResource(R.string.material_you)

                    PaletteType.Default -> stringResource(R.string.generate_palette)

                    PaletteType.Edit -> {
                        val base = stringResource(R.string.edit_palette)
                        val end = component.palette.colors.size.takeIf { it > 0 }?.let {
                            ": " + pluralStringResource(R.plurals.color_count, it, it)
                        }.orEmpty()

                        base + end
                    }

                    null -> stringResource(R.string.palette_tools)
                },
                input = component.bitmap,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = {
            if (paletteType != null) {
                component.setUri(null)
            } else {
                component.onGoBack()
            }
        },
        actions = {
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.bitmap != null,
            )
            if (component.bitmap != null) {
                EnhancedIconButton(
                    onClick = {
                        showColorPickerSheet = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Eyedropper,
                        contentDescription = stringResource(R.string.pipette)
                    )
                }
            }
            if (paletteType == PaletteType.Edit) {
                ShareButton(
                    onShare = {
                        component.sharePalette(
                            onComplete = essentials::showConfetti
                        )
                    },
                    enabled = component.palette.isNotEmpty()
                )
            }
        },
        topAppBarPersistentActions = {
            if (component.bitmap == null) {
                TopAppBarEmoji()
            }
        },
        imagePreview = {
            SimplePicture(bitmap = component.bitmap)
        },
        showImagePreviewAsStickyHeader = paletteType == PaletteType.Default,
        placeImagePreview = paletteType == PaletteType.Default,
        controls = {
            PaletteToolsScreenControls(
                component = component
            )
        },
        buttons = { actions ->
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }

            BottomButtonsBlock(
                isNoData = if (paletteType == PaletteType.Edit) {
                    !component.palette.isNotEmpty()
                } else {
                    paletteType == null || component.bitmap == null
                },
                onSecondaryButtonClick = pickImage,
                isPrimaryButtonVisible = paletteType == PaletteType.Edit && component.palette.isNotEmpty(),
                secondaryButtonIcon = if (paletteType == PaletteType.Edit) Icons.Rounded.FileOpen else Icons.Rounded.AddPhotoAlt,
                secondaryButtonText = stringResource(
                    if (paletteType == PaletteType.Edit) R.string.pick_file else R.string.pick_image_alt
                ),
                onPrimaryButtonClick = {
                    paletteSaver.make(component.createPaletteFilename())
                },
                showNullDataButtonAsContainer = true,
                actions = {
                    if (isPortrait) actions()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }.takeIf { paletteType != PaletteType.Edit }
            )

            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        contentPadding = animateDpAsState(
            if (paletteType == null) 12.dp
            else 20.dp
        ).value,
        insetsForNoData = WindowInsets(0),
        noDataControls = { preferences() },
        canShowScreenData = paletteType != null && (paletteType == PaletteType.Edit || component.bitmap != null)
    )

    var colorPickerValue by rememberSaveable(stateSaver = ColorSaver) {
        mutableStateOf(Color.Black)
    }
    PickColorFromImageSheet(
        visible = showColorPickerSheet,
        onDismiss = {
            showColorPickerSheet = false
        },
        bitmap = component.bitmap,
        onColorChange = {
            colorPickerValue = it
        },
        color = colorPickerValue
    )

    EnhancedModalBottomSheet(
        visible = showPreferencePicker,
        onDismiss = {
            showPreferencePicker = it
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showPreferencePicker = false
                }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.palette),
                icon = Icons.Rounded.Palette
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            preferences()
        }
    }


    LoadingDialog(
        visible = component.isImageLoading || component.isSaving,
        canCancel = false
    )
}