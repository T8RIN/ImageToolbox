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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Redo
import com.t8rin.imagetoolbox.core.resources.icons.Texture
import com.t8rin.imagetoolbox.core.resources.icons.Undo
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.buttons.EraseModeButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.PanModeButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageHeaderState
import com.t8rin.imagetoolbox.core.ui.widget.image.imageStickyHeader
import com.t8rin.imagetoolbox.core.ui.widget.modifier.CornerSides
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.modifier.only
import com.t8rin.imagetoolbox.core.ui.widget.saver.PtSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberAvailableHeight
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.BitmapDrawer
import com.t8rin.imagetoolbox.feature.draw.presentation.components.BrushSoftnessSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawPathModeSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.LineWidthSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import net.engawapg.lib.zoomable.rememberZoomState

@Composable
internal fun SeamCarvingMaskSheet(
    visible: Boolean,
    bitmap: Bitmap?,
    onDismiss: () -> Unit,
    onSave: (List<UiPathPaint>) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val isPortrait by isPortraitOrientationAsState()

    var paths by remember(visible) { mutableStateOf(emptyList<UiPathPaint>()) }
    var undonePaths by remember(visible) { mutableStateOf(emptyList<UiPathPaint>()) }
    var strokeWidth by rememberSaveable(visible, stateSaver = PtSaver) {
        mutableStateOf(settingsState.defaultDrawLineWidth.pt)
    }
    var brushSoftness by rememberSaveable(visible, stateSaver = PtSaver) {
        mutableStateOf(20.pt)
    }
    var panEnabled by rememberSaveable(visible) { mutableStateOf(false) }
    var isEraserOn by rememberSaveable(visible) { mutableStateOf(false) }
    var drawPathMode by remember(visible) { mutableStateOf<DrawPathMode>(DrawPathMode.Free) }
    var showExitDialog by remember { mutableStateOf(false) }
    var imageState by remember { mutableStateOf(ImageHeaderState(2)) }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (paths.isEmpty()) onDismiss()
            else showExitDialog = true
        },
        cancelable = false,
        title = {
            TitleItem(
                text = stringResource(id = R.string.add_mask),
                icon = Icons.Outlined.Texture
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = paths.isNotEmpty(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    onSave(paths)
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawHorizontalStroke(autoElevation = 3.dp)
                    .zIndex(Float.MAX_VALUE)
                    .background(EnhancedBottomSheetDefaults.barContainerColor)
                    .padding(8.dp)
            ) {
                EnhancedIconButton(
                    onClick = {
                        if (paths.isEmpty()) onDismiss()
                        else showExitDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.exit)
                    )
                }
            }
        },
        enableBackHandler = paths.isEmpty()
    ) {
        if (visible) {
            BackHandler(
                enabled = paths.isNotEmpty()
            ) {
                showExitDialog = true
            }
        }

        val drawPreview: @Composable () -> Unit = {
            SeamCarvingMaskBitmapPreview(
                bitmap = bitmap,
                imageState = imageState,
                paths = paths,
                strokeWidth = strokeWidth,
                brushSoftness = brushSoftness,
                isEraserOn = isEraserOn,
                panEnabled = panEnabled,
                drawPathMode = drawPathMode,
                onAddPath = {
                    paths = paths + it
                    undonePaths = emptyList()
                }
            )
        }

        Row {
            val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
            if (!isPortrait) {
                Box(modifier = Modifier.weight(1.3f)) {
                    drawPreview()
                }
            }
            val internalHeight = rememberAvailableHeight(imageState = imageState)
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                flingBehavior = enhancedFlingBehavior()
            ) {
                imageStickyHeader(
                    visible = isPortrait,
                    internalHeight = internalHeight,
                    imageState = imageState,
                    onStateChange = {
                        imageState = it
                    },
                    isControlsVisibleIndefinitely = true,
                    padding = 0.dp,
                    backgroundColor = backgroundColor,
                    imageBlock = drawPreview
                )
                item {
                    SeamCarvingMaskSheetControls(
                        pathsEmpty = paths.isEmpty(),
                        undonePathsEmpty = undonePaths.isEmpty(),
                        strokeWidth = strokeWidth,
                        onStrokeWidthChange = { strokeWidth = it.pt },
                        brushSoftness = brushSoftness,
                        onBrushSoftnessChange = { brushSoftness = it.pt },
                        panEnabled = panEnabled,
                        onTogglePanEnabled = { panEnabled = !panEnabled },
                        isEraserOn = isEraserOn,
                        onToggleIsEraserOn = { isEraserOn = !isEraserOn },
                        drawPathMode = drawPathMode,
                        onDrawPathModeChange = { drawPathMode = it },
                        onUndo = {
                            paths.lastOrNull()?.let {
                                paths = paths - it
                                undonePaths = undonePaths + it
                            }
                        },
                        onRedo = {
                            undonePaths.lastOrNull()?.let {
                                paths = paths + it
                                undonePaths = undonePaths - it
                            }
                        }
                    )
                }
            }
        }
    }

    ExitWithoutSavingDialog(
        onExit = onDismiss,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog,
        placeAboveAll = true
    )
}

