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

package com.t8rin.imagetoolbox.core.ui.widget.image

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.graphics.shapes.Morph
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.Image
import com.t8rin.imagetoolbox.core.resources.shapes.MorphShape
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.utils.animation.springySpec
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.utils.provider.currentScreenTwoToneIcon
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun ImageNotPickedWidget(
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.pick_image),
    isLarge: Boolean = defaultIsLarge(),
    containerColor: Color = Color.Unspecified,
) {
    SourceNotPickedWidget(
        onClick = onPickImage,
        modifier = modifier,
        text = text,
        icon = currentScreenTwoToneIcon(Icons.TwoTone.Image),
        containerColor = containerColor,
        isLarge = isLarge,
    )
}

@Composable
fun FileNotPickedWidget(
    onPickFile: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.pick_file_to_start),
    isLarge: Boolean = defaultIsLarge(),
    containerColor: Color = Color.Unspecified,
) {
    SourceNotPickedWidget(
        onClick = onPickFile,
        modifier = modifier,
        text = text,
        icon = currentScreenTwoToneIcon(Icons.TwoTone.FileOpen),
        containerColor = containerColor,
        isLarge = isLarge,
    )
}

@Composable
fun SourceNotPickedWidget(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
    text: String,
    icon: ImageVector,
    isLarge: Boolean = defaultIsLarge(),
    maxLines: Int = 3,
    containerColor: Color = Color.Unspecified,
) {
    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        contentAlignment = Alignment.Center
    ) {
        val targetSize = min(min(maxWidth, maxHeight), if (isLarge) 360.dp else 300.dp)

        Column(
            modifier = modifier
                .animateContentSizeNoClip()
                .padding(0.5.dp)
                .then(
                    if (isLarge) {
                        val canvasWidth = min(maxWidth, 420.dp)
                        Modifier
                            .width(canvasWidth)
                            .heightIn(min = canvasWidth * 250 / 420)
                            .container(
                                color = containerColor,
                                resultPadding = 24.dp
                            )
                    } else {
                        Modifier.container(color = containerColor)
                    }
                )
                .hapticsClickable(
                    enabled = onClick != null,
                    indication = null,
                    onClick = onClick ?: {},
                    interactionSource = interactionSource
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(if (isLarge) 24.dp else 16.dp))
            ClickableActionIcon(
                icon = icon,
                onClick = onClick,
                modifier = Modifier.size(targetSize / 3),
                interactionSource = interactionSource
            )
            AutoSizeText(
                text = text,
                modifier = if (isLarge) {
                    Modifier
                        .widthIn(max = 240.dp)
                        .padding(vertical = 24.dp)
                } else {
                    Modifier.padding(16.dp)
                },
                style = if (isLarge) MaterialTheme.typography.bodyLarge else LocalTextStyle.current,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                key = { it.length },
                maxLines = maxLines
            )
        }
    }
}

@Composable
fun ClickableActionIcon(
    icon: ImageVector,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val pressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(interactionSource, haptics) {
        interactionSource.interactions.filterIsInstance<PressInteraction>().collectLatest {
            haptics.longPress()
        }
    }

    val shapesType = LocalSettingsState.current.shapesType

    val nonPressedProgress = when (shapesType) {
        is ShapeType.Rounded,
        is ShapeType.Smooth,
        is ShapeType.Squircle -> 0.2f

        else -> 0f
    }
    val pressedScale = when (shapesType) {
        is ShapeType.Rounded,
        is ShapeType.Smooth,
        is ShapeType.Squircle -> 1f

        else -> 1.1f
    }

    val percentage = animateFloatAsState(
        targetValue = if (pressed) 1f else nonPressedProgress,
        animationSpec = springySpec()
    )
    val scale by animateFloatAsState(
        if (pressed) pressedScale
        else 1.1f
    )

    val morph = remember(shapesType) {
        val (start, end) = when (shapesType) {
            is ShapeType.Cut -> MaterialShapes.Gem to MaterialShapes.Diamond
            is ShapeType.Wavy -> MaterialShapes.Cookie12Sided to MaterialShapes.Clover4Leaf
            is ShapeType.Scoop -> MaterialShapes.Clover8Leaf to MaterialShapes.Bun
            is ShapeType.Notch -> MaterialShapes.PixelCircle to ShapeDefaults.polygonSquare

            is ShapeType.Rounded,
            is ShapeType.Smooth,
            is ShapeType.Squircle -> MaterialShapes.Cookie4Sided to MaterialShapes.Square
        }
        Morph(
            start = start,
            end = end
        )
    }
    val shape = remember(morph) {
        MorphShape(
            morph = morph,
            percentage = { percentage.value }
        )
    }

    Box(
        modifier = modifier
            .size(100.dp)
            .scale(scale)
            .container(
                shape = shape,
                resultPadding = 0.dp,
                color = MaterialTheme.colorScheme.mixedContainer.copy(0.8f)
            )
            .then(
                if (onClick != null) {
                    Modifier.hapticsClickable(
                        onClick = onClick,
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        enableHaptics = false
                    )
                } else Modifier
            )
            .scale(1f / scale)
    ) {
        AnimatedContent(
            targetState = icon,
            modifier = Modifier.fillMaxSize()
        ) { imageVector ->
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                tint = MaterialTheme.colorScheme.onMixedContainer
            )
        }
    }
}

@Composable
private fun defaultIsLarge() = LocalScreenSize.current.run {
    minOf(width, height) >= 480.dp
}

@Preview(name = "Phone portrait", widthDp = 360, heightDp = 480, locale = "ru")
@Preview(
    name = "Phone landscape",
    widthDp = 720,
    heightDp = 240,
    locale = "ru",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SourceNotPickedWidgetPreview() = ImageToolboxThemeForPreview(isSystemInDarkTheme()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        ImageNotPickedWidget(
            onPickImage = {},
            isLarge = false
        )
    }
}

@Preview(name = "Tablet portrait", widthDp = 800, heightDp = 1000, locale = "ru")
@Preview(
    name = "Tablet landscape",
    widthDp = 1000,
    heightDp = 700,
    locale = "ru",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SourceNotPickedWidgetLargePreview() =
    ImageToolboxThemeForPreview(isSystemInDarkTheme()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            ImageNotPickedWidget(
                onPickImage = {},
                isLarge = true
            )
        }
    }