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

package ru.tech.imageresizershrinker.media_picker.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun MediaCheckBox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheck: (() -> Unit)? = null
) {
    val image = if (isChecked) Icons.Filled.CheckCircle else Icons.Outlined.Circle
    val color = if (isChecked) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface
    if (onCheck != null) {
        IconButton(
            onClick = onCheck,
            modifier = modifier
        ) {
            Image(
                imageVector = image,
                colorFilter = ColorFilter.tint(color),
                contentDescription = null
            )
        }
    } else {
        Image(
            imageVector = image,
            colorFilter = ColorFilter.tint(color),
            modifier = modifier,
            contentDescription = null
        )
    }
}