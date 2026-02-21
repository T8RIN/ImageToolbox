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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.PredictiveBackObserver
import com.t8rin.imagetoolbox.core.ui.widget.modifier.alertDialogBorder
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.delay

@Composable
fun EnhancedAlertDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    placeAboveAll: Boolean = false,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation
) {
    BasicEnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        placeAboveAll = placeAboveAll,
        content = {
            val isCenterAlignButtons = LocalSettingsState.current.isCenterAlignDialogButtons

            EnhancedAlertDialogContent(
                buttons = {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = ButtonsHorizontalSpacing,
                            alignment = if (dismissButton != null && isCenterAlignButtons) {
                                Alignment.CenterHorizontally
                            } else Alignment.End
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            space = ButtonsVerticalSpacing,
                            alignment = if (dismissButton != null && isCenterAlignButtons) {
                                Alignment.CenterVertically
                            } else Alignment.Bottom
                        ),
                        itemVerticalAlignment = Alignment.CenterVertically
                    ) {
                        dismissButton?.invoke()
                        confirmButton()
                    }
                },
                icon = icon,
                title = title,
                text = text,
                shape = shape,
                containerColor = containerColor,
                tonalElevation = tonalElevation,
                // Note that a button content color is provided here from the dialog's token, but in
                // most cases, TextButtons should be used for dismiss and confirm buttons.
                // TextButtons will not consume this provided content color value, and will used their
                // own defined or default colors.
                buttonContentColor = MaterialTheme.colorScheme.primary,
                iconContentColor = iconContentColor,
                titleContentColor = titleContentColor,
                textContentColor = textContentColor,
                modifier = modifier
                    .alertDialogBorder()
                    .sizeIn(
                        minWidth = DialogMinWidth,
                        maxWidth = DialogMaxWidth
                    )
                    .then(Modifier.semantics { paneTitle = "Dialog" })
            )
        }
    )
}

@Composable
fun BasicEnhancedAlertDialog(
    visible: Boolean,
    onDismissRequest: (() -> Unit)?,
    modifier: Modifier = Modifier,
    placeAboveAll: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    var visibleAnimated by remember { mutableStateOf(false) }

    var scale by remember {
        mutableFloatStateOf(1f)
    }
    val animatedScale by animateFloatAsState(scale)

    LaunchedEffect(visible) {
        if (visible) {
            scale = 1f
            visibleAnimated = true
        }
    }

    if (visibleAnimated) {
        FullscreenPopupForPreview(placeAboveAll = placeAboveAll) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                var animateIn by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect(Unit) { animateIn = true }
                AnimatedVisibility(
                    visible = animateIn && visible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    val alpha = 0.5f * animatedScale

                    Box(
                        modifier = Modifier
                            .tappable { onDismissRequest?.invoke() }
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = alpha))
                            .fillMaxSize()
                    )
                }
                AnimatedVisibility(
                    visible = animateIn && visible,
                    enter = fadeIn(tween(300)) + scaleIn(
                        initialScale = .8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    exit = fadeOut(tween(300)) + scaleOut(
                        targetScale = .8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    modifier = Modifier.scale(animatedScale)
                ) {
                    Box(
                        modifier = modifier
                            .safeDrawingPadding()
                            .padding(horizontal = 48.dp, vertical = 24.dp),
                        contentAlignment = Alignment.Center,
                        content = content
                    )
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    visibleAnimated = false
                }
            }

            if (onDismissRequest != null) {
                PredictiveBackObserver(
                    onProgress = { progress ->
                        scale = (1f - progress / 6f).coerceAtLeast(0.85f)
                    },
                    onClean = { isCompleted ->
                        if (isCompleted) {
                            onDismissRequest()
                            delay(400)
                        }
                        scale = 1f
                    },
                    enabled = visible
                )
            }
        }
    }
}

@Composable
private fun EnhancedAlertDialogContent(
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)?,
    title: (@Composable () -> Unit)?,
    text: @Composable (() -> Unit)?,
    shape: Shape,
    containerColor: Color,
    tonalElevation: Dp,
    buttonContentColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
    ) {
        Column(modifier = Modifier.padding(DialogPadding)) {
            icon?.let {
                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                    Box(
                        Modifier
                            .padding(IconPadding)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        icon()
                    }
                }
            }
            title?.let {
                ProvideContentColorTextStyle(
                    contentColor = titleContentColor,
                    textStyle = MaterialTheme.typography.headlineSmall
                ) {
                    Box(
                        // Align the title to the center when an icon is present.
                        Modifier
                            .padding(TitlePadding)
                            .align(
                                if (icon == null) {
                                    Alignment.Start
                                } else {
                                    Alignment.CenterHorizontally
                                }
                            )
                    ) {
                        title()
                    }
                }
            }
            text?.let {
                val textStyle = MaterialTheme.typography.bodyMedium
                ProvideContentColorTextStyle(
                    contentColor = textContentColor,
                    textStyle = textStyle
                ) {
                    Box(
                        Modifier
                            .weight(weight = 1f, fill = false)
                            .padding(TextPadding)
                            .align(Alignment.Start)
                    ) {
                        text()
                    }
                }
            }
            Box(modifier = Modifier.align(Alignment.End)) {
                val textStyle = MaterialTheme.typography.labelLarge
                ProvideContentColorTextStyle(
                    contentColor = buttonContentColor,
                    textStyle = textStyle,
                    content = buttons
                )
            }
        }
    }
}

@Composable
fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content
    )
}

private val DialogMinWidth = 280.dp
private val DialogMaxWidth = 480.dp

private val ButtonsHorizontalSpacing = 8.dp
private val ButtonsVerticalSpacing = 12.dp

// Paddings for each of the dialog's parts.
private val DialogPadding = PaddingValues(all = 24.dp)
private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)


@Composable
private fun FullscreenPopupForPreview(
    onDismiss: (() -> Unit)? = null,
    placeAboveAll: Boolean = false,
    content: @Composable () -> Unit
) {
    if (LocalInspectionMode.current) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { onDismiss?.invoke() }
        ) {
            content()
        }
    } else {
        FullscreenPopup(
            onDismiss = onDismiss,
            placeAboveAll = placeAboveAll,
            content = content
        )
    }
}