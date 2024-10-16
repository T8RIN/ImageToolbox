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

package ru.tech.imageresizershrinker.core.ui.widget.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun ExpandableItem(
    modifier: Modifier = Modifier,
    visibleContent: @Composable RowScope.(Boolean) -> Unit,
    expandableContent: @Composable ColumnScope.(Boolean) -> Unit,
    initialState: Boolean = false,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    shape: Shape = RoundedCornerShape(20.dp),
    color: Color = Color.Unspecified,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    canExpand: Boolean = true,
    onClick: () -> Unit = {},
    expansionIconContainerColor: Color = Color.Transparent
) {
    val haptics = LocalHapticFeedback.current
    Column(
        Modifier
            .animateContentSize()
            .then(modifier)
            .container(
                color = color,
                resultPadding = 0.dp,
                shape = shape
            )
    ) {
        var expanded by rememberSaveable { mutableStateOf(initialState) }
        val rotation by animateFloatAsState(if (expanded) 180f else 0f)
        Row(
            modifier = Modifier
                .clip(shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current
                ) {
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
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
        AnimatedVisibility(expanded) {
            Column(verticalArrangement = verticalArrangement) {
                Spacer(modifier = Modifier.height(8.dp))
                expandableContent(expanded)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}