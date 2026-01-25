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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.feature.markup_layers.presentation

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.zIndex
import androidx.core.graphics.applyCanvas
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.settings.presentation.provider.rememberAppColorTuple
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.utils.capturable.capturable
import com.t8rin.imagetoolbox.core.ui.utils.capturable.rememberCaptureController
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.controls.SaveExifWidget
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.Layer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.MarkupLayersActions
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.MarkupLayersNoDataControls
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.MarkupLayersSideMenu
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.MarkupLayersTopAppBarActions
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.MarkupLayersUndoRedo
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.BackgroundBehavior
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import kotlinx.coroutines.flow.collectLatest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun MarkupLayersContent(
    component: MarkupLayersComponent
) {
    AutoContentBasedColors(component.bitmap)

    val themeState = LocalDynamicThemeState.current

    val appColorTuple = rememberAppColorTuple()

    val essentials = rememberLocalEssentials()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        when (component.backgroundBehavior) {
            !is BackgroundBehavior.None if component.haveChanges -> showExitDialog = true

            !is BackgroundBehavior.None -> {
                component.resetState()
                themeState.updateColorTuple(appColorTuple)
            }

            else -> component.onGoBack()
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

    val screenSize = LocalScreenSize.current
    val isPortrait by isPortraitOrientationAsState()

    val bitmap =
        component.bitmap ?: (component.backgroundBehavior as? BackgroundBehavior.Color)?.run {
            remember(width, height, color) {
                ImageBitmap(width, height).asAndroidBitmap()
                    .applyCanvas { drawColor(color) }
            }
        } ?: remember {
            ImageBitmap(
                screenSize.widthPx,
                screenSize.heightPx
            ).asAndroidBitmap()
        }

    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showLayersSelection by rememberSaveable {
        mutableStateOf(false)
    }

    var isContextOptionsVisible by rememberSaveable {
        mutableStateOf(false)
    }

    AdaptiveBottomScaffoldLayoutScreen(
        autoClearFocus = false,
        modifier = Modifier
            .clearFocusOnTap()
            .tappable {
                component.deactivateAllLayers()
            },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.marquee()
            ) {
                Text(
                    text = stringResource(R.string.markup_layers)
                )
                EnhancedBadge(
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
        shouldDisableBackHandler = component.backgroundBehavior is BackgroundBehavior.None,
        actions = {
            MarkupLayersActions(
                component = component,
                showLayersSelection = showLayersSelection,
                onToggleLayersSection = { showLayersSelection = !showLayersSelection },
                onToggleLayersSectionQuick = {
                    showLayersSelection = true
                    isContextOptionsVisible = true
                }
            )
        },
        topAppBarPersistentActions = { scaffoldState ->
            MarkupLayersTopAppBarActions(
                component = component,
                scaffoldState = scaffoldState
            )
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
                    .zoomable(
                        zoomState = rememberZoomState(maxScale = 10f),
                        zoomEnabled = !component.layers.fastAny { it.state.isActive }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    val captureController = rememberCaptureController()
                    LaunchedEffect(captureController) {
                        component.captureRequestFlow.collectLatest {
                            if (it) {
                                component.sendCapturedImage(captureController.captureAsync())
                            }
                        }
                    }

                    Box(
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
                            .clip(ShapeDefaults.extremeSmall)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant(),
                                shape = ShapeDefaults.extremeSmall
                            )
                            .background(MaterialTheme.colorScheme.surfaceContainerLow),
                        contentAlignment = Alignment.Center
                    ) {
                        Picture(
                            model = imageBitmap,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .zIndex(-1f)
                                .matchParentSize()
                                .clipToBounds()
                                .transparencyChecker()
                        )
                        BoxWithConstraints(
                            modifier = Modifier
                                .matchParentSize()
                                .capturable(captureController),
                            contentAlignment = Alignment.Center
                        ) {
                            component.layers.forEachIndexed { index, layer ->
                                Layer(
                                    layer = layer,
                                    onActivate = {
                                        component.activateLayer(layer)
                                    },
                                    onUpdateLayer = {
                                        component.updateLayerAt(index, it)
                                    },
                                    onShowContextOptions = {
                                        showLayersSelection = true
                                        isContextOptionsVisible = true
                                    }
                                )
                            }
                        }
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
                if (!isPortrait) {
                    MarkupLayersUndoRedo(
                        component = component,
                        color = Color.Unspecified,
                        removePadding = false
                    )
                    Spacer(Modifier.height(4.dp))
                }
                val behavior = component.backgroundBehavior
                if (behavior is BackgroundBehavior.Color) {
                    ColorRowSelector(
                        value = behavior.color.toColor(),
                        onValueChange = component::updateBackgroundColor,
                        icon = Icons.Outlined.BackgroundColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ShapeDefaults.extraLarge
                            )
                    )
                }
                PreferenceRowSwitch(
                    title = stringResource(R.string.coerce_points_to_image_bounds),
                    subtitle = stringResource(R.string.coerce_points_to_image_bounds_sub),
                    startIcon = Icons.Outlined.Rectangle,
                    checked = component.coerceToBounds,
                    onClick = {
                        component.toggleCoerceToBounds()
                    },
                    shape = ShapeDefaults.large,
                    modifier = Modifier.fillMaxWidth()
                )
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
                isNoData = component.backgroundBehavior is BackgroundBehavior.None,
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
            MarkupLayersNoDataControls(
                component = component,
                onPickImage = pickImage
            )
        },
        canShowScreenData = component.backgroundBehavior !is BackgroundBehavior.None,
        mainContentWeight = 0.65f
    )

    MarkupLayersSideMenu(
        visible = showLayersSelection,
        onDismiss = { showLayersSelection = false },
        isContextOptionsVisible = isContextOptionsVisible,
        onContextOptionsVisibleChange = { isContextOptionsVisible = it },
        onRemoveLayer = component::removeLayer,
        onReorderLayers = component::reorderLayers,
        onActivateLayer = component::activateLayer,
        onCopyLayer = component::copyLayer,
        layers = component.layers
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