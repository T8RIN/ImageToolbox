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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.animation.FancyTransitionEasing
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction

@Composable
fun ExpandableItem(
    modifier: Modifier = Modifier,
    visibleContent: @Composable RowScope.(Boolean) -> Unit,
    expandableContent: @Composable ColumnScope.(Boolean) -> Unit,
    initialState: Boolean = false,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    shape: Shape = ShapeDefaults.large,
    pressedShape: Shape = ShapeDefaults.pressed,
    color: Color = Color.Unspecified,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    canExpand: Boolean = true,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    expansionIconContainerColor: Color = Color.Transparent
) {
    val animatedShape = shapeByInteraction(
        shape = shape,
        pressedShape = pressedShape,
        interactionSource = interactionSource
    )

    Column(
        modifier = Modifier
            .then(modifier)
            .container(
                color = color,
                resultPadding = 0.dp,
                shape = animatedShape
            )
            .animateContentSizeNoClip(
                animationSpec = spec(10)
            )
    ) {
        var expanded by rememberSaveable(initialState) { mutableStateOf(initialState) }
        val rotation by animateFloatAsState(if (expanded) 180f else 0f)
        Row(
            modifier = Modifier
                .clip(animatedShape)
                .hapticsCombinedClickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onLongClick = onLongClick
                ) {
                    if (canExpand) {
                        expanded = !expanded
                    }
                    onClick()
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier.weight(1f)) {
                visibleContent(expanded)
            }
            if (canExpand) {
                EnhancedIconButton(
                    containerColor = expansionIconContainerColor,
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Expand",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        }
        BoxAnimatedVisibility(
            visible = expanded,
            enter = fadeIn(spec(500)) + expandVertically(spec(500)),
            exit = fadeOut(spec(700)) + shrinkVertically(spec(700))
        ) {
            Column(verticalArrangement = verticalArrangement) {
                Spacer(modifier = Modifier.height(8.dp))
                expandableContent(expanded)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun <T> spec(duration: Int): FiniteAnimationSpec<T> = tween(
    durationMillis = duration,
    easing = FancyTransitionEasing
)