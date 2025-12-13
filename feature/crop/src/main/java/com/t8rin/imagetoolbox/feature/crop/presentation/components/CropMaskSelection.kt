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

package com.t8rin.imagetoolbox.feature.crop.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.CornerRadiusProperties
import com.smarttoolfactory.cropper.model.CutCornerCropShape
import com.smarttoolfactory.cropper.model.ImageMaskOutline
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RoundedCornerCropShape
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.widget.CropFrameDisplayCard
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CropMaskSelection(
    modifier: Modifier = Modifier,
    selectedItem: CropOutlineProperty,
    loadImage: suspend (Uri) -> ImageBitmap?,
    onCropMaskChange: (CropOutlineProperty) -> Unit,
    color: Color = Color.Unspecified,
    shape: Shape = ShapeDefaults.extraLarge
) {
    var cornerRadius by rememberSaveable { mutableIntStateOf(20) }

    val outlineProperties = DefaultOutlineProperties

    val scope = rememberCoroutineScope()

    val maskLauncher = rememberImagePicker { uri: Uri ->
        scope.launch {
            loadImage(uri)?.let {
                onCropMaskChange(
                    outlineProperties.last().run {
                        copy(
                            cropOutline = (cropOutline as ImageMaskOutline).copy(image = it)
                        )
                    }
                )
            }
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.container(
            color = color,
            shape = shape
        )
    ) {
        Text(
            text = stringResource(id = R.string.crop_mask),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 16.dp),
            fontWeight = FontWeight.Medium
        )
        val listState = rememberLazyListState()
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 16.dp + WindowInsets
                    .navigationBars
                    .asPaddingValues()
                    .calculateEndPadding(LocalLayoutDirection.current)
            ),
            state = listState,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fadingEdges(listState)
        ) {
            itemsIndexed(
                items = outlineProperties,
                key = { _, o -> o.cropOutline.id }
            ) { _, item ->
                val selected = selectedItem.cropOutline.id == item.cropOutline.id
                CropFrameDisplayCard(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .height(100.dp)
                        .container(
                            resultPadding = 0.dp,
                            color = animateColorAsState(
                                targetValue = if (selected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else MaterialTheme.colorScheme.surfaceContainerLowest,
                            ).value,
                            borderColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                0.7f
                            )
                            else MaterialTheme.colorScheme.outlineVariant()
                        )
                        .hapticsClickable {
                            if (item.cropOutline is ImageMaskOutline) {
                                maskLauncher.pickImage()
                            } else {
                                onCropMaskChange(item)
                            }
                            cornerRadius = 20
                        }
                        .padding(16.dp),
                    editable = false,
                    scale = 1f,
                    outlineColor = MaterialTheme.colorScheme.secondary,
                    title = "",
                    cropOutline = item.cropOutline
                )
            }
        }

        EnhancedSliderItem(
            visible = selectedItem.cropOutline.id == 1 || selectedItem.cropOutline.id == 2,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            shape = ShapeDefaults.default,
            value = cornerRadius,
            title = stringResource(R.string.radius),
            icon = null,
            internalStateTransformation = {
                it.roundToInt()
            },
            containerColor = MaterialTheme.colorScheme.surface,
            onValueChange = {
                cornerRadius = it.roundToInt()
                if (selectedItem.cropOutline is CutCornerCropShape) {
                    onCropMaskChange(
                        selectedItem.copy(
                            cropOutline = CutCornerCropShape(
                                id = selectedItem.cropOutline.id,
                                title = selectedItem.cropOutline.title,
                                cornerRadius = CornerRadiusProperties(
                                    topStartPercent = cornerRadius,
                                    topEndPercent = cornerRadius,
                                    bottomStartPercent = cornerRadius,
                                    bottomEndPercent = cornerRadius
                                )
                            )
                        )
                    )
                } else if (selectedItem.cropOutline is RoundedCornerCropShape) {
                    onCropMaskChange(
                        selectedItem.copy(
                            cropOutline = RoundedCornerCropShape(
                                id = selectedItem.cropOutline.id,
                                title = selectedItem.cropOutline.title,
                                cornerRadius = CornerRadiusProperties(
                                    topStartPercent = cornerRadius,
                                    topEndPercent = cornerRadius,
                                    bottomStartPercent = cornerRadius,
                                    bottomEndPercent = cornerRadius
                                )
                            )
                        )
                    )
                }
            },
            valueRange = 0f..50f,
            steps = 50
        )
        AnimatedVisibility(
            selectedItem.cropOutline.title == OutlineType.ImageMask.name
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .container(shape = ShapeDefaults.extraLarge)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.image_crop_mask_sub),
                    textAlign = TextAlign.Center,
                    color = LocalContentColor.current.copy(0.5f),
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}