@Composable
private fun SeamCarvingMaskBitmapPreview(
    bitmap: Bitmap?,
    imageState: ImageHeaderState,
    paths: List<UiPathPaint>,
    strokeWidth: Pt,
    brushSoftness: Pt,
    isEraserOn: Boolean,
    panEnabled: Boolean,
    drawPathMode: DrawPathMode,
    onAddPath: (UiPathPaint) -> Unit
) {
    val zoomState = rememberZoomState(maxScale = 30f, key = imageState)
    val isPortrait by isPortraitOrientationAsState()

    AnimatedContent(
        targetState = bitmap,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = Modifier
            .fillMaxSize()
            .clip(
                if (isPortrait) {
                    ShapeDefaults.extraLarge.only(
                        CornerSides.Bottom
                    )
                } else RectangleShape
            )
            .background(
                color = MaterialTheme.colorScheme
                    .surfaceContainer
                    .copy(0.8f)
            )
    ) { image ->
        if (image == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                EnhancedLoadingIndicator()
            }
        } else {
            BitmapDrawer(
                zoomState = zoomState,
                imageBitmap = image.asImageBitmap(),
                paths = paths,
                strokeWidth = strokeWidth,
                brushSoftness = brushSoftness,
                drawColor = Color.Red.copy(alpha = 0.55f),
                onAddPath = onAddPath,
                isEraserOn = isEraserOn,
                drawMode = DrawMode.Pen,
                modifier = Modifier
                    .padding(16.dp)
                    .aspectRatio(image.width / image.height.toFloat(), isPortrait)
                    .fillMaxSize(),
                panEnabled = panEnabled,
                onRequestFiltering = { source, _ -> source },
                drawPathMode = drawPathMode,
                backgroundColor = Color.Transparent
            )
        }
    }
}

@Composable
private fun SeamCarvingMaskSheetControls(
    pathsEmpty: Boolean,
    undonePathsEmpty: Boolean,
    strokeWidth: Pt,
    onStrokeWidthChange: (Float) -> Unit,
    brushSoftness: Pt,
    onBrushSoftnessChange: (Float) -> Unit,
    panEnabled: Boolean,
    onTogglePanEnabled: () -> Unit,
    isEraserOn: Boolean,
    onToggleIsEraserOn: () -> Unit,
    drawPathMode: DrawPathMode,
    onDrawPathModeChange: (DrawPathMode) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .container(shape = ShapeDefaults.circle)
    ) {
        PanModeButton(
            selected = panEnabled,
            onClick = onTogglePanEnabled
        )
        Spacer(Modifier.width(4.dp))
        EnhancedIconButton(
            containerColor = Color.Transparent,
            borderColor = MaterialTheme.colorScheme.outlineVariant(luminance = 0.1f),
            onClick = onUndo,
            enabled = !pathsEmpty
        ) {
            Icon(
                imageVector = Icons.Rounded.Undo,
                contentDescription = "Undo"
            )
        }
        EnhancedIconButton(
            containerColor = Color.Transparent,
            borderColor = MaterialTheme.colorScheme.outlineVariant(luminance = 0.1f),
            onClick = onRedo,
            enabled = !undonePathsEmpty
        ) {
            Icon(
                imageVector = Icons.Rounded.Redo,
                contentDescription = "Redo"
            )
        }
        EraseModeButton(
            selected = isEraserOn,
            enabled = !panEnabled,
            onClick = onToggleIsEraserOn
        )
    }

    DrawPathModeSelector(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp
        ),
        values = remember {
            listOf(
                DrawPathMode.Free,
                DrawPathMode.FloodFill(),
                DrawPathMode.Spray(),
                DrawPathMode.Lasso,
                DrawPathMode.Rect(),
                DrawPathMode.Oval,
                DrawPathMode.Triangle,
                DrawPathMode.Polygon(),
                DrawPathMode.Star()
            )
        },
        value = drawPathMode,
        onValueChange = onDrawPathModeChange,
        drawMode = DrawMode.Pen
    )
    AnimatedVisibility(
        visible = drawPathMode.canChangeStrokeWidth,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = Modifier.fillMaxWidth()
    ) {
        LineWidthSelector(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp
            ),
            color = Color.Unspecified,
            value = strokeWidth.value,
            onValueChange = onStrokeWidthChange
        )
    }
    BrushSoftnessSelector(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 16.dp
        ),
        color = Color.Unspecified,
        value = brushSoftness.value,
        onValueChange = onBrushSoftnessChange
    )
}