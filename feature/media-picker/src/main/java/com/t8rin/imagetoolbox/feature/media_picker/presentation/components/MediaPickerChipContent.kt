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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText

@Composable
internal fun MediaPickerChipContent(
    title: String,
    previewUri: String?,
    count: Long,
    showThumbnail: Boolean,
    showInlineCount: Boolean = false,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    val hasPreview = !previewUri.isNullOrEmpty()
    Column(
        modifier = Modifier
            .animateContentSizeNoClip(alignment = Alignment.Center)
            .then(
                if (showThumbnail && !hasPreview) Modifier.height(140.dp)
                else Modifier
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var titleWidth by rememberSaveable(title) { mutableFloatStateOf(1f) }
        val width = titleWidth.dp
        val density = LocalDensity.current
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon == null) {
                Text(
                    text = title,
                    modifier = Modifier.onSizeChanged {
                        titleWidth = with(density) { it.width.toDp().coerceAtLeast(100.dp).value }
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .heightIn(min = 20.dp)
                        .onSizeChanged {
                            titleWidth =
                                with(density) { it.width.toDp().coerceAtLeast(100.dp).value }
                        },
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = title)
                }
            }
            if (showInlineCount && (!showThumbnail || !hasPreview) && !isLoading) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalContentColor.current.copy(alpha = 0.65f)
                )
            }
        }
        BoxAnimatedVisibility(
            visible = showThumbnail && hasPreview,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box {
                BoxAnimatedVisibility(
                    visible = width > 1.dp,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Picture(
                        model = previewUri,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .height(100.dp)
                            .width(width)
                            .alpha(if (isLoading) 0f else 1f),
                        shape = ShapeDefaults.small
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(100.dp)
                        .width(width)
                        .clip(ShapeDefaults.small)
                        .background(MaterialTheme.colorScheme.surfaceContainer.copy(0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.alpha(if (isLoading) 0f else 1f)
                    )
                }
            }
        }
    }
}