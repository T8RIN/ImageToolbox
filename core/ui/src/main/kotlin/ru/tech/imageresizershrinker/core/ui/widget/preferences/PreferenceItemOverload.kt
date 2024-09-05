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

package ru.tech.imageresizershrinker.core.ui.widget.preferences

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeDefaults
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@OptIn(ExperimentalFoundationApi::class)
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
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = Color.Unspecified,
    contentColor: Color = contentColorFor(backgroundColor = color),
    overrideIconShapeContentColor: Boolean = false,
    resultModifier: Modifier = Modifier.padding(16.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
    titleFontStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 18.sp
    ),
    onDisabledClick: (() -> Unit)? = null,
    drawStartIconContainer: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val haptics = LocalHapticFeedback.current
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
        Card(
            shape = shape,
            modifier = modifier
                .container(
                    shape = shape,
                    resultPadding = 0.dp,
                    color = color,
                    autoShadowElevation = autoShadowElevation
                )
                .then(
                    onClick
                        ?.let {
                            if (enabled) {
                                Modifier.combinedClickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current,
                                    onClick = {
                                        haptics.performHapticFeedback(
                                            HapticFeedbackType.LongPress
                                        )
                                        onClick()
                                    },
                                    onLongClick = onLongClick?.let {
                                        {
                                            haptics.performHapticFeedback(
                                                HapticFeedbackType.LongPress
                                            )
                                            onLongClick()
                                        }
                                    }
                                )
                            } else {
                                if (onDisabledClick != null) {
                                    Modifier.clickable {
                                        haptics.performHapticFeedback(
                                            HapticFeedbackType.LongPress
                                        )
                                        onDisabledClick()
                                    }
                                } else Modifier
                            }
                        } ?: Modifier
                )
                .alpha(animateFloatAsState(targetValue = if (enabled) 1f else 0.5f).value),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = contentColor
            )
        ) {
            Row(
                modifier = resultModifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                startIcon?.let {
                    ProvideContainerDefaults {
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
                    AnimatedContent(
                        targetState = title,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { title ->
                        Text(
                            text = title,
                            style = titleFontStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
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
                }
                ProvideContainerDefaults {
                    endIcon?.invoke()
                }
            }
        }
    }
}