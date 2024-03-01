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

@file:Suppress("LocalVariableName")

package ru.tech.imageresizershrinker.core.ui.widget.preferences

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreferenceItem(
    onClick: (() -> Unit)? = null,
    title: String,
    enabled: Boolean = true,
    subtitle: String? = null,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    autoShadowElevation: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = if (color == MaterialTheme.colorScheme.surfaceContainer) contentColorFor(
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) else contentColorFor(backgroundColor = color),
    drawStartIconContainer: Boolean = true,
    titleFontStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 18.sp
    ),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
) {
    val targetIcon: (@Composable () -> Unit)? = if (startIcon == null) null else {
        {
            AnimatedContent(
                targetState = startIcon,
                transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
            ) { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        }
    }
    val targetEndIcon: (@Composable () -> Unit)? = if (endIcon == null) null else {
        {
            Box {
                AnimatedContent(
                    targetState = endIcon,
                    transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
                ) { endIcon ->
                    Icon(imageVector = endIcon, contentDescription = null)
                }
            }
        }
    }

    PreferenceItemOverload(
        autoShadowElevation = autoShadowElevation,
        contentColor = contentColor,
        onClick = onClick,
        enabled = enabled,
        title = title,
        subtitle = subtitle,
        startIcon = targetIcon,
        endIcon = targetEndIcon,
        shape = shape,
        color = color,
        modifier = modifier,
        titleFontStyle = titleFontStyle,
        drawStartIconContainer = drawStartIconContainer
    )
}