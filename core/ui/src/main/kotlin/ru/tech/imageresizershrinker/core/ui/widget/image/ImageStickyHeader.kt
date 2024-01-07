package ru.tech.imageresizershrinker.core.ui.widget.image

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.modifier.materialShadow
import ru.tech.imageresizershrinker.core.ui.widget.other.GradientEdge
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun LazyListScope.imageStickyHeader(
    visible: Boolean,
    imageState: ImageHeaderState,
    internalHeight: Dp,
    onStateChange: (ImageHeaderState) -> Unit,
    backgroundColor: Color = Color.Unspecified,
    padding: Dp = 20.dp,
    imageModifier: Modifier = Modifier,
    imageBlock: @Composable () -> Unit,
) {
    val content = @Composable {
        var controlsVisible by remember {
            mutableStateOf(true)
        }
        LaunchedEffect(controlsVisible) {
            if (controlsVisible) {
                delay(3000)
                controlsVisible = false
            }
        }
        val sliderInteractionSource = remember {
            MutableInteractionSource()
        }
        val isFocused by sliderInteractionSource.collectIsFocusedAsState()
        LaunchedEffect(isFocused) {
            while (isFocused) {
                delay(100)
                controlsVisible = true
            }
        }

        val haptics = LocalHapticFeedback.current
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
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
                .pointerInput(Unit) {
                    detectTapGestures {
                        controlsVisible = true
                    }
                }
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
                    .background(color)
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f, false)
                        .then(imageModifier)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                controlsVisible = true
                            }
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, _, _ ->
                                controlsVisible = true
                            }
                        }
                ) {
                    imageBlock()
                }
                Spacer(
                    Modifier.height(
                        animateDpAsState(
                            if (controlsVisible) 36.dp else 0.dp
                        ).value
                    )
                )
            }
            Box {
                GradientEdge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    startColor = color,
                    endColor = Color.Transparent
                )
                androidx.compose.animation.AnimatedVisibility(
                    visible = controlsVisible,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-48).dp),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.7f),
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
                                activeTrackColor = MaterialTheme.colorScheme.tertiary.copy(0.5f)
                            ),
                            thumbColor = MaterialTheme.colorScheme.onTertiary,
                            steps = 3,
                            valueRange = 0f..4f
                        )
                        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
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
                                        Icon(Icons.Rounded.Lock, null)
                                    } else {
                                        Icon(Icons.Rounded.LockOpen, null)
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