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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ZoomIn
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.derivative.EnhancedZoomableModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun ZoomModalSheet(
    data: Any?,
    visible: Boolean,
    onDismiss: () -> Unit,
    transformations: List<Transformation> = emptyList()
) {
    if (data != null) {
        EnhancedZoomableModalBottomSheet(
            visible = visible,
            onDismiss = onDismiss,
            title = {
                TitleItem(
                    text = stringResource(R.string.zoom),
                    icon = Icons.Outlined.ZoomIn
                )
            }
        ) {
            var aspectRatio by remember(data) {
                mutableFloatStateOf(1f)
            }
            Picture(
                model = data,
                contentDescription = null,
                onSuccess = {
                    aspectRatio = it.result.image.safeAspectRatio
                },
                contentScale = ContentScale.FillBounds,
                showTransparencyChecker = false,
                transformations = transformations,
                modifier = Modifier.aspectRatio(aspectRatio)
            )
        }
    }
}