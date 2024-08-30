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

package ru.tech.imageresizershrinker.feature.media_picker.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MediaCheckBox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheck: (() -> Unit)? = null,
    checkedIcon: ImageVector = Icons.Filled.CheckCircle,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val image = if (isChecked) {
        checkedIcon
    } else Icons.Outlined.Circle
    val color by animateColorAsState(
        if (isChecked) checkedColor
        else uncheckedColor
    )
    if (onCheck != null) {
        IconButton(
            onClick = onCheck,
            modifier = modifier
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
            targetState = image,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { icon ->
            Icon(
                imageVector = icon,
                modifier = modifier,
                contentDescription = null,
                tint = color
            )
        }
    }
}