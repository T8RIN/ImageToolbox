/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.jxl_tools.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagesPreviewWithSelection
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.jxl_tools.presentation.screenLogic.JxlToolsComponent

@Composable
internal fun JxlToolsBitmapPreview(
    component: JxlToolsComponent,
    onAddImages: () -> Unit
) {
    val uris = when (val type = component.type) {
        is Screen.JxlTools.Type.JpegToJxl -> type.jpegImageUris
        is Screen.JxlTools.Type.JxlToJpeg -> type.jxlImageUris
        is Screen.JxlTools.Type.ImageToJxl -> type.imageUris
        is Screen.JxlTools.Type.JxlToImage -> listOfNotNull(type.jxlUri)
        null -> null
    } ?: emptyList()

    val isPortrait by isPortraitOrientationAsState()

    AnimatedContent(
        targetState = component.isLoading to component.type
    ) { (loading, type) ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = if (loading) {
                Modifier.padding(32.dp)
            } else Modifier
        ) {
            if (loading || type == null) {
                EnhancedLoadingIndicator()
            } else {
                when (type) {
                    is Screen.JxlTools.Type.JxlToImage -> {
                        ImagesPreviewWithSelection(
                            imageUris = component.convertedImageUris,
                            imageFrames = component.imageFrames,
                            onFrameSelectionChange = component::updateJxlFrames,
                            isPortrait = isPortrait,
                            isLoadingImages = component.isLoadingJxlImages
                        )
                    }

                    is Screen.JxlTools.Type.ImageToJxl -> {
                        ImageReorderCarousel(
                            images = uris,
                            modifier = Modifier
                                .padding(top = if (isPortrait) 24.dp else 0.dp)
                                .container(
                                    shape = ShapeDefaults.extraLarge,
                                    color = if (isPortrait) {
                                        Color.Unspecified
                                    } else MaterialTheme.colorScheme.surface
                                ),
                            onReorder = {
                                component.setType(
                                    Screen.JxlTools.Type.ImageToJxl(it)
                                )
                            },
                            onNeedToAddImage = onAddImages,
                            onNeedToRemoveImageAt = {
                                component.setType(
                                    Screen.JxlTools.Type.ImageToJxl(
                                        (component.type as Screen.JxlTools.Type.ImageToJxl)
                                            .imageUris?.toMutableList()
                                            ?.apply {
                                                removeAt(it)
                                            }
                                    )
                                )
                            },
                            onNavigate = component.onNavigate
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    else -> Unit
                }
            }
        }
    }
}