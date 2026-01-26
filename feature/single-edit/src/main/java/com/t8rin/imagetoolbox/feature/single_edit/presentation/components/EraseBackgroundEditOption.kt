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

package com.t8rin.imagetoolbox.feature.single_edit.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.buttons.PanModeButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.HelperGridParamsSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.MagnifierEnabledSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.other.DrawLockScreenOrientation
import com.t8rin.imagetoolbox.core.ui.widget.saver.PtSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.BrushSoftnessSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawPathModeSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.LineWidthSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemover
import com.t8rin.imagetoolbox.feature.erase_background.presentation.components.AutoEraseBackgroundCard
import com.t8rin.imagetoolbox.feature.erase_background.presentation.components.BitmapEraser
import com.t8rin.imagetoolbox.feature.erase_background.presentation.components.OriginalImagePreviewAlphaSelector
import com.t8rin.imagetoolbox.feature.erase_background.presentation.components.RecoverModeButton
import com.t8rin.imagetoolbox.feature.erase_background.presentation.components.RecoverModeCard
import com.t8rin.imagetoolbox.feature.erase_background.presentation.components.TrimImageToggle
import kotlinx.coroutines.launch

@Composable
fun EraseBackgroundEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap, Boolean) -> Unit,
    clearErasing: (Boolean) -> Unit,
    undo: () -> Unit,
    redo: () -> Unit,
    paths: List<UiPathPaint>,
    lastPaths: List<UiPathPaint>,
    undonePaths: List<UiPathPaint>,
    drawPathMode: DrawPathMode,
    onUpdateDrawPathMode: (DrawPathMode) -> Unit,
    addPath: (UiPathPaint) -> Unit,
    autoBackgroundRemover: AutoBackgroundRemover<Bitmap>,
    helperGridParams: HelperGridParams,
    onUpdateHelperGridParams: (HelperGridParams) -> Unit,
) {
    val essentials = rememberLocalEssentials()

    bitmap?.let {
        var panEnabled by rememberSaveable { mutableStateOf(false) }

        val switch = @Composable {
            PanModeButton(
                selected = panEnabled,
                onClick = {
                    panEnabled = !panEnabled
                }
            )
        }

        var isRecoveryOn by rememberSaveable { mutableStateOf(false) }

        val settingsState = LocalSettingsState.current
        var strokeWidth by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(settingsState.defaultDrawLineWidth.pt) }
        var brushSoftness by rememberSaveable(stateSaver = PtSaver) {
            mutableStateOf(0.pt)
        }

        var originalImagePreviewAlpha by rememberSaveable {
            mutableFloatStateOf(0.2f)
        }

        var trimImage by rememberSaveable { mutableStateOf(true) }

        val secondaryControls = @Composable {
            Row(
                modifier = Modifier
                    .then(
                        if (!useScaffold) {
                            Modifier
                                .padding(16.dp)
                                .container(shape = ShapeDefaults.circle)
                        } else Modifier
                    )
            ) {
                switch()
                Spacer(Modifier.width(8.dp))
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f
                    ),
                    onClick = undo,
                    enabled = lastPaths.isNotEmpty() || paths.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Undo,
                        contentDescription = "Undo"
                    )
                }
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f
                    ),
                    onClick = redo,
                    enabled = undonePaths.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Redo,
                        contentDescription = "Redo"
                    )
                }
                RecoverModeButton(
                    selected = isRecoveryOn,
                    enabled = !panEnabled,
                    onClick = { isRecoveryOn = !isRecoveryOn }
                )
            }
        }

        var bitmapState by remember(bitmap, visible) { mutableStateOf(bitmap) }
        var erasedBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }

        var loading by remember { mutableStateOf(false) }

        var autoErased by remember { mutableStateOf(false) }

        FullscreenEditOption(
            canGoBack = paths.isEmpty() && !autoErased,
            visible = visible,
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = { scaffoldState ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!useScaffold) secondaryControls()
                    Spacer(modifier = Modifier.height(8.dp))
                    RecoverModeCard(
                        selected = isRecoveryOn,
                        enabled = !panEnabled,
                        onClick = { isRecoveryOn = !isRecoveryOn }
                    )
                    AutoEraseBackgroundCard(
                        onClick = { modelType ->
                            essentials.launch {
                                scaffoldState?.bottomSheetState?.partialExpand()
                            }
                            loading = true
                            autoBackgroundRemover.removeBackgroundFromImage(
                                image = erasedBitmap,
                                modelType = modelType,
                                onSuccess = {
                                    loading = false
                                    bitmapState = it
                                    clearErasing(false)
                                    autoErased = true
                                    essentials.showConfetti()
                                },
                                onFailure = {
                                    loading = false
                                    essentials.showFailureToast(it)
                                }
                            )
                        },
                        onReset = {
                            bitmapState = bitmap
                            autoErased = true
                        }
                    )
                    OriginalImagePreviewAlphaSelector(
                        value = originalImagePreviewAlpha,
                        onValueChange = {
                            originalImagePreviewAlpha = it
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    )
                    DrawPathModeSelector(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp
                        ),
                        value = drawPathMode,
                        onValueChange = onUpdateDrawPathMode,
                        values = remember {
                            listOf(
                                DrawPathMode.Free,
                                DrawPathMode.FloodFill(),
                                DrawPathMode.Spray(),
                                DrawPathMode.Line,
                                DrawPathMode.Lasso,
                                DrawPathMode.Rect(),
                                DrawPathMode.Oval
                            )
                        }
                    )
                    BoxAnimatedVisibility(drawPathMode.canChangeStrokeWidth) {
                        LineWidthSelector(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                            value = strokeWidth.value,
                            onValueChange = { strokeWidth = it.pt }
                        )
                    }
                    BrushSoftnessSelector(
                        modifier = Modifier
                            .padding(top = 8.dp, end = 16.dp, start = 16.dp),
                        value = brushSoftness.value,
                        onValueChange = { brushSoftness = it.pt }
                    )
                    TrimImageToggle(
                        checked = trimImage,
                        onCheckedChange = { trimImage = it },
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                        )
                    )
                    HelperGridParamsSelector(
                        value = helperGridParams,
                        onValueChange = onUpdateHelperGridParams,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                        )
                    )
                    MagnifierEnabledSelector(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 16.dp
                            ),
                        shape = ShapeDefaults.extraLarge
                    )
                }
            },
            fabButtons = null,
            actions = {
                if (useScaffold) {
                    secondaryControls()
                    Spacer(Modifier.weight(1f))
                }
            },
            topAppBar = { closeButton ->
                EnhancedTopAppBar(
                    type = EnhancedTopAppBarType.Center,
                    navigationIcon = closeButton,
                    actions = {
                        AnimatedVisibility(
                            visible = paths.isNotEmpty() || autoErased,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                onClick = {
                                    essentials.launch {
                                        val trimmed = if (trimImage) {
                                            autoBackgroundRemover.trimEmptyParts(
                                                erasedBitmap
                                            )
                                        } else {
                                            erasedBitmap
                                        }

                                        onGetBitmap(
                                            trimmed,
                                            trimmed.width == erasedBitmap.width && trimmed.height == erasedBitmap.height
                                        )
                                        clearErasing(false)
                                    }
                                    onDismiss()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Done,
                                    contentDescription = "Done"
                                )
                            }
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.erase_background),
                            modifier = Modifier.marquee()
                        )
                    }
                )
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = remember(bitmapState) {
                        derivedStateOf {
                            bitmapState.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                        }
                    }.value,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { imageBitmap ->
                    val direction = LocalLayoutDirection.current
                    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                    BitmapEraser(
                        imageBitmapForShader = bitmap.asImageBitmap(),
                        imageBitmap = imageBitmap,
                        paths = paths,
                        strokeWidth = strokeWidth,
                        brushSoftness = brushSoftness,
                        onAddPath = addPath,
                        isRecoveryOn = isRecoveryOn,
                        modifier = Modifier
                            .padding(
                                start = WindowInsets
                                    .displayCutout
                                    .asPaddingValues()
                                    .calculateStartPadding(direction)
                            )
                            .padding(16.dp)
                            .aspectRatio(aspectRatio, !useScaffold)
                            .fillMaxSize(),
                        panEnabled = panEnabled,
                        drawPathMode = drawPathMode,
                        originalImagePreviewAlpha = originalImagePreviewAlpha,
                        onErased = { erasedBitmap = it },
                        helperGridParams = helperGridParams
                    )
                }

                AnimatedVisibility(
                    visible = loading,
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scrim.copy(0.5f))
                    ) {
                        EnhancedLoadingIndicator()
                    }
                }
            }
        }

        if (visible) {
            DrawLockScreenOrientation()
        }
    }
}