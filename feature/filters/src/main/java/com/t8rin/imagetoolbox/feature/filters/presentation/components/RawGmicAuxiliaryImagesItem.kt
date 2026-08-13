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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Image
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
internal fun RawGmicAuxiliaryImagesItem(
    images: List<ImageModel>,
    onImagesChange: (List<ImageModel>) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val uris = images.map { it.data.toString().toUri() }
    val imagePicker = rememberImagePicker { selectedUris: List<Uri> ->
        onImagesChange(
            (images + selectedUris.map(::ImageModel)).distinctBy { it.data.toString() }
        )
    }

    Column(
        modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            bottom = 8.dp
        )
    ) {
        AnimatedContent(targetState = uris.isEmpty()) { isEmpty ->
            if (isEmpty) {
                PreferenceItem(
                    title = stringResource(R.string.gmic_auxiliary_images),
                    subtitle = stringResource(R.string.gmic_auxiliary_images_empty_sub),
                    startIcon = Icons.TwoTone.Image,
                    endIcon = Icons.Rounded.MiniEdit,
                    onClick = imagePicker::pickImage,
                    shape = ShapeDefaults.extraLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column {
                    ImageReorderCarousel(
                        images = uris,
                        onReorder = { reorderedUris ->
                            onImagesChange(reorderedUris.map(::ImageModel))
                        },
                        onNeedToAddImage = imagePicker::pickImage,
                        onNeedToRemoveImageAt = { index ->
                            onImagesChange(images.toMutableList().apply { removeAt(index) })
                        },
                        onNavigate = onNavigate,
                        title = stringResource(R.string.gmic_auxiliary_images),
                        minimumImageCount = 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .container(ShapeDefaults.extraLarge)
                    )
                    InfoContainer(
                        text = stringResource(R.string.gmic_auxiliary_images_usage_sub)
                    )
                }
            }
        }
    }
}
