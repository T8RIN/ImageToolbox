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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Slideshow
import com.t8rin.imagetoolbox.core.ui.theme.StrongBlack
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer

@Composable
internal fun FilterPreviewPicture(
    model: Any?,
    canShowImage: Boolean,
    canOpenPreview: Boolean,
    onOpenPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (canShowImage) {
            Picture(
                model = model,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .scale(1.2f),
                shape = MaterialTheme.shapes.medium
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(48.dp)
                    .scale(1.2f)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmer(true)
            )
        }
        if (canOpenPreview) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(ShapeDefaults.circle)
                    .hapticsClickable(onClick = onOpenPreview),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Slideshow,
                    contentDescription = stringResource(R.string.image_preview),
                    tint = StrongBlack,
                    modifier = Modifier.scale(1.2f)
                )
                Icon(
                    imageVector = Icons.Outlined.Slideshow,
                    contentDescription = stringResource(R.string.image_preview),
                    tint = White
                )
            }
        }
    }
}