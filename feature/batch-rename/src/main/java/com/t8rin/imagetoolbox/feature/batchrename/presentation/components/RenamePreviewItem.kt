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

package com.t8rin.imagetoolbox.feature.batchrename.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.CheckBoxOutlineBlank
import com.t8rin.imagetoolbox.core.resources.icons.RemoveCircle
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun RenamePreviewItem(
    preview: RenamePreview,
    index: Int,
    shape: Shape,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentAlpha = if (preview.isChanged) 1f else 0.55f
    val thumbnailShape = ShapeDefaults.small

    Column(
        modifier = modifier
            .fillMaxWidth()
            .container(
                shape = shape,
                resultPadding = 12.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .container(
                        shape = thumbnailShape,
                        color = MaterialTheme.colorScheme.surface,
                        resultPadding = 0.dp
                    )
            ) {
                Picture(
                    model = preview.uri,
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    contentScale = ContentScale.Crop,
                    error = {
                        BoxWithConstraints(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val width = this.maxWidth

                            Icon(
                                imageVector = Icons.Rounded.CheckBoxOutlineBlank,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(width / 1.4f)
                                    .align(Alignment.Center),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer.copy(0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = preview.originalName,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                color = LocalContentColor.current.copy(alpha = contentAlpha),
                textDecoration = if (preview.isChanged) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clip(ShapeDefaults.circle)
                    .hapticsClickable(
                        onClick = onRemove
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.RemoveCircle,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }

        if (!preview.isChanged || preview.newName.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (preview.isChanged) {
                    preview.newName
                } else {
                    stringResource(R.string.rename_unchanged)
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 16.sp,
                color = if (preview.isChanged) {
                    MaterialTheme.colorScheme.primary
                } else {
                    LocalContentColor.current.copy(alpha = 0.5f)
                }
            )
        }
    }
}

@Composable
@Preview(locale = "ru")
private fun Preview() = ImageToolboxThemeForPreview(
    isDarkTheme = false,
    isImageError = true
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RenamePreviewItem(
            preview = RenamePreview(
                uri = Uri.EMPTY,
                originalName = "preview.png",
                newName = "Preview-new.png",
                usedFallbackDate = false,
                isChanged = true
            ),
            index = 0,
            shape = ShapeDefaults.default,
            onRemove = {}
        )
        RenamePreviewItem(
            preview = RenamePreview(
                uri = Uri.EMPTY,
                originalName = "preview.jpg",
                newName = "preview.jpg",
                usedFallbackDate = false,
                isChanged = false
            ),
            index = 0,
            shape = ShapeDefaults.default,
            onRemove = {}
        )
        RenamePreviewItem(
            preview = RenamePreview(
                uri = Uri.EMPTY,
                originalName = "preview.jpg",
                newName = "",
                usedFallbackDate = false,
                isChanged = false
            ),
            index = 0,
            shape = ShapeDefaults.default,
            onRemove = {}
        )
    }
}