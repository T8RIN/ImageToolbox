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

package com.t8rin.imagetoolbox.core.ui.widget.image

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.FitScreen
import com.t8rin.imagetoolbox.core.resources.icons.PushPin
import com.t8rin.imagetoolbox.core.settings.domain.model.SliderType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.snapBackZoomable
import net.engawapg.lib.zoomable.zoomable


fun LazyListScope.imageStickyHeader(
    visible: Boolean,
    isBlocked: Boolean,
    internalHeight: Dp,
    backgroundColor: Color = Color.Unspecified,
    padding: Dp = 20.dp,
    isControlsVisibleIndefinitely: Boolean = false,
    allowStateChanges: Boolean = true,
    onGloballyPositioned: (LayoutCoordinates) -> Unit = {},
    imageModifier: Modifier = Modifier,
    imageBlock: @Composable () -> Unit,
) {
    val content = @Composable {
        val settingsState = LocalSettingsState.current
        val imageState = settingsState.imageHeaderState
        val settingsInteractor = LocalSimpleSettingsInteractor.current
        val scope = rememberCoroutineScope()
        var controlsVisible by rememberSaveable(allowStateChanges) {
            mutableStateOf(allowStateChanges)
        }
        var isHeaderTouched by remember { mutableStateOf(false) }

        LaunchedEffect(allowStateChanges, controlsVisible, isHeaderTouched, imageState) {
            if (allowStateChanges && controlsVisible && !isHeaderTouched) {
                delay(2500)
                controlsVisible = isControlsVisibleIndefinitely
            }
        }

        val screenWidth = LocalScreenSize.current.width
        val density = LocalDensity.current
        Column(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val result =
                        measurable.measure(
                            constraints.copy(
                                maxWidth = with(density) {
                                    screenWidth.roundToPx()
                                }.coerceAtLeast(constraints.minWidth)
                            )
                        )
                    layout(result.measuredWidth, result.measuredHeight) {
                        result.place(0, 0)
                    }
                }
                .then(
                    if (allowStateChanges) {
                        Modifier
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown(requireUnconsumed = false)
                                    isHeaderTouched = true
                                    try {
                                        do {
                                            val event = awaitPointerEvent(PointerEventPass.Final)
                                        } while (event.changes.any { it.pressed })
                                    } finally {
                                        isHeaderTouched = false
                                    }
                                }
                            }
                            .tappable(!isControlsVisibleIndefinitely) {
                                controlsVisible = true
                            }
                    } else Modifier
                )
                .onGloballyPositioned(onGloballyPositioned)
                .animateContentSizeNoClip()
        ) {
            val color = if (backgroundColor.isSpecified) {
                backgroundColor
            } else MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (internalHeight.isSpecified) Modifier.height(internalHeight)
                        else Modifier
                    )
                    .fadingEdges(
                        scrollableState = null,
                        isVertical = true,
                        length = animateDpAsState(
                            if (allowStateChanges && imageState.position == 4) 0.dp
                            else 16.dp
                        ).value,
                        gravity = FadingEdgesGravity.End
                    )
                    .background(color)
                    .padding(
                        start = padding,
                        end = padding,
                        top = padding,
                        bottom = animateDpAsState(
                            if (allowStateChanges && controlsVisible) padding / 2
                            else padding
                        ).value
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val zoomModifier = if (allowStateChanges) {
                    val zoomState = rememberZoomState()
                    var previousPosition by remember { mutableIntStateOf(imageState.position) }
                    LaunchedEffect(imageState.position, imageState.isZoomResetEnabled) {
                        val shouldReset =
                            imageState.isZoomResetEnabled || previousPosition != imageState.position
                        previousPosition = imageState.position
                        if (shouldReset && (zoomState.scale != 1f || zoomState.offsetX != 0f || zoomState.offsetY != 0f)) {
                            zoomState.changeScale(
                                targetScale = 1f,
                                position = Offset.Zero
                            )
                        }
                    }
                    Modifier
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false).consume()
                            }
                        }
                        .then(
                            if (imageState.isZoomResetEnabled) {
                                Modifier.snapBackZoomable(
                                    zoomState = zoomState,
                                    onTap = { controlsVisible = true }
                                )
                            } else {
                                Modifier.zoomable(
                                    zoomState = zoomState,
                                    onTap = { controlsVisible = true }
                                )
                            }
                        )
                } else Modifier

                Box(
                    modifier = Modifier
                        .then(
                            if (internalHeight.isSpecified) Modifier.weight(1f, false)
                            else Modifier
                        )
                        .then(imageModifier)
                        .then(zoomModifier)
                ) {
                    imageBlock()
                }
                BoxAnimatedVisibility(
                    visible = allowStateChanges && controlsVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EnhancedSlider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            value = imageState.position.toFloat(),
                            onValueChange = {
                                controlsVisible = true
                                scope.launch {
                                    settingsInteractor.setImageHeaderState(
                                        imageState.copy(position = it.toInt())
                                    )
                                }
                            },
                            colors = SliderDefaults.colors(
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant(
                                    onTopOf = MaterialTheme.colorScheme.tertiaryContainer
                                ).copy(0.5f),
                                activeTrackColor = MaterialTheme.colorScheme.tertiary.copy(0.5f),
                                thumbColor = if (settingsState.sliderType == SliderType.Fancy) {
                                    MaterialTheme.colorScheme.onTertiary
                                } else MaterialTheme.colorScheme.tertiary,
                                activeTickColor = MaterialTheme.colorScheme.tertiaryContainer,
                                inactiveTickColor = MaterialTheme.colorScheme.tertiary.copy(0.5f)
                            ),
                            steps = 3,
                            valueRange = 0f..4f
                        )
                        EnhancedIconButton(
                            onClick = {
                                controlsVisible = true
                                scope.launch {
                                    settingsInteractor.setImageHeaderState(
                                        imageState.copy(isBlocked = !imageState.isBlocked)
                                    )
                                }
                            },
                            containerColor = takeColorFromScheme {
                                if (imageState.isBlocked) {
                                    tertiary.copy(0.8f)
                                } else {
                                    surfaceVariant.copy(0.5f)
                                }
                            },
                            contentColor = takeColorFromScheme {
                                if (imageState.isBlocked) {
                                    onTertiary
                                } else {
                                    onSurfaceVariant
                                }
                            }
                        ) {
                            AnimatedContent(targetState = imageState.isBlocked) { blocked ->
                                if (blocked) {
                                    Icon(
                                        imageVector = Icons.Rounded.PushPin,
                                        contentDescription = "Pin Image"
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.PushPin,
                                        contentDescription = "Unpin Image"
                                    )
                                }
                            }
                        }
                        EnhancedIconButton(
                            onClick = {
                                controlsVisible = true
                                scope.launch {
                                    settingsInteractor.setImageHeaderState(
                                        imageState.copy(isZoomResetEnabled = !imageState.isZoomResetEnabled)
                                    )
                                }
                            },
                            containerColor = takeColorFromScheme {
                                if (imageState.isZoomResetEnabled) {
                                    secondary.copy(0.8f)
                                } else {
                                    surfaceVariant.copy(0.5f)
                                }
                            },
                            contentColor = takeColorFromScheme {
                                if (imageState.isZoomResetEnabled) {
                                    onSecondary
                                } else {
                                    onSurfaceVariant
                                }
                            }
                        ) {
                            AnimatedContent(targetState = imageState.isZoomResetEnabled) { resetEnabled ->
                                if (resetEnabled) {
                                    Icon(
                                        imageVector = Icons.Rounded.FitScreen,
                                        contentDescription = "reset zoom after touch"
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.FitScreen,
                                        contentDescription = "keep zoom after touch"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (!isBlocked) {
        item(
            key = "stickyHeader",
            contentType = "stickyHeader"
        ) {
            AnimatedContent(
                targetState = visible,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (it) content()
                else Spacer(Modifier)
            }
        }
    } else {
        stickyHeader(
            key = "stickyHeader",
            contentType = "stickyHeader"
        ) {
            AnimatedContent(
                targetState = visible,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (it) content()
                else Spacer(Modifier)
            }
        }
    }
}