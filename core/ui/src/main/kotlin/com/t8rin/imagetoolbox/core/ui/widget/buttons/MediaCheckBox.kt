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

package com.t8rin.imagetoolbox.core.ui.widget.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.CheckCircle
import com.t8rin.imagetoolbox.core.resources.icons.Circle
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.ui.theme.suggestContainerColorBy
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText

@Composable
fun MediaCheckBox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    selectionIndex: Int = -1,
    onCheck: (() -> Unit)? = null,
    checkedIcon: ImageVector = Icons.Rounded.CheckCircle,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = MaterialTheme.colorScheme.onSurface,
    addContainer: Boolean = false,
    numberSize: Dp = 24.dp,
    uncheckedIcon: Painter? = null,
    transitionState: MutableTransitionState<Triple<Boolean, ImageVector, Int>>? = null
) {
    val image = if (isChecked) {
        checkedIcon
    } else {
        Icons.Outlined.Circle
    }
    if (onCheck != null) {
        val color by animateColorAsState(if (isChecked) checkedColor else uncheckedColor)
        EnhancedIconButton(
            onClick = onCheck,
            modifier = modifier,
            containerColor = animateColorAsState(
                if (addContainer) MaterialTheme.colorScheme.suggestContainerColorBy(color)
                else Color.Transparent
            ).value
        ) {
            AnimatedContent(
                targetState = image,
                transitionSpec = {
                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                }
            ) { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            }
        }
    } else {
        val target = Triple(isChecked, image, selectionIndex)
        val state = transitionState ?: remember { MutableTransitionState(target) }
        state.targetState = target

        if (state.isIdle) {
            MediaCheckBoxContent(
                modifier = modifier,
                isChecked = isChecked,
                image = image,
                selectionIndex = selectionIndex,
                color = if (isChecked) checkedColor else uncheckedColor,
                numberSize = numberSize,
                uncheckedIcon = uncheckedIcon
            )
        } else {
            val transition = rememberTransition(state)
            val color by transition.animateColor {
                if (it.first) checkedColor else uncheckedColor
            }
            transition.AnimatedContent(
                transitionSpec = {
                    if (initialState.third >= 0 && targetState.third >= 0) {
                        fadeIn() + scaleIn(initialScale = 0.85f) togetherWith fadeOut() + scaleOut(
                            targetScale = 0.85f
                        )
                    } else {
                        fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                    }
                }
            ) { (checked, icon, index) ->
                MediaCheckBoxContent(
                    modifier = modifier,
                    isChecked = checked,
                    image = icon,
                    selectionIndex = index,
                    color = color,
                    numberSize = numberSize,
                    uncheckedIcon = uncheckedIcon
                )
            }
        }
    }
}

@Composable
private fun MediaCheckBoxContent(
    modifier: Modifier,
    isChecked: Boolean,
    image: ImageVector,
    selectionIndex: Int,
    color: Color,
    numberSize: Dp,
    uncheckedIcon: Painter?
) {
    if (isChecked && selectionIndex >= 0) {
        Box(
            modifier = modifier
                .size(numberSize)
                .padding(2.dp)
                .clip(ShapeDefaults.circle)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            val style = MaterialTheme.typography.bodySmall
            AutoSizeText(
                text = (selectionIndex + 1).toString(),
                style = style.copy(
                    color = contentColorFor(color),
                    fontWeight = FontWeight.Bold,
                    lineHeight = TextUnit.Unspecified
                ),
                maxLines = 1,
                softWrap = false
            )
        }
    } else {
        Icon(
            painter = uncheckedIcon?.takeUnless { isChecked } ?: rememberVectorPainter(image),
            modifier = modifier,
            contentDescription = null,
            tint = color
        )
    }
}