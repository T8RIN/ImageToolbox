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

package ru.tech.imageresizershrinker.feature.generate_palette.presentation

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
import androidx.compose.material.icons.rounded.Colorize
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
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Theme
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.SimplePicture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.components.GeneratePaletteScreenControls
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.screenLogic.GeneratePaletteComponent
import ru.tech.imageresizershrinker.feature.pick_color.presentation.components.PickColorFromImageSheet

@Composable
fun GeneratePaletteContent(
    component: GeneratePaletteComponent
) {
    val essentials = rememberLocalEssentials()

    var useMaterialYouPalette by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }

    var showPreferencePicker by rememberSaveable(component.initialUri) {
        mutableStateOf(component.initialUri != null)
    }

    AutoContentBasedColors(
        model = component.bitmap,
        allowChangeColor = useMaterialYouPalette == false
    )

    val imagePicker = rememberImagePicker { uri: Uri ->
        showPreferencePicker = true
        component.setUri(
            uri = uri,
            onFailure = essentials::showFailureToast
        )
    }

    AutoFilePicker(
        onAutoPick = imagePicker::pickImage,
        isPickedAlready = component.initialUri != null
    )

    val paletteImageLauncher = rememberImagePicker { uri: Uri ->
        useMaterialYouPalette = false
        component.setUri(
            uri = uri,
            onFailure = essentials::showFailureToast
        )
    }

    val materialYouImageLauncher = rememberImagePicker { uri: Uri ->
        useMaterialYouPalette = true
        component.setUri(
            uri = uri,
            onFailure = essentials::showFailureToast
        )
    }

    val pickImage = when (useMaterialYouPalette) {
        true -> materialYouImageLauncher::pickImage
        false -> paletteImageLauncher::pickImage
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
                        useMaterialYouPalette = false
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
                        useMaterialYouPalette = true
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
        shouldDisableBackHandler = useMaterialYouPalette == null,
        title = {
            TopAppBarTitle(
                title = if (useMaterialYouPalette == true) {
                    stringResource(R.string.material_you)
                } else stringResource(R.string.palette),
                input = component.bitmap,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = {
            if (useMaterialYouPalette != null) {
                useMaterialYouPalette = null
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
                        imageVector = Icons.Rounded.Colorize,
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
        showImagePreviewAsStickyHeader = useMaterialYouPalette == false,
        placeImagePreview = useMaterialYouPalette == false,
        controls = {
            component.bitmap?.let { bitmap ->
                GeneratePaletteScreenControls(
                    bitmap = bitmap,
                    useMaterialYouPalette = useMaterialYouPalette
                )
            }
        },
        buttons = { actions ->
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }

            BottomButtonsBlock(
                isNoData = useMaterialYouPalette == null || component.bitmap == null,
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
            if (useMaterialYouPalette == null) 12.dp
            else 20.dp
        ).value,
        insetsForNoData = WindowInsets(0),
        noDataControls = {
            preferences()
        },
        canShowScreenData = useMaterialYouPalette != null && component.bitmap != null
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