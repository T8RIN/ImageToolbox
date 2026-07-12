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

package com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Visibility
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCheckbox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRow
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateItem

@Composable
internal fun DuplicateItemRow(
    item: DuplicateItem,
    isRecommended: Boolean,
    isSelected: Boolean,
    shape: Shape,
    onToggleSelection: () -> Unit,
    onPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    PreferenceRow(
        title = item.name,
        subtitle = remember(
            item.width,
            item.height,
            item.sizeBytes,
            item.format,
            item.distance
        ) {
            listOfNotNull(
                "${item.width} × ${item.height}",
                humanFileSize(item.sizeBytes),
                item.format,
                if (!isRecommended) "Δ = ${item.distance}" else null
            ).joinToString(separator = " • ")
        },
        shape = shape,
        containerColor = if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        onClick = onToggleSelection,
        applyHorizontalPadding = false,
        startContent = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(56.dp)
                    .clip(ShapeDefaults.small)
                    .hapticsClickable(onClick = onPreview)
            ) {
                Picture(
                    model = item.uri.toUri(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    size = 128
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            MaterialTheme.colorScheme
                                .surfaceContainer
                                .copy(0.6f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = null
                    )
                }
            }
        },
        endContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = 4.dp)
            ) {
                AnimatedVisibility(visible = isRecommended) {
                    EnhancedBadge(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Text(stringResource(R.string.best))
                    }
                }
                EnhancedCheckbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() }
                )
            }
        },
        resultModifier = Modifier.padding(12.dp),
        modifier = modifier
    )
}