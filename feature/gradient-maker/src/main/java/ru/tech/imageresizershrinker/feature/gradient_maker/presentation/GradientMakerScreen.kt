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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation

import android.app.Activity
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.failedToSaveImages
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.AlphaSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.ColorStopSelection
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientPreview
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientPropertiesSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientSizeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientTypeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.TileModeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.rememberGradientState
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.viewModel.GradientMakerViewModel

@Composable
fun GradientMakerScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: GradientMakerViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )

    val context = LocalContext.current as Activity

    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch { confettiController.showEmpty() }
    }
    val toastHostState = LocalToastHost.current

    var allowPickingImage by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }

    LaunchedEffect(viewModel.brush, viewModel.selectedUri) {
        if (allowChangeColor && allowPickingImage != null) {
            viewModel.createGradientBitmap(
                data = viewModel.selectedUri,
                integerSize = IntegerSize(1000, 1000)
            )?.let { bitmap ->
                themeState.updateColorByImage(bitmap)
            }
        }
    }

    LaunchedEffect(allowPickingImage) {
        if (allowPickingImage != true) {
            viewModel.clearState()
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val onBack = {
        if (allowPickingImage == null) onGoBack()
        else showExitDialog = true
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            allowPickingImage = true
            viewModel.setUris(it) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
            viewModel.updateGradientAlpha(0.5f)
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.let {
                allowPickingImage = true
                viewModel.setUris(it) {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
                viewModel.updateGradientAlpha(0.5f)
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val showPickImageFromUrisSheet = rememberSaveable { mutableStateOf(false) }

    val showCompareSheet = rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        isPortrait = isPortrait,
        canShowScreenData = allowPickingImage != null,
        title = {
            TopAppBarTitle(
                title = if (allowPickingImage != true) {
                    stringResource(R.string.gradient_maker)
                } else stringResource(R.string.gradient_maker_type_image),
                input = Unit,
                isLoading = false,
                size = null
            )
        },
        onGoBack = onBack,
        actions = {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = {
                    viewModel.shareBitmaps { showConfetti() }
                },
                enabled = viewModel.brush != null
            ) {
                Icon(Icons.Outlined.Share, null)
            }
        },
        topAppBarPersistentActions = {
            if (allowPickingImage == null) {
                TopAppBarEmoji()
            }
            CompareButton(
                onClick = { showCompareSheet.value = true },
                visible = viewModel.brush != null && allowPickingImage == true && viewModel.selectedUri != Uri.EMPTY
            )
        },
        imagePreview = {
            Box(
                modifier = Modifier
                    .container()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                GradientPreview(
                    brush = viewModel.brush,
                    gradientAlpha = viewModel.gradientAlpha,
                    allowPickingImage = allowPickingImage,
                    gradientSize = viewModel.gradientSize,
                    onSizeChanged = viewModel::setPreviewSize,
                    selectedUri = viewModel.selectedUri,
                    imageAspectRatio = viewModel.imageAspectRatio
                )
            }
        },
        controls = {
            ImageCounter(
                imageCount = viewModel.uris.size.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet.value = true
                }
            )
            AnimatedContent(
                allowPickingImage == false
            ) { canChangeSize ->
                if (canChangeSize) {
                    GradientSizeSelector(
                        value = viewModel.gradientSize,
                        onWidthChange = viewModel::updateWidth,
                        onHeightChange = viewModel::updateHeight
                    )
                } else {
                    AlphaSelector(
                        value = viewModel.gradientAlpha,
                        onValueChange = viewModel::updateGradientAlpha,
                        modifier = Modifier
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            GradientTypeSelector(
                value = viewModel.gradientType,
                onValueChange = viewModel::setGradientType
            ) {
                GradientPropertiesSelector(
                    gradientType = viewModel.gradientType,
                    linearAngle = viewModel.angle,
                    onLinearAngleChange = viewModel::updateLinearAngle,
                    centerFriction = viewModel.centerFriction,
                    radiusFriction = viewModel.radiusFriction,
                    onRadialDimensionsChange = viewModel::setRadialProperties
                )
            }
            Spacer(Modifier.height(8.dp))
            ColorStopSelection(
                colorStops = viewModel.colorStops,
                onRemoveClick = viewModel::removeColorStop,
                onValueChange = viewModel::updateColorStop,
                onAddColorStop = viewModel::addColorStop
            )
            Spacer(Modifier.height(8.dp))
            TileModeSelector(
                value = viewModel.tileMode,
                onValueChange = viewModel::setTileMode
            )
            Spacer(Modifier.height(8.dp))
            SaveExifWidget(
                checked = viewModel.keepExif,
                imageFormat = viewModel.imageFormat,
                onCheckedChange = viewModel::toggleKeepExif
            )
            Spacer(Modifier.height(8.dp))
            ExtensionGroup(
                value = viewModel.imageFormat,
                enabled = true,
                onValueChange = viewModel::setImageFormat,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer
            )
        },
        noDataControls = {
            val preference1 = @Composable {
                val screen = remember {
                    Screen.GradientMaker()
                }
                PreferenceItem(
                    title = stringResource(screen.title),
                    subtitle = stringResource(screen.subtitle),
                    icon = screen.icon,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    onClick = {
                        allowPickingImage = false
                    }
                )
            }
            val preference2 = @Composable {
                PreferenceItem(
                    title = stringResource(R.string.gradient_maker_type_image),
                    subtitle = stringResource(R.string.gradient_maker_type_image_sub),
                    icon = Icons.Rounded.Collections,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    onClick = pickImage
                )
            }
            if (isPortrait) {
                Column {
                    preference1()
                    Spacer(modifier = Modifier.height(8.dp))
                    preference2()
                }
            } else {
                Row {
                    preference1.withModifier(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    preference2.withModifier(modifier = Modifier.weight(1f))
                }
            }
        },
        buttons = { actions ->
            val saveBitmap: () -> Unit = {
                if (viewModel.brush != null) {
                    viewModel.saveBitmaps(
                        onStandaloneGradientSaveResult = { saveResult ->
                            parseSaveResult(
                                saveResult = saveResult,
                                onSuccess = showConfetti,
                                toastHostState = toastHostState,
                                scope = scope,
                                context = context
                            )
                        },
                        onResult = { failed, savingPath ->
                            context.failedToSaveImages(
                                scope = scope,
                                failed = failed,
                                done = viewModel.done,
                                toastHostState = toastHostState,
                                savingPathString = savingPath,
                                showConfetti = showConfetti
                            )
                        }
                    )
                }
            }
            BottomButtonsBlock(
                targetState = (allowPickingImage == null) to isPortrait,
                onSecondaryButtonClick = pickImage,
                isPickImageButtonVisible = allowPickingImage == true,
                isPrimaryButtonVisible = viewModel.brush != null,
                showNullDataButtonAsContainer = true,
                onPrimaryButtonClick = saveBitmap,
                actions = {
                    if (isPortrait) actions()
                }
            )
        },
        contentPadding = animateDpAsState(
            if (allowPickingImage == null) 12.dp
            else 20.dp
        ).value
    )

    PickImageFromUrisSheet(
        transformations = remember(viewModel.brush) {
            derivedStateOf {
                listOf(
                    viewModel.getGradientTransformation()
                )
            }
        }.value,
        visible = showPickImageFromUrisSheet,
        uris = viewModel.uris,
        selectedUri = viewModel.selectedUri,
        onUriPicked = { uri ->
            viewModel.setUri(uri = uri) {
                scope.launch {
                    toastHostState.showError(context, it)
                }
            }
        },
        onUriRemoved = { uri ->
            viewModel.updateUrisSilently(removedUri = uri)
        },
        columns = if (isPortrait) 2 else 4,
    )

    CompareSheet(
        beforeContent = {
            Picture(
                model = viewModel.selectedUri,
                modifier = Modifier.aspectRatio(
                    viewModel.imageAspectRatio
                ),
                shape = MaterialTheme.shapes.medium
            )
        },
        afterContent = {
            val gradientState = rememberGradientState()
            LaunchedEffect(viewModel.brush) {
                gradientState.gradientType = viewModel.gradientType
                gradientState.linearGradientAngle = viewModel.angle
                gradientState.centerFriction = viewModel.centerFriction
                gradientState.radiusFriction = viewModel.radiusFriction
                gradientState.colorStops.apply {
                    clear()
                    addAll(viewModel.colorStops)
                }
                gradientState.tileMode = viewModel.tileMode
            }
            GradientPreview(
                brush = gradientState.brush,
                gradientAlpha = viewModel.gradientAlpha,
                allowPickingImage = allowPickingImage,
                gradientSize = viewModel.gradientSize,
                onSizeChanged = {
                    gradientState.size = it
                },
                selectedUri = viewModel.selectedUri,
                imageAspectRatio = viewModel.imageAspectRatio
            )
        },
        visible = showCompareSheet,
        shape = MaterialTheme.shapes.medium
    )

    if (viewModel.isSaving || viewModel.isImageLoading) {
        if (viewModel.left != -1) {
            LoadingDialog(
                done = viewModel.done,
                left = viewModel.left,
                onCancelLoading = viewModel::cancelSaving
            )
        } else {
            LoadingDialog(
                canCancel = viewModel.isSaving,
                onCancelLoading = viewModel::cancelSaving
            )
        }
    }

    ExitWithoutSavingDialog(
        onExit = {
            if (allowPickingImage != null) {
                allowPickingImage = null
                themeState.updateColorTuple(appColorTuple)
                viewModel.clearState()
            } else onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}