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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagesPreviewWithSelection
import com.t8rin.imagetoolbox.core.ui.widget.modifier.negativePadding
import com.t8rin.imagetoolbox.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent

@Composable
internal fun ParsedImagesSelection(
    component: LoadNetImageComponent
) {
    AnimatedVisibility(component.parsedImages.size > 1) {
        ImagesPreviewWithSelection(
            imageUris = component.parsedImages,
            imageFrames = component.imageFrames,
            onFrameSelectionChange = component::updateImageFrames,
            isPortrait = true,
            isLoadingImages = component.isImageLoading,
            contentScale = ContentScale.Fit,
            contentPadding = PaddingValues(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    (130.dp * component.parsedImages.size).coerceAtMost(420.dp)
                )
                .negativePadding(horizontal = 20.dp),
            showExtension = false
        )
    }
}