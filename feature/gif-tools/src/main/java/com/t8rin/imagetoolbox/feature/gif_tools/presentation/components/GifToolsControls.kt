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

package com.t8rin.imagetoolbox.feature.gif_tools.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.screenLogic.GifToolsComponent

@Composable
internal fun GifToolsControls(component: GifToolsComponent) {
    when (val type = component.type) {
        is Screen.GifTools.Type.GifToImage -> {
            Spacer(modifier = Modifier.height(16.dp))
            ImageFormatSelector(
                value = component.imageFormat,
                onValueChange = component::setImageFormat,
                quality = component.params.quality
            )
            Spacer(modifier = Modifier.height(8.dp))
            QualitySelector(
                imageFormat = component.imageFormat,
                quality = component.params.quality,
                onQualityChange = component::setQuality
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        is Screen.GifTools.Type.ImageToGif -> {
            val addImagesToGifPicker =
                rememberImagePicker(onSuccess = component::addImageToUris)
            Spacer(modifier = Modifier.height(16.dp))
            ImageReorderCarousel(
                images = type.imageUris,
                onReorder = component::reorderImageUris,
                onNeedToAddImage = addImagesToGifPicker::pickImage,
                onNeedToRemoveImageAt = component::removeImageAt,
                onNavigate = component.onNavigate
            )
            Spacer(modifier = Modifier.height(8.dp))
            GifParamsSelector(
                value = component.params,
                onValueChange = component::updateParams
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        is Screen.GifTools.Type.GifToJxl -> {
            QualitySelector(
                imageFormat = ImageFormat.Jxl.Lossy,
                quality = component.jxlQuality,
                onQualityChange = component::setJxlQuality
            )
        }

        is Screen.GifTools.Type.GifToWebp -> {
            QualitySelector(
                imageFormat = ImageFormat.Jpg,
                quality = component.webpQuality,
                onQualityChange = component::setWebpQuality
            )
        }

        null -> Unit
    }
}