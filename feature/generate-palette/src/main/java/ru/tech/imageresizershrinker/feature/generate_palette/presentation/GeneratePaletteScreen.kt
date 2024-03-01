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

import android.content.res.Configuration
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.Theme
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.image.SimplePicture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.components.GeneratePaletteScreenControls
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.viewModel.GeneratePaletteViewModel
import ru.tech.imageresizershrinker.feature.pick_color.presentation.components.PickColorFromImageSheet

@Composable
fun GeneratePaletteScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: GeneratePaletteViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )

    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    var useMaterialYouPalette by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }

    var showPreferencePicker by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            showPreferencePicker = true

            viewModel.setUri(it) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
        }
    }

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor && useMaterialYouPalette == false) {
                themeState.updateColorByImage(it)
            }
        }
    }

    val pickImageLauncher = rememberImagePicker(
        mode = localImagePickerMode(Picker.Single)
    ) { uris ->
        uris.takeIf { it.isNotEmpty() }
            ?.firstOrNull()
            ?.let {
                showPreferencePicker = true
                viewModel.setUri(it) {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            }
    }

    val paletteImageLauncher = rememberImagePicker(
        mode = localImagePickerMode(Picker.Single)
    ) { uris ->
        uris.takeIf { it.isNotEmpty() }
            ?.firstOrNull()
            ?.let {
                useMaterialYouPalette = false
                viewModel.setUri(it) {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            }
    }

    val materialYouImageLauncher = rememberImagePicker(
        mode = localImagePickerMode(Picker.Single)
    ) { uris ->
        uris.takeIf { it.isNotEmpty() }
            ?.firstOrNull()
            ?.let {
                useMaterialYouPalette = true
                viewModel.setUri(it) {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            }
    }

    val pickImage = {
        when (useMaterialYouPalette) {
            true -> materialYouImageLauncher.pickImage()
            false -> paletteImageLauncher.pickImage()
            null -> pickImageLauncher.pickImage()
        }
    }

    val isLandscape =
        LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact || LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isPortrait = !isLandscape

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    ZoomModalSheet(
        data = viewModel.bitmap,
        visible = showZoomSheet
    )

    val showColorPickerSheet = rememberSaveable {
        mutableStateOf(false)
    }

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
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                onClick = {
                    if (viewModel.bitmap == null) {
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
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                onClick = {
                    if (viewModel.bitmap == null) {
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
        title = {
            TopAppBarTitle(
                title = if (useMaterialYouPalette == true) {
                    stringResource(R.string.material_you)
                } else stringResource(R.string.palette),
                input = viewModel.bitmap,
                isLoading = viewModel.isImageLoading,
                size = null
            )
        },
        onGoBack = {
            if (useMaterialYouPalette != null) {
                useMaterialYouPalette = null
                viewModel.setUri(null)
                themeState.updateColorTuple(appColorTuple)
            } else onGoBack()
        },
        actions = {
            ZoomButton(
                onClick = { showZoomSheet.value = true },
                visible = viewModel.bitmap != null,
            )
            if (viewModel.uri != null) {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = {
                        showColorPickerSheet.value = true
                    }
                ) {
                    Icon(Icons.Rounded.Colorize, null)
                }
            }
        },
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) {
                TopAppBarEmoji()
            }
        },
        imagePreview = {
            SimplePicture(bitmap = viewModel.bitmap)
        },
        showImagePreviewAsStickyHeader = useMaterialYouPalette == false,
        placeImagePreview = useMaterialYouPalette == false,
        controls = {
            viewModel.bitmap?.let { bitmap ->
                GeneratePaletteScreenControls(
                    bitmap = bitmap,
                    useMaterialYouPalette = useMaterialYouPalette
                )
            }
        },
        buttons = { actions ->
            BottomButtonsBlock(
                targetState = (useMaterialYouPalette == null || viewModel.bitmap == null) to isPortrait,
                onSecondaryButtonClick = pickImage,
                isPrimaryButtonVisible = false,
                onPrimaryButtonClick = {},
                showNullDataButtonAsContainer = true,
                actions = {
                    if (isPortrait) actions()
                }
            )
        },
        contentPadding = animateDpAsState(
            if (useMaterialYouPalette == null) 12.dp
            else 20.dp
        ).value,
        noDataControls = {
            preferences()
        },
        canShowScreenData = useMaterialYouPalette != null && viewModel.bitmap != null,
        isPortrait = isPortrait
    )

    var colorPickerValue by rememberSaveable(
        stateSaver = ColorSaver
    ) {
        mutableStateOf(Color.Black)
    }
    PickColorFromImageSheet(
        visible = showColorPickerSheet,
        bitmap = viewModel.bitmap,
        onColorChange = {
            colorPickerValue = it
        },
        color = colorPickerValue
    )

    SimpleSheet(
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


    if (viewModel.isImageLoading) {
        LoadingDialog(canCancel = false)
    }
}