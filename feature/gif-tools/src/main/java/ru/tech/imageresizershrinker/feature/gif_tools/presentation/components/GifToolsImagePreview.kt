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

package ru.tech.imageresizershrinker.feature.gif_tools.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.image.ImagesPreviewWithSelection
import ru.tech.imageresizershrinker.core.ui.widget.image.UrisPreview
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.screenLogic.GifToolsComponent

@Composable
internal fun GifToolsImagePreview(
    component: GifToolsComponent,
    onAddGifsToJxl: () -> Unit,
    onAddGifsToWebp: () -> Unit,
) {
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
                LoadingIndicator()
            } else {
                when (type) {
                    is Screen.GifTools.Type.GifToImage -> {
                        ImagesPreviewWithSelection(
                            imageUris = component.convertedImageUris,
                            imageFrames = component.gifFrames,
                            onFrameSelectionChange = component::updateGifFrames,
                            isPortrait = isPortrait,
                            isLoadingImages = component.isLoadingGifImages
                        )
                    }

                    is Screen.GifTools.Type.GifToJxl -> {
                        UrisPreview(
                            modifier = Modifier
                                .then(
                                    if (!isPortrait) {
                                        Modifier
                                            .layout { measurable, constraints ->
                                                val placeable = measurable.measure(
                                                    constraints = constraints.copy(
                                                        maxHeight = constraints.maxHeight + 48.dp.roundToPx()
                                                    )
                                                )
                                                layout(placeable.width, placeable.height) {
                                                    placeable.place(0, 0)
                                                }
                                            }
                                            .verticalScroll(rememberScrollState())
                                    } else Modifier
                                )
                                .padding(vertical = 24.dp),
                            uris = type.gifUris ?: emptyList(),
                            isPortrait = true,
                            onRemoveUri = {
                                component.setType(
                                    Screen.GifTools.Type.GifToJxl(type.gifUris?.minus(it))
                                )
                            },
                            onAddUris = onAddGifsToJxl
                        )
                    }

                    is Screen.GifTools.Type.GifToWebp -> {
                        UrisPreview(
                            modifier = Modifier
                                .then(
                                    if (!isPortrait) {
                                        Modifier
                                            .layout { measurable, constraints ->
                                                val placeable = measurable.measure(
                                                    constraints = constraints.copy(
                                                        maxHeight = constraints.maxHeight + 48.dp.roundToPx()
                                                    )
                                                )
                                                layout(placeable.width, placeable.height) {
                                                    placeable.place(0, 0)
                                                }
                                            }
                                            .verticalScroll(rememberScrollState())
                                    } else Modifier
                                )
                                .padding(vertical = 24.dp),
                            uris = type.gifUris ?: emptyList(),
                            isPortrait = true,
                            onRemoveUri = {
                                component.setType(
                                    Screen.GifTools.Type.GifToWebp(type.gifUris?.minus(it))
                                )
                            },
                            onAddUris = onAddGifsToWebp
                        )
                    }

                    is Screen.GifTools.Type.ImageToGif -> Unit
                }
            }
        }
    }
}