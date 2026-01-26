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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
    checkedIcon: ImageVector = Icons.Filled.CheckCircle,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = MaterialTheme.colorScheme.onSurface,
    addContainer: Boolean = false
) {
    val image = if (isChecked) {
        checkedIcon
    } else Icons.Outlined.Circle
    val color by animateColorAsState(
        if (isChecked) checkedColor
        else uncheckedColor
    )
    if (onCheck != null) {
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
                    fadeIn() togetherWith fadeOut()
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
        AnimatedContent(
            targetState = Triple(isChecked, image, selectionIndex),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { (isChecked, image, selectionIndex) ->
            if (selectionIndex >= 0) {
                if (isChecked) {
                    Box(
                        modifier = modifier
                            .size(24.dp)
                            .padding(2.dp)
                            .clip(ShapeDefaults.circle)
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        AutoSizeText(
                            text = (selectionIndex + 1).toString(),
                            color = contentColorFor(color),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Icon(
                        imageVector = image,
                        modifier = modifier,
                        contentDescription = null,
                        tint = color
                    )
                }
            } else {
                Icon(
                    imageVector = image,
                    modifier = modifier,
                    contentDescription = null,
                    tint = color
                )
            }
        }
    }
}