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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.StackSticky
import com.t8rin.imagetoolbox.core.resources.icons.StackStickyOff
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.uiCornerRadiusPercent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent
import com.t8rin.modalsheet.FullscreenPopup

@Composable
internal fun MarkupLayersSideMenu(
    component: MarkupLayersComponent,
    visible: Boolean,
    onDismiss: () -> Unit,
    isContextOptionsVisible: Boolean,
    onContextOptionsVisibleChange: (Boolean) -> Unit
) {
    val layers = component.layers

    FullscreenPopup {
        var parentSize by remember {
            mutableStateOf(IntSize.Zero)
        }
        var sideMenuSize by remember {
            mutableStateOf(IntSize.Zero)
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
            modifier = Modifier.fillMaxSize(),
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        WindowInsets.systemBars
                            .union(WindowInsets.displayCutout)
                            .asPaddingValues()
                    )
                    .onSizeChanged {
                        parentSize = it
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                if (visible) {
                    BackHandler(onBack = onDismiss)

                    Box(
                        Modifier
                            .fillMaxSize()
                            .tappable { onDismiss() }
                    )
                }

                val maxHeightFull = this.maxHeight
                val sideMenuScale by animateFloatAsState(component.sideMenuScale)
                val sideMenuAlpha by animateFloatAsState(component.sideMenuAlpha)

                LaunchedEffect(parentSize, sideMenuSize, sideMenuScale) {
                    component.updateSideMenuTranslation(
                        coerceSideMenuTranslation(
                            currentTranslation = Offset(
                                component.sideMenuTranslationX,
                                component.sideMenuTranslationY
                            ),
                            dragAmount = Offset.Zero,
                            parentSize = parentSize,
                            menuSize = sideMenuSize,
                            scale = sideMenuScale
                        )
                    )
                }

                Surface(
                    color = Color.Transparent,
                    modifier = Modifier
                        .onSizeChanged {
                            sideMenuSize = it
                        }
                        .graphicsLayer {
                            scaleX = sideMenuScale
                            scaleY = sideMenuScale
                            translationX = component.sideMenuTranslationX
                            translationY = component.sideMenuTranslationY
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .height(
                                minOf(maxHeightFull, 480.dp)
                            )
                            .width(168.dp)
                            .container(
                                color = MaterialTheme.colorScheme.surfaceContainer.copy(
                                    alpha = sideMenuAlpha
                                ),
                                composeColorOnTopOfBackground = false,
                                resultPadding = 0.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val activeLayer by remember(layers) {
                            derivedStateOf {
                                layers.find { it.state.isActive }
                            }
                        }
                        val normalizedPosition by remember(activeLayer) {
                            derivedStateOf {
                                activeLayer?.let { layer ->
                                    layer.state.normalizedPosition(
                                        cornerRadiusPercent = layer.uiCornerRadiusPercent()
                                    )
                                }
                            }
                        }
                        val scale by remember(activeLayer) {
                            derivedStateOf {
                                activeLayer?.state?.scale?.roundTo(3)
                            }
                        }
                        val normalizedRotationDegrees by remember(activeLayer) {
                            derivedStateOf {
                                activeLayer?.state?.rotation
                                    ?.normalizeForUi()
                                    ?.roundTo(1)
                            }
                        }
                        Scaffold(
                            topBar = {
                                Column(
                                    modifier = Modifier
                                        .container(
                                            shape = RectangleShape,
                                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                                                alpha = sideMenuAlpha * 0.9f
                                            ),
                                            composeColorOnTopOfBackground = false
                                        )
                                        .pointerInput(parentSize, sideMenuSize, sideMenuScale) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()

                                                component.updateSideMenuTranslation(
                                                    coerceSideMenuTranslation(
                                                        currentTranslation = Offset(
                                                            component.sideMenuTranslationX,
                                                            component.sideMenuTranslationY
                                                        ),
                                                        dragAmount = dragAmount,
                                                        parentSize = parentSize,
                                                        menuSize = sideMenuSize,
                                                        scale = sideMenuScale
                                                    )
                                                )
                                            }
                                        }
                                ) {
                                    val showContextActions =
                                        isContextOptionsVisible && (activeLayer != null || component.isGroupingSelectionMode)

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        EnhancedIconButton(
                                            onClick = {
                                                activeLayer?.let(component::removeLayer)
                                            },
                                            enabled = activeLayer != null && !component.isGroupingSelectionMode && !showContextActions
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Delete,
                                                contentDescription = null
                                            )
                                        }

                                        Spacer(Modifier.weight(1f))

                                        AnimatedContent(
                                            targetState = (component.groupingSelectionCount >= 2) to (activeLayer?.isGroup == true && !component.isGroupingSelectionMode)
                                        ) { (canGroupLayers, canUngroupLayer) ->
                                            if (canGroupLayers) {
                                                EnhancedIconButton(
                                                    onClick = component::groupSelectedLayers,
                                                    enabled = !showContextActions
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.StackSticky,
                                                        contentDescription = null
                                                    )
                                                }
                                            } else if (canUngroupLayer) {
                                                EnhancedIconButton(
                                                    onClick = {
                                                        activeLayer?.let(component::ungroupLayer)
                                                    },
                                                    enabled = !showContextActions
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.StackStickyOff,
                                                        contentDescription = null
                                                    )
                                                }
                                            } else {
                                                Spacer(Modifier.height(48.dp))
                                            }
                                        }
                                        Box {
                                            EnhancedIconButton(
                                                onClick = {
                                                    onContextOptionsVisibleChange(true)
                                                },
                                                enabled = activeLayer != null || component.isGroupingSelectionMode
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Build,
                                                    contentDescription = null
                                                )
                                            }
                                            MarkupLayersContextActions(
                                                visible = showContextActions,
                                                onDismiss = { onContextOptionsVisibleChange(false) },
                                                onCopyLayer = {
                                                    activeLayer?.let(component::copyLayer)
                                                },
                                                onToggleEditMode = {
                                                    activeLayer
                                                        ?.takeIf { !it.isLocked }
                                                        ?.state
                                                        ?.isInEditMode = true
                                                },
                                                onRemoveLayer = {
                                                    activeLayer?.let(component::removeLayer)
                                                },
                                                onActivateLayer = {
                                                    component.clearSelections()
                                                },
                                                isLayerLocked = activeLayer?.isLocked == true,
                                                onToggleLayerLock = {
                                                    activeLayer?.let(component::toggleLayerLock)
                                                },
                                                isGroupingSelectionMode = component.isGroupingSelectionMode,
                                                groupingSelectionCount = component.groupingSelectionCount,
                                                onFlipLayerHorizontally = {
                                                    activeLayer?.let { layer ->
                                                        component.updateLayerState(layer) {
                                                            isFlippedHorizontally =
                                                                !isFlippedHorizontally
                                                        }
                                                    }
                                                },
                                                onFlipLayerVertically = {
                                                    activeLayer?.let { layer ->
                                                        component.updateLayerState(layer) {
                                                            isFlippedVertically =
                                                                !isFlippedVertically
                                                        }
                                                    }
                                                },
                                                onAlignLayer = { x, y ->
                                                    activeLayer?.let { layer ->
                                                        component.setLayerNormalizedPosition(
                                                            layer = layer,
                                                            x = x,
                                                            y = y
                                                        )
                                                    }
                                                },
                                                onMoveLayerBy = { dx, dy ->
                                                    activeLayer?.let { layer ->
                                                        component.moveLayerBy(
                                                            layer = layer,
                                                            offsetChange = Offset(dx, dy)
                                                        )
                                                    }
                                                },
                                                onResetLayerPosition = {
                                                    activeLayer?.let(component::resetLayerPosition)
                                                },
                                                onNormalizedPositionXChange = { x ->
                                                    activeLayer?.let { layer ->
                                                        component.setLayerNormalizedPosition(
                                                            layer = layer,
                                                            x = x
                                                        )
                                                    }
                                                },
                                                onNormalizedPositionYChange = { y ->
                                                    activeLayer?.let { layer ->
                                                        component.setLayerNormalizedPosition(
                                                            layer = layer,
                                                            y = y
                                                        )
                                                    }
                                                },
                                                normalizedPositionX = normalizedPosition?.x,
                                                normalizedPositionY = normalizedPosition?.y,
                                                scale = scale,
                                                onScaleChange = {
                                                    component.beginHistoryTransaction()
                                                    activeLayer?.let { layer ->
                                                        component.setLayerScale(
                                                            layer = layer,
                                                            scale = it,
                                                            commitToHistory = false
                                                        )
                                                    }
                                                },
                                                onScaleChangeFinished = {
                                                    component.commitHistoryTransaction()
                                                },
                                                rotationDegrees = normalizedRotationDegrees,
                                                onRotationDegreesChange = {
                                                    component.beginHistoryTransaction()
                                                    activeLayer?.let { layer ->
                                                        component.updateLayerState(
                                                            layer = layer,
                                                            commitToHistory = false
                                                        ) {
                                                            rotation = it.roundTo(1)
                                                        }
                                                    }
                                                },
                                                onRotationDegreesChangeFinished = {
                                                    component.commitHistoryTransaction()
                                                }
                                            )
                                        }
                                    }
                                    EnhancedSlider(
                                        value = activeLayer?.state?.alpha ?: 1f,
                                        enabled = activeLayer != null &&
                                                activeLayer?.isLocked != true &&
                                                !component.isGroupingSelectionMode,
                                        onValueChange = {
                                            component.beginHistoryTransaction()
                                            activeLayer?.let { layer ->
                                                component.updateLayerState(
                                                    layer = layer,
                                                    commitToHistory = false
                                                ) {
                                                    alpha = it
                                                }
                                            }
                                        },
                                        onValueChangeFinished = component::commitHistoryTransaction,
                                        valueRange = 0f..1f,
                                        drawContainer = false,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            },
                            containerColor = Color.Transparent,
                            contentWindowInsets = WindowInsets(0)
                        ) { contentPadding ->
                            MarkupLayersSideMenuColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = contentPadding,
                                layers = layers,
                                onReorderLayers = component::reorderLayers,
                                onActivateLayer = component::activateLayer,
                                isGroupingSelectionMode = component.isGroupingSelectionMode,
                                groupingSelectionIds = component.groupingSelectionIds,
                                onStartGroupingSelection = component::startGroupingSelection,
                                onToggleGroupingSelection = component::toggleGroupingSelection,
                                onToggleLayerVisibility = { layer ->
                                    component.updateLayerState(
                                        layer = layer,
                                        allowLocked = true
                                    ) {
                                        isVisible = !isVisible
                                    }
                                },
                                onUnlockLayer = component::toggleLayerLock
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun coerceSideMenuTranslation(
    currentTranslation: Offset,
    dragAmount: Offset,
    parentSize: IntSize,
    menuSize: IntSize,
    scale: Float
): Offset {
    if (
        parentSize.width <= 0 ||
        parentSize.height <= 0 ||
        menuSize.width <= 0 ||
        menuSize.height <= 0
    ) {
        return currentTranslation + dragAmount * scale
    }

    val parentWidth = parentSize.width.toFloat()
    val parentHeight = parentSize.height.toFloat()

    val menuWidth = menuSize.width.toFloat()
    val menuHeight = menuSize.height.toFloat()

    val scaledHalfWidth = menuWidth * scale / 2f
    val scaledHalfHeight = menuHeight * scale / 2f

    val initialCenterX = parentWidth - menuWidth / 2f
    val initialCenterY = parentHeight / 2f

    val maxCenterX = parentWidth - scaledHalfWidth

    val maxCenterY = parentHeight - scaledHalfHeight

    val targetTranslation = currentTranslation + dragAmount * scale

    val targetCenterX = initialCenterX + targetTranslation.x
    val targetCenterY = initialCenterY + targetTranslation.y

    val coercedCenterX = targetCenterX.coerceInSafe(scaledHalfWidth, maxCenterX)
    val coercedCenterY = targetCenterY.coerceInSafe(scaledHalfHeight, maxCenterY)

    return Offset(
        x = coercedCenterX - initialCenterX,
        y = coercedCenterY - initialCenterY
    )
}

private fun Float.coerceInSafe(
    minimumValue: Float,
    maximumValue: Float
): Float {
    return if (minimumValue <= maximumValue) {
        coerceIn(minimumValue, maximumValue)
    } else {
        (minimumValue + maximumValue) / 2f
    }
}

private fun Float.normalizeForUi(): Float {
    val normalized = this % 360f

    return when {
        normalized < 0f -> normalized + 360f
        normalized == 0f && this > 0f -> 360f
        else -> normalized
    }
}
