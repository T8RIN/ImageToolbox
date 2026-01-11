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

package com.t8rin.imagetoolbox.feature.load_net_image.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.data.utils.safeAspectRatio
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent

@Composable
internal fun ParsedImagePreview(
    component: LoadNetImageComponent
) {
    Picture(
        allowHardware = false,
        model = component.targetUrl,
        modifier = Modifier
            .container(
                resultPadding = 8.dp
            )
            .then(
                if (component.bitmap == null) {
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                } else {
                    Modifier.aspectRatio(component.bitmap?.safeAspectRatio ?: 2f)
                }
            ),
        isLoadingFromDifferentPlace = component.isImageLoading,
        contentScale = ContentScale.FillBounds,
        shape = MaterialTheme.shapes.small,
        error = {
            if (component.bitmap != null) {
                Picture(
                    modifier = Modifier.fillMaxSize(),
                    model = component.bitmap,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.FillBounds
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BrokenImageAlt,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .size(64.dp)
                    )
                    Text(stringResource(id = R.string.no_image))
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    )
}