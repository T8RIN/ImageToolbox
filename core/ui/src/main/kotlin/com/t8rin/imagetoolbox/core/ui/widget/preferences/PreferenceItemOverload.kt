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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeContainer
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction


@Composable
fun PreferenceItemOverload(
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    title: String,
    enabled: Boolean = true,
    subtitle: String? = null,
    autoShadowElevation: Dp = 1.dp,
    startIcon: (@Composable () -> Unit)? = null,
    endIcon: (@Composable () -> Unit)? = null,
    badge: (@Composable RowScope.() -> Unit)? = null,
    shape: Shape = ShapeDefaults.default,
    pressedShape: Shape = ShapeDefaults.pressed,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = contentColorFor(backgroundColor = containerColor),
    overrideIconShapeContentColor: Boolean = false,
    resultModifier: Modifier = Modifier.padding(16.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
    titleFontStyle: TextStyle = PreferenceItemDefaults.TitleFontStyle,
    onDisabledClick: (() -> Unit)? = null,
    drawStartIconContainer: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    placeBottomContentInside: Boolean = false,
    bottomContent: (@Composable () -> Unit)? = null
) {
    CompositionLocalProvider(
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
        val animatedShape = shapeByInteraction(
            shape = shape,
            pressedShape = pressedShape,
            interactionSource = interactionSource
        )
        Card(
            shape = RectangleShape,
            modifier = modifier
                .container(
                    shape = animatedShape,
                    resultPadding = 0.dp,
                    color = containerColor,
                    autoShadowElevation = autoShadowElevation
                )
                .alpha(animateFloatAsState(targetValue = if (enabled) 1f else 0.5f).value),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = contentColor
            )
        ) {
            val onClickModifier = onClick
                ?.let {
                    if (enabled) {
                        Modifier.hapticsCombinedClickable(
                            interactionSource = interactionSource,
                            indication = LocalIndication.current,
                            onClick = onClick,
                            onLongClick = onLongClick
                        )
                    } else {
                        if (onDisabledClick != null) {
                            Modifier.hapticsClickable(onClick = onDisabledClick)
                        } else Modifier
                    }
                } ?: Modifier

            Column(
                modifier = Modifier
                    .then(
                        onClickModifier.takeIf { placeBottomContentInside } ?: Modifier
                    )
            ) {
                Row(
                    modifier = Modifier
                        .then(onClickModifier.takeIf { !placeBottomContentInside } ?: Modifier)
                        .then(resultModifier),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    startIcon?.let {
                        ProvideContainerDefaults(
                            color = containerColor
                        ) {
                            Row {
                                IconShapeContainer(
                                    enabled = drawStartIconContainer,
                                    contentColor = if (overrideIconShapeContentColor) {
                                        Color.Unspecified
                                    } else IconShapeDefaults.contentColor,
                                    content = {
                                        startIcon()
                                    }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                        }
                    }
                    Column(
                        Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    ) {
                        Row {
                            AnimatedContent(
                                targetState = title,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                modifier = Modifier.weight(1f, fill = badge == null)
                            ) { title ->
                                Text(
                                    text = title,
                                    style = titleFontStyle
                                )
                            }
                            badge?.invoke(this)
                        }
                        AnimatedContent(
                            targetState = subtitle,
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { sub ->
                            sub?.let {
                                Column {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = sub,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.Normal,
                                        lineHeight = 14.sp,
                                        color = LocalContentColor.current.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                        if (placeBottomContentInside) bottomContent?.invoke()
                    }
                    ProvideContainerDefaults {
                        endIcon?.invoke()
                    }
                }
                if (!placeBottomContentInside) bottomContent?.invoke()
            }
        }
    }
}