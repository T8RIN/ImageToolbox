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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EnhancedToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shapes: ToggleButtonShapes = ToggleButtonDefaults.shapesFor(ButtonDefaults.MinHeight),
    colors: ToggleButtonColors = ToggleButtonDefaults.toggleButtonColors(),
    elevation: Dp = 0.dp,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val realInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    val containerColor = colors.containerColor(enabled, checked)
    val contentColor = colors.contentColor(enabled, checked)
    val buttonShape = shapeByInteraction(
        shape = if (checked) shapes.checkedShape else shapes.shape,
        pressedShape = shapes.pressedShape,
        interactionSource = realInteractionSource
    )

    val haptics = LocalHapticFeedback.current
    val focus = LocalFocusManager.current

    val scope = rememberCoroutineScope()

    LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
        Surface(
            checked = checked,
            onCheckedChange = {
                haptics.longPress()
                onCheckedChange(it)

                scope.launch {
                    delay(200)
                    focus.clearFocus()
                }
            },
            modifier = modifier.semantics { role = Role.Checkbox },
            enabled = enabled,
            shape = buttonShape,
            color = containerColor,
            contentColor = contentColor,
            shadowElevation = elevation,
            border = border,
            interactionSource = realInteractionSource
        ) {
            val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge)

            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                LocalTextStyle provides mergedStyle
            ) {
                Row(
                    modifier = Modifier
                        .defaultMinSize(minHeight = ToggleButtonDefaults.MinHeight)
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

@Stable
private fun ToggleButtonColors.containerColor(enabled: Boolean, checked: Boolean): Color {
    return when {
        enabled && checked -> checkedContainerColor
        enabled && !checked -> containerColor
        else -> disabledContainerColor
    }
}

@Stable
private fun ToggleButtonColors.contentColor(enabled: Boolean, checked: Boolean): Color {
    return when {
        enabled && checked -> checkedContentColor
        enabled && !checked -> contentColor
        else -> disabledContentColor
    }
}