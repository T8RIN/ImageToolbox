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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSwitch

@Composable
fun PreferenceRowSwitch(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    subtitle: String? = null,
    autoShadowElevation: Dp = 1.dp,
    applyHorPadding: Boolean = true,
    checked: Boolean,
    color: Color = MaterialTheme.colorScheme.secondaryContainer.copy(
        alpha = 0.2f
    ),
    contentColor: Color? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    startContent: (@Composable () -> Unit)? = null,
    resultModifier: Modifier = Modifier.padding(
        horizontal = if (startContent != null) 0.dp else 16.dp,
        vertical = 8.dp
    ),
    changeAlphaWhenDisabled: Boolean = true,
    onClick: (Boolean) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    PreferenceRow(
        autoShadowElevation = autoShadowElevation,
        enabled = enabled,
        modifier = modifier,
        resultModifier = resultModifier,
        applyHorPadding = applyHorPadding,
        title = title,
        contentColor = contentColor,
        shape = shape,
        changeAlphaWhenDisabled = changeAlphaWhenDisabled,
        color = color,
        subtitle = subtitle,
        startContent = startContent,
        onClick = {
            haptics.performHapticFeedback(
                HapticFeedbackType.LongPress
            )
            onClick(!checked)
        },
        endContent = {
            EnhancedSwitch(
                modifier = Modifier.padding(start = 8.dp),
                thumbIcon = if (checked) Icons.Rounded.Check else null,
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline.blend(
                        color, 0.3f
                    ),
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline.blend(
                        color, 0.3f
                    ),
                    uncheckedTrackColor = color,
                    disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.12f)
                        .compositeOver(MaterialTheme.colorScheme.surface),
                    checkedIconColor = MaterialTheme.colorScheme.primary
                ),
                enabled = enabled,
                checked = checked,
                onCheckedChange = onClick
            )
        }
    )
}

@Composable
fun PreferenceRowSwitch(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    subtitle: String? = null,
    autoShadowElevation: Dp = 1.dp,
    checked: Boolean,
    color: Color = MaterialTheme.colorScheme.secondaryContainer.copy(
        alpha = 0.2f
    ),
    changeAlphaWhenDisabled: Boolean = true,
    contentColor: Color? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    startIcon: ImageVector?,
    onClick: (Boolean) -> Unit
) {
    PreferenceRowSwitch(
        modifier = modifier,
        title = title,
        enabled = enabled,
        subtitle = subtitle,
        changeAlphaWhenDisabled = changeAlphaWhenDisabled,
        autoShadowElevation = autoShadowElevation,
        checked = checked,
        color = color,
        contentColor = contentColor,
        shape = shape,
        startContent = startIcon?.let {
            {
                Icon(
                    imageVector = startIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        resultModifier = Modifier.padding(16.dp),
        applyHorPadding = false,
        onClick = onClick
    )
}