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

package com.t8rin.imagetoolbox.feature.fractal_generation.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.CenterFocusStrong
import com.t8rin.imagetoolbox.core.resources.icons.Redo
import com.t8rin.imagetoolbox.core.resources.icons.RotateRight
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.resources.icons.Undo
import com.t8rin.imagetoolbox.core.ui.utils.animation.animate
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.state.derivedValueOf
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ResizeImageField
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.formattedZoom
import com.t8rin.imagetoolbox.feature.fractal_generation.presentation.components.FractalParamsSelection
import com.t8rin.imagetoolbox.feature.fractal_generation.presentation.components.FractalPreview
import com.t8rin.imagetoolbox.feature.fractal_generation.presentation.screenLogic.FractalGenerationComponent
import kotlinx.coroutines.launch

@Composable
fun FractalGenerationContent(
    component: FractalGenerationComponent
) {
    val isPortrait by isPortraitOrientationAsState()
    val scope = rememberCoroutineScope()
    var previewAreaSize by remember { mutableStateOf(IntSize.Zero) }
    var showCameraGestureGuide by rememberSaveable { mutableStateOf(true) }

    DisposableEffect(component) {
        component.onPreviewAttached()
        onDispose(component::onPreviewDetached)
    }

    LaunchedEffect(previewAreaSize) {
        component.updateAvailableOutputSize(
            width = previewAreaSize.width,
            height = previewAreaSize.height
        )
    }
    val saveFractal: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveFractal(oneTimeSaveLocationUri = it)
    }

    val shareButton: @Composable () -> Unit = {
        var editSheetData by remember {
            mutableStateOf(listOf<Uri>())
        }
        ShareButton(
            onShare = component::shareFractal,
            onCopy = {
                component.cacheCurrentFractal(Clipboard::copy)
            },
            onEdit = {
                component.cacheCurrentFractal {
                    editSheetData = listOf(it)
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

    val secondaryControls: @Composable () -> Unit = {
        EnhancedIconButton(
            onClick = component::resetViewport,
            enabled = component.params != component.params.resetView()
        ) {
            Icon(
                imageVector = Icons.Rounded.CenterFocusStrong,
                contentDescription = null
            )
        }
        EnhancedIconButton(
            onClick = component::undo,
            enabled = component.canUndo
        ) {
            Icon(
                imageVector = Icons.Rounded.Undo,
                contentDescription = null
            )
        }
        EnhancedIconButton(
            onClick = component::redo,
            enabled = component.canRedo
        ) {
            Icon(
                imageVector = Icons.Rounded.Redo,
                contentDescription = null
            )
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    AdaptiveBottomScaffoldLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.fractal_generation),
                input = component.previewBitmap?.let { },
                isLoading = false,
                size = null,
                originalSize = null
            )
        },
        onGoBack = {
            if (component.haveChanges) {
                showExitDialog = true
            } else {
                component.onGoBack()
            }
        },
        shouldDisableBackHandler = !component.haveChanges,
        actions = {
            secondaryControls()
        },
        topAppBarPersistentActions = { scaffoldState ->
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
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = stringResource(R.string.properties)
                    )
                }
            }
            shareButton()
        },
        mainContent = {
            val direction = LocalLayoutDirection.current
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = WindowInsets
                            .displayCutout
                            .asPaddingValues()
                            .calculateStartPadding(direction)
                    )
                    .padding(16.dp)
                    .onSizeChanged { previewAreaSize = it },
                contentAlignment = Alignment.Center
            ) {
                FractalPreview(
                    frame = component.previewFrame,
                    renderAspectRatio = component.outputSize.aspectRatio,
                    isLoading = component.isImageLoading,
                    isThreeDimensional = component.params.formula.isThreeDimensional,
                    showCameraGestureGuide = showCameraGestureGuide,
                    backgroundColor = component.params.insideColor.toColor(),
                    onGestureStart = component::onViewportGestureStart,
                    onGesture = component::onViewportGesture,
                    onGestureEnd = component::onViewportGestureEnd,
                    onCopyCoordinate = component::copyCoordinateAt,
                    onFrameDisplayed = component::onPreviewFrameDisplayed,
                    modifier = if (
                        component.previewBitmap == null ||
                        component.specifiedOutputSize.isZero()
                    ) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .aspectRatio(
                                ratio = component.outputSize.safeAspectRatio.animate(),
                                matchHeightConstraintsFirst = isPortrait
                            )
                            .fillMaxSize()
                    }
                )
                EnhancedBadge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = ShapeDefaults.extraSmall
                ) {
                    Text(
                        text = "${stringResource(R.string.zoom)} " + "${component.params.formattedZoom()}×",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                    )
                }
            }
        },
        controls = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isPortrait) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .container(shape = ShapeDefaults.circle)
                    ) {
                        secondaryControls()
                    }
                }
                ResizeImageField(
                    imageInfo = derivedValueOf(component.specifiedOutputSize) {
                        ImageInfo(
                            component.specifiedOutputSize.width,
                            component.specifiedOutputSize.height
                        )
                    },
                    originalSize = null,
                    onWidthChange = component::setOutputWidth,
                    onHeightChange = component::setOutputHeight
                )
                FractalParamsSelection(
                    value = component.params,
                    supportedFormulas = component.supportedFormulas,
                    onValueChange = component::updateParams,
                    onFormulaChange = component::setFormula,
                    previewProvider = component::getFractalPreviewTransformation
                )
                AnimatedVisibility(
                    visible = component.params.formula.isThreeDimensional,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PreferenceRowSwitch(
                        title = stringResource(R.string.fractal_camera_gesture_guide),
                        subtitle = stringResource(
                            R.string.fractal_camera_gesture_guide_subtitle
                        ),
                        checked = showCameraGestureGuide,
                        onClick = { showCameraGestureGuide = it },
                        startIcon = Icons.Rounded.RotateRight,
                        shape = ShapeDefaults.large,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                ImageFormatSelector(
                    value = component.imageFormat,
                    onValueChange = component::setImageFormat,
                    forceEnabled = true,
                    quality = component.quality
                )
                QualitySelector(
                    quality = component.quality,
                    imageFormat = component.imageFormat,
                    onQualityChange = component::setQuality
                )
            }
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = false,
                isSecondaryButtonVisible = false,
                onSecondaryButtonClick = {},
                onPrimaryButtonClick = {
                    saveFractal(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) actions()
                },
                showNullDataButtonAsContainer = true,
                drawBothStrokes = true
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveFractal,
                formatForFilenameSelection = component.getFormatForFilenameSelection(),
                hasOriginalUri = false
            )
        },
        enableNoDataScroll = false,
        canShowScreenData = true,
        showActionsInTopAppBar = false,
        mainContentWeight = 0.65f
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )
}
