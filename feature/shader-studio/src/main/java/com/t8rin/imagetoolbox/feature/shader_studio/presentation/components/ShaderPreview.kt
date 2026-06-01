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

package com.t8rin.imagetoolbox.feature.shader_studio.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeAspectRatio
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic.ShaderStudioComponent

@Composable
internal fun ShaderPreview(component: ShaderStudioComponent) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        var aspectRatio by remember {
            mutableFloatStateOf(1f)
        }

        Picture(
            model = component.previewModel.data,
            modifier = Modifier
                .container(MaterialTheme.shapes.medium)
                .aspectRatio(aspectRatio),
            onSuccess = {
                aspectRatio = it.result.image.toBitmap().safeAspectRatio
            },
            isLoadingFromDifferentPlace = component.isImageLoading,
            shape = MaterialTheme.shapes.medium,
            contentScale = ContentScale.FillBounds,
            transformations = remember(
                component.shaderSource,
                component.helperSource,
                component.params
            ) {
                component.getPreviewTransformations()
            }
        )
        if (component.isImageLoading) EnhancedLoadingIndicator()
    }
}

@Composable
internal fun ShaderPreviewSourceSelector(component: ShaderStudioComponent) {
    val previewModel = component.previewModel

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Max)
    ) {
        ImageSelector(
            value = previewModel.data,
            onValueChange = { component.setFilterPreviewModel(it.toString()) },
            title = stringResource(R.string.filter_preview_image),
            subtitle = stringResource(R.string.filter_preview_image_sub),
            contentScale = ContentScale.Crop,
            color = Color.Unspecified,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = ShapeDefaults.start
        )
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(2) { index ->
                val shape = if (index == 0) {
                    ShapeDefaults.topEnd
                } else {
                    ShapeDefaults.bottomEnd
                }
                val containerColor = takeColorFromScheme {
                    when (previewModel.data) {
                        R.drawable.filter_preview_source if index == 0 -> secondary
                        R.drawable.filter_preview_source_3 if index == 1 -> secondary
                        else -> secondaryContainer
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .hapticsClickable {
                            component.setFilterPreviewModel(index.toString())
                        }
                        .container(
                            color = containerColor,
                            shape = shape,
                            resultPadding = 0.dp
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = (index + 1).toString(),
                        color = contentColorFor(containerColor)
                    )
                }
            }
        }
    }
}
