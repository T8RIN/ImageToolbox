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

package com.t8rin.imagetoolbox.core.ui.widget.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChangeCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
fun ImageCounter(
    imageCount: Int?,
    onRepick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier.padding(bottom = 16.dp),
        visible = imageCount != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .container(shape = ShapeDefaults.circle)
                .padding(start = 3.dp),
            horizontalArrangement = Arrangement.spacedBy((-1).dp)
        ) {
            LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f),
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.9f),
                    onClick = { if ((imageCount ?: 0) > 1) onRepick() },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f,
                        onTopOf = MaterialTheme
                            .colorScheme
                            .tertiaryContainer
                            .copy(0.1f),
                    ),
                    isShadowClip = true,
                    shape = AutoCornersShape(
                        topStart = CornerSize(50),
                        topEnd = CornerSize(4.dp),
                        bottomStart = CornerSize(50),
                        bottomEnd = CornerSize(4.dp),
                    )
                ) {
                    Text(stringResource(R.string.images, imageCount ?: 0L))
                }
                EnhancedIconButton(
                    onClick = { if ((imageCount ?: 0) > 1) onRepick() },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f,
                        onTopOf = MaterialTheme
                            .colorScheme
                            .tertiaryContainer
                            .copy(0.1f),
                    ),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f),
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.9f),
                    isShadowClip = true,
                    shape = AutoCornersShape(
                        topEnd = CornerSize(50),
                        topStart = CornerSize(4.dp),
                        bottomEnd = CornerSize(50),
                        bottomStart = CornerSize(4.dp),
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChangeCircle,
                        contentDescription = stringResource(R.string.change_preview)
                    )
                }
            }
        }
    }
}