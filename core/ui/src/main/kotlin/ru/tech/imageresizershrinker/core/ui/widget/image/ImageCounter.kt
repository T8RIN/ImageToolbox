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

package ru.tech.imageresizershrinker.core.ui.widget.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChangeCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun ImageCounter(
    imageCount: Int?,
    onRepick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = imageCount != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .container(shape = CircleShape)
                    .padding(start = 3.dp)
            ) {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f),
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.9f),
                    onClick = { if ((imageCount ?: 0) > 1) onRepick() },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        0.1f,
                        MaterialTheme.colorScheme.tertiaryContainer.copy(0.1f),
                    ),
                    isShadowClip = true
                ) {
                    Text(stringResource(R.string.images, imageCount ?: 0L))
                }
                EnhancedIconButton(
                    onClick = { if ((imageCount ?: 0) > 1) onRepick() },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        0.1f,
                        MaterialTheme.colorScheme.tertiaryContainer.copy(0.1f),
                    ),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f),
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.9f),
                    isShadowClip = true
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChangeCircle,
                        contentDescription = stringResource(R.string.change_preview)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}