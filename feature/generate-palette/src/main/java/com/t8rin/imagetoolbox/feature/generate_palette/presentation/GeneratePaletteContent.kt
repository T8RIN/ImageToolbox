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

package com.t8rin.imagetoolbox.feature.generate_palette.presentation

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
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Eyedropper
import com.t8rin.imagetoolbox.core.resources.icons.Theme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
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
import com.t8rin.imagetoolbox.feature.generate_palette.presentation.components.GeneratePaletteScreenControls
import com.t8rin.imagetoolbox.feature.generate_palette.presentation.components.PaletteType
import com.t8rin.imagetoolbox.feature.generate_palette.presentation.screenLogic.GeneratePaletteComponent
import com.t8rin.imagetoolbox.feature.pick_color.presentation.components.PickColorFromImageSheet

@Composable
fun GeneratePaletteContent(
    component: GeneratePaletteComponent
) {
    val paletteType = component.paletteType

    var showPreferencePicker by rememberSaveable(component.initialUri) {
        mutableStateOf(component.initialUri != null)
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

    val pickImage = when (paletteType) {
        PaletteType.MaterialYou -> materialYouImageLauncher::pickImage
        PaletteType.Default -> paletteImageLauncher::pickImage
        null -> imagePicker::pickImage
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
            val screen = remember {
                Screen.GeneratePalette()
            }
            PreferenceItem(
                title = stringResource(screen.title),
                subtitle = stringResource(screen.subtitle),
                startIcon = screen.icon,
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
        if (isPortrait) {
            Column {
                preference1()
                Spacer(modifier = Modifier.height(8.dp))
                preference2()
            }
        } else {
            val direction = LocalLayoutDirection.current
            Row(
                modifier = Modifier.padding(
                    WindowInsets.displayCutout.asPaddingValues()
                        .let {
                            PaddingValues(
                                start = it.calculateStartPadding(direction),
                                end = it.calculateEndPadding(direction)
                            )
                        }
                )
            ) {
                preference1.withModifier(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                preference2.withModifier(modifier = Modifier.weight(1f))
            }
        }
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = paletteType == null,
        title = {
            TopAppBarTitle(
                title = when (paletteType) {
                    PaletteType.MaterialYou -> stringResource(R.string.material_you)
                    PaletteType.Default, null -> stringResource(R.string.palette)
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
            if (component.uri != null) {
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
            component.bitmap?.let { bitmap ->
                GeneratePaletteScreenControls(
                    bitmap = bitmap,
                    paletteType = paletteType
                )
            }
        },
        buttons = { actions ->
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }

            BottomButtonsBlock(
                isNoData = paletteType == null || component.bitmap == null,
                onSecondaryButtonClick = pickImage,
                isPrimaryButtonVisible = false,
                onPrimaryButtonClick = {},
                showNullDataButtonAsContainer = true,
                actions = {
                    if (isPortrait) actions()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }
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
        noDataControls = {
            preferences()
        },
        canShowScreenData = paletteType != null && component.bitmap != null
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
        visible = component.isImageLoading,
        canCancel = false
    )
}