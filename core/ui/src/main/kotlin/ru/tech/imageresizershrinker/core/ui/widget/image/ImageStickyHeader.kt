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

package ru.tech.imageresizershrinker.core.ui.widget.image

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gigamole.composefadingedges.FadingEdgesGravity
import com.smarttoolfactory.gesture.PointerRequisite
import com.smarttoolfactory.gesture.detectPointerTransformGestures
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.settings.domain.model.SliderType
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ProvidesValue
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalScreenSize
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.modifier.materialShadow
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.imageStickyHeader(
    visible: Boolean,
    imageState: ImageHeaderState,
    internalHeight: Dp,
    onStateChange: (ImageHeaderState) -> Unit,
    backgroundColor: Color = Color.Unspecified,
    padding: Dp = 20.dp,
    isControlsVisibleIndefinitely: Boolean = false,
    onGloballyPositioned: (LayoutCoordinates) -> Unit = {},
    imageModifier: Modifier = Modifier,
    imageBlock: @Composable () -> Unit,
) {
    val content = @Composable {
        var controlsVisible by rememberSaveable {
            mutableStateOf(true)
        }
        val sliderInteractionSource = remember {
            MutableInteractionSource()
        }
        val interactions by sliderInteractionSource.interactions.collectAsState(initial = null)

        LaunchedEffect(controlsVisible, interactions) {
            if (controlsVisible && !(interactions is DragInteraction.Start || interactions is PressInteraction.Press)) {
                delay(2500)
                controlsVisible = isControlsVisibleIndefinitely
            }
        }

        val haptics = LocalHapticFeedback.current
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
                                }
                            )
                        )
                    layout(result.measuredWidth, result.measuredHeight) {
                        result.place(0, 0)
                    }
                }
                .then(
                    if (!isControlsVisibleIndefinitely) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures {
                                controlsVisible = true
                            }
                        }
                    } else Modifier
                )
                .onGloballyPositioned(onGloballyPositioned)
                .animateContentSize()
        ) {
            val color = if (backgroundColor.isSpecified) {
                backgroundColor
            } else MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)

            val settingsState = LocalSettingsState.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(internalHeight)
                    .fadingEdges(
                        scrollableState = null,
                        isVertical = true,
                        length = animateDpAsState(
                            if (imageState.position == 4) 0.dp
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
                            if (controlsVisible) padding / 2
                            else padding
                        ).value
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f, false)
                        .then(imageModifier)
                        .then(
                            if (!isControlsVisibleIndefinitely) {
                                Modifier.pointerInput(Unit) {
                                    var touchPointerOffset = Offset.Zero
                                    detectPointerTransformGestures(
                                        consume = false,
                                        onGestureEnd = {
                                            val diff = touchPointerOffset - it.position
                                            if (abs(diff.x) < 10f && abs(diff.y) < 10f) {
                                                controlsVisible = true
                                                it.consume()
                                            }
                                        },
                                        requisite = PointerRequisite.EqualTo,
                                        onGestureStart = {
                                            touchPointerOffset = it.position
                                        },
                                        onGesture = { _, _, _, _, _, _ -> }
                                    )
                                }
                            } else Modifier
                        )
                ) {
                    imageBlock()
                }
                BoxAnimatedVisibility(
                    visible = controlsVisible,
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
                            interactionSource = sliderInteractionSource,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            value = imageState.position.toFloat(),
                            onValueChange = {
                                controlsVisible = true
                                onStateChange(imageState.copy(position = it.toInt()))
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
                        LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
                            OutlinedIconToggleButton(
                                checked = imageState.isBlocked,
                                onCheckedChange = {
                                    controlsVisible = true
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    onStateChange(imageState.copy(isBlocked = it))
                                },
                                modifier = Modifier.materialShadow(
                                    shape = CircleShape,
                                    elevation = if (settingsState.borderWidth > 0.dp) 0.dp else 0.5.dp,
                                    isClipped = true
                                ),
                                border = BorderStroke(
                                    width = settingsState.borderWidth,
                                    color = MaterialTheme.colorScheme
                                        .outlineVariant()
                                        .copy(alpha = 0.3f)
                                ),
                                colors = IconButtonDefaults.filledTonalIconToggleButtonColors(
                                    checkedContainerColor = MaterialTheme.colorScheme.tertiary.copy(
                                        0.8f
                                    ),
                                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        0.5f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                AnimatedContent(targetState = imageState.isBlocked) { blocked ->
                                    if (blocked) {
                                        Icon(
                                            imageVector = Icons.Rounded.Lock,
                                            contentDescription = "Lock Image"
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Rounded.LockOpen,
                                            contentDescription = "Unlock Image"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (visible) {
        if (!imageState.isBlocked) {
            item(
                key = "stickyHeader",
                contentType = "stickyHeader"
            ) {
                content()
            }
        } else {
            stickyHeader(
                key = "stickyHeader",
                contentType = "stickyHeader"
            ) {
                content()
            }
        }
    }
}
