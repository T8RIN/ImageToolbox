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

package com.t8rin.imagetoolbox.core.ui.widget.preferences

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeContainer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction

@Composable
fun PreferenceRow(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    containerColor: Color = Color.Unspecified,
    enabled: Boolean = true,
    shape: Shape = ShapeDefaults.default,
    pressedShape: Shape = ShapeDefaults.pressed,
    contentColor: Color? = null,
    applyHorizontalPadding: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    startContent: (@Composable () -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    titleFontStyle: TextStyle = PreferenceItemDefaults.TitleFontStyle,
    resultModifier: Modifier = Modifier.padding(
        horizontal = if (startContent != null) 0.dp else 16.dp,
        vertical = 8.dp
    ),
    changeAlphaWhenDisabled: Boolean = true,
    drawStartIconContainer: Boolean = false,
    onClick: (() -> Unit)?,
    onDisabledClick: (() -> Unit)? = null,
    autoShadowElevation: Dp = 1.dp,
    additionalContent: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    drawContainer: Boolean = true,
) {
    val animatedShape = shapeByInteraction(
        shape = shape,
        pressedShape = pressedShape,
        interactionSource = interactionSource
    )

    val internalColor = contentColor
        ?: contentColorFor(backgroundColor = containerColor)
    CompositionLocalProvider(
        LocalContentColor provides internalColor,
        LocalSettingsState provides LocalSettingsState.current.let {
            if (!enabled) it.copy(
                drawButtonShadows = false,
                drawContainerShadows = false,
                drawFabShadows = false,
                drawSwitchShadows = false,
                drawSliderShadows = false
            ) else it
        }
    ) {
        val rowModifier = modifier
            .then(
                if (applyHorizontalPadding) {
                    Modifier.padding(horizontal = 16.dp)
                } else Modifier
            )
            .then(
                if (drawContainer) {
                    Modifier.container(
                        color = containerColor,
                        shape = animatedShape,
                        resultPadding = 0.dp,
                        autoShadowElevation = autoShadowElevation
                    )
                } else Modifier
            )
            .then(
                onClick
                    ?.let {
                        if (enabled) {
                            Modifier.hapticsClickable(
                                interactionSource = interactionSource,
                                indication = LocalIndication.current,
                                onClick = onClick
                            )
                        } else Modifier.then(
                            if (onDisabledClick != null) {
                                Modifier.hapticsClickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current,
                                    onClick = onDisabledClick
                                )
                            } else Modifier
                        )
                    } ?: Modifier
            )
            .then(resultModifier)
            .then(
                if (changeAlphaWhenDisabled) Modifier.alpha(animateFloatAsState(targetValue = if (enabled) 1f else 0.5f).value)
                else Modifier
            )

        val rowContent: @Composable (Modifier) -> Unit = { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                startContent?.let { content ->
                    ProvideContainerDefaults(
                        color = containerColor
                    ) {
                        if (drawStartIconContainer) {
                            IconShapeContainer(
                                enabled = true,
                                content = {
                                    content()
                                },
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        } else content()
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = title,
                        transitionSpec = {
                            fadeIn().togetherWith(fadeOut())
                        }
                    ) {
                        Text(
                            text = it,
                            maxLines = maxLines,
                            style = titleFontStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    AnimatedContent(
                        targetState = subtitle,
                        transitionSpec = {
                            fadeIn().togetherWith(fadeOut())
                        }
                    ) {
                        it?.let {
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 14.sp,
                                color = LocalContentColor.current.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                ProvideContainerDefaults(null) {
                    endContent?.invoke()
                }
            }
        }

        if (additionalContent != null) {
            Column(rowModifier) {
                rowContent(Modifier)
                additionalContent()
            }
        } else {
            rowContent(rowModifier)
        }
    }
}


@Composable
fun PreferenceRow(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    subtitle: String? = null,
    autoShadowElevation: Dp = 1.dp,
    color: Color = Color.Unspecified,
    drawStartIconContainer: Boolean = true,
    onDisabledClick: (() -> Unit)? = null,
    changeAlphaWhenDisabled: Boolean = true,
    contentColor: Color? = null,
    shape: Shape = ShapeDefaults.default,
    titleFontStyle: TextStyle = PreferenceItemDefaults.TitleFontStyle,
    startIcon: ImageVector?,
    endContent: (@Composable () -> Unit)? = null,
    additionalContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)?,
) {
    PreferenceRow(
        modifier = modifier,
        title = title,
        enabled = enabled,
        subtitle = subtitle,
        changeAlphaWhenDisabled = changeAlphaWhenDisabled,
        autoShadowElevation = autoShadowElevation,
        containerColor = color,
        contentColor = contentColor,
        shape = shape,
        titleFontStyle = titleFontStyle,
        onDisabledClick = onDisabledClick,
        drawStartIconContainer = false,
        startContent = startIcon?.let {
            {
                IconShapeContainer(
                    enabled = drawStartIconContainer,
                    content = {
                        AnimatedContent(startIcon) { startIcon ->
                            Icon(
                                imageVector = startIcon,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        endContent = endContent,
        resultModifier = if (endContent != null) {
            Modifier.padding(
                top = 8.dp,
                start = 16.dp,
                bottom = 8.dp
            )
        } else Modifier.padding(16.dp),
        applyHorizontalPadding = false,
        onClick = onClick,
        additionalContent = additionalContent
    )
}