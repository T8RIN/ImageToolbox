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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.ui.utils.helper.LinkPreview
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
fun LinkPreviewCard(
    linkPreview: LinkPreview,
    shape: Shape
) {
    val essentials = rememberLocalEssentials()
    val linkHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .container(
                shape = shape,
                color = MaterialTheme.colorScheme.surface,
                resultPadding = 0.dp
            )
            .hapticsCombinedClickable(
                onClick = {
                    linkPreview.link?.let(linkHandler::openUri)
                },
                onLongClick = {
                    linkPreview.link?.let {
                        essentials.copyToClipboard(
                            text = it,
                            icon = Icons.Default.Link
                        )
                    }
                },
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var sizeOfRight by remember {
            mutableStateOf(80.dp)
        }
        val density = LocalDensity.current
        Picture(
            model = linkPreview.image,
            contentDescription = stringResource(R.string.image_link),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(80.dp)
                .height(sizeOfRight),
            alignment = Alignment.Center,
            error = {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp)
                        .clip(MaterialStarShape)
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer
                                .copy(0.5f)
                                .compositeOver(MaterialTheme.colorScheme.surface)
                        )
                        .padding(8.dp)
                )
            },
            filterQuality = FilterQuality.High
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .onSizeChanged {
                    sizeOfRight =
                        if (linkPreview.title.isNullOrBlank() && linkPreview.description.isNullOrBlank()) {
                            80.dp
                        } else {
                            with(density) { it.height.toDp() }
                        }
                }
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            if (!linkPreview.title.isNullOrBlank()) {
                Text(
                    text = linkPreview.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            if (!linkPreview.description.isNullOrBlank()) {
                Text(
                    text = linkPreview.description,
                    maxLines = if (linkPreview.title.isNullOrBlank()) 3 else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    lineHeight = 16.sp
                )
            }
            if ((linkPreview.description.isNullOrBlank() || linkPreview.title.isNullOrBlank()) && (!linkPreview.url.isNullOrBlank() || !linkPreview.link.isNullOrBlank())) {
                Text(
                    text = linkPreview.url ?: linkPreview.link ?: "",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}