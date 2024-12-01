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

package ru.tech.imageresizershrinker.feature.markup_layers.presentation

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.window.PopupProperties
import androidx.core.graphics.applyCanvas
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageTooltip
import ru.tech.imageresizershrinker.core.resources.icons.Stacks
import ru.tech.imageresizershrinker.core.settings.presentation.provider.rememberAppColorTuple
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ColorRowSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedDropdownMenu
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors
import ru.tech.imageresizershrinker.feature.markup_layers.domain.LayerType
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.AddTextLayerDialog
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.AnimatedBorder
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.Layer
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.LayerContent
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.model.BackgroundBehavior
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.model.UiMarkupLayer
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent

@Composable
fun MarkupLayersContent(
    component: MarkupLayersComponent
) {
    val context = LocalComponentActivity.current

    val themeState = LocalDynamicThemeState.current

    val appColorTuple = rememberAppColorTuple()

    val essentials = rememberLocalEssentials()
    val scope = essentials.coroutineScope
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.backgroundBehavior !is BackgroundBehavior.None && component.haveChanges) {
            showExitDialog = true
        } else if (component.backgroundBehavior !is BackgroundBehavior.None) {
            component.resetState()
            themeState.updateColorTuple(appColorTuple)
        } else {
            component.onGoBack()
        }
    }

    AutoContentBasedColors(component.bitmap)

    val imagePicker = rememberImagePicker { uri: Uri ->
        component.setUri(
            uri = uri,
            onFailure = essentials::showFailureToast
        )
    }

    val pickImage = imagePicker::pickImage

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    val configuration = LocalConfiguration.current
    val isPortrait by isPortraitOrientationAsState()

    val bitmap =
        component.bitmap ?: (component.backgroundBehavior as? BackgroundBehavior.Color)?.run {
            remember {
                ImageBitmap(width, height).asAndroidBitmap()
                    .applyCanvas { drawColor(color) }
            }
        } ?: remember {
            ImageBitmap(
                configuration.screenWidthDp,
                configuration.screenHeightDp
            ).asAndroidBitmap()
        }

    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val focus = LocalFocusManager.current
    AdaptiveBottomScaffoldLayoutScreen(
        autoClearFocus = false,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
                focus.clearFocus()
                component.deactivateAllLayers()
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.marquee()
            ) {
                Text(
                    text = stringResource(R.string.markup_layers)
                )
                Badge(
                    content = {
                        Text(stringResource(R.string.beta))
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .padding(bottom = 12.dp)
                )
            }
        },
        onGoBack = onBack,
        isPortrait = isPortrait,
        shouldDisableBackHandler = component.backgroundBehavior is BackgroundBehavior.None,
        actions = {
            val layerImagePicker = rememberImagePicker { uri: Uri ->
                component.deactivateAllLayers()
                component.addLayer(
                    UiMarkupLayer(
                        type = LayerType.Image(uri)
                    )
                )
            }
            var showTextEnteringDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showLayersSelection by rememberSaveable {
                mutableStateOf(false)
            }

            Box {
                EnhancedIconButton(
                    containerColor = takeColorFromScheme {
                        if (showLayersSelection) tertiary
                        else Color.Transparent
                    },
                    onClick = {
                        showLayersSelection = !showLayersSelection
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Stacks,
                        contentDescription = null
                    )
                }

                EnhancedDropdownMenu(
                    expanded = showLayersSelection,
                    onDismissRequest = { },
                    properties = PopupProperties(dismissOnClickOutside = false)
                ) {
                    component.layers.forEach { (type, state) ->
                        BoxWithConstraints(
                            modifier = Modifier
                                .size(128.dp)
                                .clip(
                                    RoundedCornerShape(4.dp)
                                )
                                .transparencyChecker(),
                            contentAlignment = Alignment.Center
                        ) {
                            val scope = this

                            Box(
                                modifier = Modifier.graphicsLayer(
                                    scaleX = state.scale,
                                    scaleY = state.scale,
                                    rotationZ = state.rotation
                                )
                            ) {
                                LayerContent(
                                    modifier = Modifier.sizeIn(
                                        maxWidth = scope.maxWidth,
                                        maxHeight = scope.maxHeight
                                    ),
                                    type = type,
                                    textFullSize = scope.constraints.run {
                                        minOf(maxWidth, maxHeight)
                                    }
                                )
                            }

                            AnimatedBorder(
                                modifier = Modifier.matchParentSize(),
                                alpha = animateFloatAsState(if (state.isActive) 1f else 0f).value,
                                scale = state.scale,
                                shape = RoundedCornerShape(4.dp)
                            )
                        }
                    }
                }
            }
            EnhancedIconButton(
                onClick = {
                    showTextEnteringDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.TextFields,
                    contentDescription = null
                )
            }
            EnhancedIconButton(
                onClick = layerImagePicker::pickImage
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null
                )
            }

            AddTextLayerDialog(
                visible = showTextEnteringDialog,
                onDismiss = { showTextEnteringDialog = false },
                onAddLayer = {
                    component.deactivateAllLayers()
                    component.addLayer(it)
                }
            )
        },
        topAppBarPersistentActions = { scaffoldState ->
            if (component.backgroundBehavior == BackgroundBehavior.None) TopAppBarEmoji()
            else {
                if (isPortrait) {
                    EnhancedIconButton(
                        onClick = {
                            scope.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                    scaffoldState.bottomSheetState.partialExpand()
                                } else {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.properties)
                        )
                    }
                }
                var editSheetData by remember {
                    mutableStateOf(listOf<Uri>())
                }
                ShareButton(
                    enabled = component.backgroundBehavior !is BackgroundBehavior.None,
                    onShare = {
                        component.shareBitmap(showConfetti)
                    },
                    onCopy = { manager ->
                        component.cacheCurrentImage { uri ->
                            manager.setClip(uri.asClip(context))
                            showConfetti()
                        }
                    },
                    onEdit = {
                        component.cacheCurrentImage { uri ->
                            editSheetData = listOf(uri)
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
        },
        mainContent = {
            val imageBitmap by remember(bitmap) {
                derivedStateOf {
                    bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                }
            }
            val direction = LocalLayoutDirection.current
            val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    //TODO: Improve
                    .zoomable(
                        zoomState = rememberZoomState(maxScale = 10f),
                        zoomEnabled = !component.layers.fastAny { it.state.isActive }
                    ),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .padding(
                            start = WindowInsets
                                .displayCutout
                                .asPaddingValues()
                                .calculateStartPadding(direction)
                        )
                        .padding(16.dp)
                        .aspectRatio(aspectRatio, isPortrait)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(2.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant(),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize()
                    )
                    component.layers.forEachIndexed { index, layer ->
                        Layer(
                            layer = layer,
                            onActivate = {
                                component.activateLayer(layer)
                            },
                            onUpdateLayer = {
                                component.updateLayerAt(index, it)
                            }
                        )
                    }
                }
            }
        },
        controls = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SaveExifWidget(
                    modifier = Modifier.fillMaxWidth(),
                    checked = component.saveExif,
                    imageFormat = component.imageFormat,
                    onCheckedChange = component::setSaveExif
                )
                ImageFormatSelector(
                    modifier = Modifier.navigationBarsPadding(),
                    forceEnabled = component.backgroundBehavior is BackgroundBehavior.Color,
                    value = component.imageFormat,
                    onValueChange = component::setImageFormat
                )
            }
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (component.backgroundBehavior is BackgroundBehavior.None) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                isSecondaryButtonVisible = component.backgroundBehavior !is BackgroundBehavior.Color,
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                isPrimaryButtonVisible = component.backgroundBehavior !is BackgroundBehavior.None,
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
                },
                showNullDataButtonAsContainer = true
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmap,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        enableNoDataScroll = false,
        noDataControls = {
            var showBackgroundDrawingSetup by rememberSaveable { mutableStateOf(false) }

            val cutout = WindowInsets.displayCutout.asPaddingValues()
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxHeight(),
                columns = StaggeredGridCells.Adaptive(300.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(
                    bottom = 12.dp + WindowInsets
                        .navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding(),
                    top = 12.dp,
                    end = 12.dp + cutout.calculateEndPadding(
                        LocalLayoutDirection.current
                    ),
                    start = 12.dp + cutout.calculateStartPadding(
                        LocalLayoutDirection.current
                    )
                ),
            ) {
                item {
                    PreferenceItem(
                        onClick = pickImage,
                        startIcon = Icons.Outlined.ImageTooltip,
                        title = stringResource(R.string.layers_on_image),
                        subtitle = stringResource(R.string.layers_on_image_sub),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    PreferenceItem(
                        onClick = { showBackgroundDrawingSetup = true },
                        startIcon = Icons.Outlined.FormatPaint,
                        title = stringResource(R.string.layers_on_background),
                        subtitle = stringResource(R.string.layers_on_background_sub),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            val density = LocalDensity.current
            val screenWidth = with(density) { configuration.screenWidthDp.dp.roundToPx() }
            val screenHeight = with(density) { configuration.screenHeightDp.dp.roundToPx() }

            var width by remember(
                showBackgroundDrawingSetup,
                screenWidth
            ) {
                mutableIntStateOf(screenWidth)
            }
            var height by remember(
                showBackgroundDrawingSetup,
                screenHeight
            ) {
                mutableIntStateOf(screenHeight)
            }
            var sheetBackgroundColor by rememberSaveable(
                showBackgroundDrawingSetup,
                stateSaver = ColorSaver
            ) {
                mutableStateOf(Color.White)
            }
            EnhancedModalBottomSheet(
                title = {
                    TitleItem(
                        text = stringResource(R.string.markup_layers),
                        icon = Icons.Rounded.Stacks
                    )
                },
                confirmButton = {
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            showBackgroundDrawingSetup = false
                            component.startDrawOnBackground(
                                reqWidth = width,
                                reqHeight = height,
                                color = sheetBackgroundColor
                            )
                        }
                    ) {
                        AutoSizeText(stringResource(R.string.ok))
                    }
                },
                sheetContent = {
                    Box {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            Row(
                                Modifier
                                    .padding(16.dp)
                                    .container(shape = RoundedCornerShape(24.dp))
                            ) {
                                RoundedTextField(
                                    value = width.takeIf { it != 0 }?.toString() ?: "",
                                    onValueChange = {
                                        width = it.restrict(8192).toIntOrNull() ?: 0
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    label = {
                                        Text(stringResource(R.string.width, " "))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(
                                            start = 8.dp,
                                            top = 8.dp,
                                            bottom = 4.dp,
                                            end = 4.dp
                                        )
                                )
                                RoundedTextField(
                                    value = height.takeIf { it != 0 }?.toString() ?: "",
                                    onValueChange = {
                                        height = it.restrict(8192).toIntOrNull() ?: 0
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    label = {
                                        Text(stringResource(R.string.height, " "))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(
                                            start = 4.dp,
                                            top = 8.dp,
                                            bottom = 4.dp,
                                            end = 8.dp
                                        ),
                                )
                            }
                            ColorRowSelector(
                                value = sheetBackgroundColor,
                                onValueChange = { sheetBackgroundColor = it },
                                icon = Icons.Rounded.FormatColorFill,
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )
                                    .container(RoundedCornerShape(24.dp))
                            )
                        }
                    }
                },
                visible = showBackgroundDrawingSetup,
                onDismiss = {
                    showBackgroundDrawingSetup = it
                }
            )
        },
        canShowScreenData = component.backgroundBehavior !is BackgroundBehavior.None,
        mainContentWeight = 0.65f
    )

    LoadingDialog(
        visible = component.isSaving || component.isImageLoading,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving
    )

    ExitWithoutSavingDialog(
        onExit = {
            if (component.backgroundBehavior !is BackgroundBehavior.None) {
                component.resetState()
                themeState.updateColorTuple(appColorTuple)
            } else component.onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}