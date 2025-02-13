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

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.DensitySmall
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageOverlay
import ru.tech.imageresizershrinker.core.resources.icons.MeshDownload
import ru.tech.imageresizershrinker.core.resources.icons.MeshGradient
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShowOriginalButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.AlphaSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.detectSwipes
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.ColorStopSelection
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientPreview
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientPropertiesSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientSizeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientTypeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.MeshGradientEditor
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.TileModeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.generateMesh
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.rememberGradientState
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent
import kotlin.math.roundToInt

@Composable
fun GradientMakerContent(
    component: GradientMakerComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    var allowPickingImage by rememberSaveable(component.initialUris) {
        mutableStateOf(
            if (component.initialUris.isNullOrEmpty()) null
            else true
        )
    }

    AutoContentBasedColors(
        model = Triple(component.brush, component.meshPoints, component.selectedUri),
        selector = { (_, uri) ->
            component.createGradientBitmap(
                data = uri,
                integerSize = IntegerSize(1000, 1000)
            )
        },
        allowChangeColor = allowPickingImage != null
    )

    LaunchedEffect(allowPickingImage) {
        if (allowPickingImage != true && !component.isMeshGradient) {
            component.resetState()
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme
    LaunchedEffect(component.colorStops) {
        if (component.colorStops.isEmpty()) {
            colorScheme.apply {
                component.addColorStop(
                    pair = 0f to primary.blend(primaryContainer, 0.5f),
                    isInitial = true
                )
                component.addColorStop(
                    pair = 0.5f to secondary.blend(secondaryContainer, 0.5f),
                    isInitial = true
                )
                component.addColorStop(
                    pair = 1f to tertiary.blend(tertiaryContainer, 0.5f),
                    isInitial = true
                )
            }
        }
    }


    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        allowPickingImage = true
        component.setUris(
            uris = uris,
            onFailure = essentials::showFailureToast
        )
        component.updateGradientAlpha(0.5f)
    }

    val pickImage = imagePicker::pickImage

    val isPortrait by isPortraitOrientationAsState()

    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }

    var showCompareSheet by rememberSaveable { mutableStateOf(false) }
    var showOriginal by rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        isPortrait = isPortrait,
        canShowScreenData = allowPickingImage != null,
        title = {
            TopAppBarTitle(
                title = if (allowPickingImage != true) {
                    if (component.isMeshGradient) {
                        stringResource(R.string.mesh_gradients)
                    } else {
                        stringResource(R.string.gradient_maker)
                    }
                } else {
                    if (component.isMeshGradient) {
                        stringResource(R.string.gradient_maker_type_image_mesh)
                    } else {
                        stringResource(R.string.gradient_maker_type_image)
                    }
                },
                input = Unit,
                isLoading = false,
                size = null
            )
        },
        onGoBack = {
            if (component.haveChanges) showExitDialog = true
            else component.onGoBack()
        },
        actions = {
            if (component.uris.isNotEmpty()) {
                ShowOriginalButton(
                    canShow = true,
                    onStateChange = {
                        showOriginal = it
                    }
                )
            }
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = component.brush != null,
                onShare = {
                    component.shareBitmaps(showConfetti)
                },
                onCopy = { manager ->
                    component.cacheCurrentImage { uri ->
                        manager.copyToClipboard(uri.asClip(context))
                        showConfetti()
                    }
                },
                onEdit = {
                    component.cacheImages {
                        editSheetData = it
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
        },
        topAppBarPersistentActions = {
            if (allowPickingImage == null) {
                TopAppBarEmoji()
            }
            CompareButton(
                onClick = { showCompareSheet = true },
                visible = component.brush != null && allowPickingImage == true && component.selectedUri != Uri.EMPTY
            )
        },
        imagePreview = {
            Box(
                modifier = Modifier
                    .detectSwipes(
                        onSwipeRight = component::selectLeftUri,
                        onSwipeLeft = component::selectRightUri
                    )
                    .container()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (component.isMeshGradient) {
                    GradientPreview(
                        meshGradientState = component.meshGradientState,
                        gradientAlpha = if (showOriginal) 0f else component.gradientAlpha,
                        allowPickingImage = allowPickingImage,
                        gradientSize = component.gradientSize,
                        selectedUri = component.selectedUri,
                        imageAspectRatio = component.imageAspectRatio
                    )
                } else {
                    GradientPreview(
                        brush = component.brush,
                        gradientAlpha = if (showOriginal) 0f else component.gradientAlpha,
                        allowPickingImage = allowPickingImage,
                        gradientSize = component.gradientSize,
                        onSizeChanged = component::setPreviewSize,
                        selectedUri = component.selectedUri,
                        imageAspectRatio = component.imageAspectRatio
                    )
                }
            }
        },
        controls = {
            ImageCounter(
                imageCount = component.uris.size.takeIf { it > 1 },
                onRepick = {
                    showPickImageFromUrisSheet = true
                }
            )
            AnimatedContent(
                allowPickingImage == false
            ) { canChangeSize ->
                if (canChangeSize) {
                    GradientSizeSelector(
                        value = component.gradientSize,
                        onWidthChange = component::updateWidth,
                        onHeightChange = component::updateHeight
                    )
                } else {
                    AlphaSelector(
                        value = component.gradientAlpha,
                        onValueChange = component::updateGradientAlpha,
                        modifier = Modifier
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            if (component.isMeshGradient) {
                Column(
                    modifier = Modifier.container(
                        resultPadding = 0.dp
                    )
                ) {
                    Spacer(Modifier.height(16.dp))
                    TitleItem(
                        text = stringResource(R.string.points_customization),
                        icon = Icons.Rounded.Build,
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        )
                    )
                    MeshGradientEditor(
                        state = component.meshGradientState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(16.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                EnhancedSliderItem(
                    value = component.meshGradientState.gridSize,
                    title = stringResource(R.string.grid_size),
                    icon = Icons.Rounded.GridOn,
                    valueRange = 2f..6f,
                    internalStateTransformation = { it.roundToInt() },
                    onValueChange = { value ->
                        val size = value.roundToInt()
                        component.setResolution(lerp(1f, 64f, 2f / size))
                        component.meshGradientState.points.apply {
                            clear()
                            addAll(generateMesh(size))
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
                EnhancedSliderItem(
                    value = component.meshResolutionX,
                    title = stringResource(R.string.resolution),
                    icon = Icons.Rounded.DensitySmall,
                    valueRange = 1f..64f,
                    internalStateTransformation = { it.roundToInt() },
                    onValueChange = component::setResolution
                )
            } else {
                GradientTypeSelector(
                    value = component.gradientType,
                    onValueChange = component::setGradientType
                ) {
                    GradientPropertiesSelector(
                        gradientType = component.gradientType,
                        linearAngle = component.angle,
                        onLinearAngleChange = component::updateLinearAngle,
                        centerFriction = component.centerFriction,
                        radiusFriction = component.radiusFriction,
                        onRadialDimensionsChange = component::setRadialProperties
                    )
                }
                Spacer(Modifier.height(8.dp))
                ColorStopSelection(
                    colorStops = component.colorStops,
                    onRemoveClick = component::removeColorStop,
                    onValueChange = component::updateColorStop,
                    onAddColorStop = component::addColorStop
                )
                Spacer(Modifier.height(8.dp))
                TileModeSelector(
                    value = component.tileMode,
                    onValueChange = component::setTileMode
                )
            }
            if (allowPickingImage == true) {
                Spacer(Modifier.height(8.dp))
                SaveExifWidget(
                    checked = component.keepExif,
                    imageFormat = component.imageFormat,
                    onCheckedChange = component::toggleKeepExif
                )
            }
            Spacer(Modifier.height(8.dp))
            ImageFormatSelector(
                value = component.imageFormat,
                forceEnabled = allowPickingImage == false,
                onValueChange = component::setImageFormat
            )
        },
        insetsForNoData = WindowInsets(0),
        noDataControls = {
            val preference1 = @Composable {
                val screen = remember {
                    Screen.GradientMaker()
                }
                PreferenceItem(
                    title = stringResource(screen.title),
                    subtitle = stringResource(screen.subtitle),
                    startIcon = screen.icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        component.setIsMeshGradient(false)
                        allowPickingImage = false
                    }
                )
            }
            val preference2 = @Composable {
                PreferenceItem(
                    title = stringResource(R.string.gradient_maker_type_image),
                    subtitle = stringResource(R.string.gradient_maker_type_image_sub),
                    startIcon = Icons.Outlined.Collections,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        component.setIsMeshGradient(false)
                        pickImage()
                    }
                )
            }
            val preference3 = @Composable {
                PreferenceItem(
                    title = stringResource(R.string.mesh_gradients),
                    subtitle = stringResource(R.string.mesh_gradients_sub),
                    startIcon = Icons.Outlined.MeshGradient,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        component.setIsMeshGradient(true)
                        allowPickingImage = false
                    }
                )
            }
            val preference4 = @Composable {
                PreferenceItem(
                    title = stringResource(R.string.gradient_maker_type_image_mesh),
                    subtitle = stringResource(R.string.gradient_maker_type_image_mesh_sub),
                    startIcon = Icons.Outlined.ImageOverlay,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        component.setIsMeshGradient(true)
                        pickImage()
                    }
                )
            }
            val preference5 = @Composable {
                PreferenceItem(
                    title = stringResource(R.string.collection_mesh_gradients),
                    subtitle = stringResource(R.string.collection_mesh_gradients_sub),
                    startIcon = Icons.Outlined.MeshDownload,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        component.onNavigate(Screen.MeshGradients)
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
                    Spacer(modifier = Modifier.height(8.dp))
                    preference4()
                    Spacer(modifier = Modifier.height(8.dp))
                    preference5()
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                        )
                    ) {
                        preference1.withModifier(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        preference2.withModifier(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                        )
                    ) {
                        preference3.withModifier(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        preference4.withModifier(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    preference5.withModifier(modifier = Modifier.fillMaxWidth(0.5f))
                }
            }
        },
        buttons = { actions ->
            val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
                if (component.brush != null) {
                    component.saveBitmaps(
                        oneTimeSaveLocationUri = it,
                        onStandaloneGradientSaveResult = essentials::parseSaveResult,
                        onResult = essentials::parseSaveResults
                    )
                }
            }
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (allowPickingImage == null) to isPortrait,
                onSecondaryButtonClick = pickImage,
                isSecondaryButtonVisible = allowPickingImage == true,
                isPrimaryButtonVisible = component.brush != null,
                showNullDataButtonAsContainer = true,
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) actions()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmap,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        forceImagePreviewToMax = showOriginal,
        contentPadding = animateDpAsState(
            if (allowPickingImage == null) 12.dp
            else 20.dp
        ).value
    )

    val transformations by remember(
        component.brush,
        component.isMeshGradient,
        component.meshPoints,
        component.meshResolutionX,
        component.meshResolutionY,
        component.gradientAlpha
    ) {
        derivedStateOf {
            listOf(
                component.getGradientTransformation()
            )
        }
    }

    PickImageFromUrisSheet(
        transformations = transformations,
        visible = showPickImageFromUrisSheet,
        onDismiss = {
            showPickImageFromUrisSheet = false
        },
        uris = component.uris,
        selectedUri = component.selectedUri,
        onUriPicked = { uri ->
            component.updateSelectedUri(
                uri = uri,
                onFailure = essentials::showFailureToast
            )
        },
        onUriRemoved = { uri ->
            component.updateUrisSilently(removedUri = uri)
        },
        columns = if (isPortrait) 2 else 4,
    )

    CompareSheet(
        beforeContent = {
            Picture(
                model = component.selectedUri,
                modifier = Modifier.aspectRatio(
                    component.imageAspectRatio
                )
            )
        },
        afterContent = {
            if (component.isMeshGradient) {
                GradientPreview(
                    meshGradientState = component.meshGradientState,
                    gradientAlpha = component.gradientAlpha,
                    allowPickingImage = allowPickingImage,
                    gradientSize = component.gradientSize,
                    selectedUri = component.selectedUri,
                    imageAspectRatio = component.imageAspectRatio
                )
            } else {
                val gradientState = rememberGradientState()
                LaunchedEffect(component.brush) {
                    gradientState.gradientType = component.gradientType
                    gradientState.linearGradientAngle = component.angle
                    gradientState.centerFriction = component.centerFriction
                    gradientState.radiusFriction = component.radiusFriction
                    gradientState.colorStops.apply {
                        clear()
                        addAll(component.colorStops)
                    }
                    gradientState.tileMode = component.tileMode
                }
                GradientPreview(
                    brush = gradientState.brush,
                    gradientAlpha = component.gradientAlpha,
                    allowPickingImage = allowPickingImage,
                    gradientSize = component.gradientSize,
                    onSizeChanged = {
                        gradientState.size = it
                    },
                    selectedUri = component.selectedUri,
                    imageAspectRatio = component.imageAspectRatio
                )
            }
        },
        visible = showCompareSheet,
        onDismiss = {
            showCompareSheet = false
        }
    )

    LoadingDialog(
        visible = component.isSaving || component.isImageLoading,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving,
    )

    ExitWithoutSavingDialog(
        onExit = {
            if (allowPickingImage != null) {
                allowPickingImage = null
                component.resetState()
            } else {
                component.onGoBack()
            }
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}