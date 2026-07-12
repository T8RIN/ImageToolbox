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

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.request.ImageRequest
import com.t8rin.imagetoolbox.core.data.image.utils.static
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.resources.icons.FitScreen
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.Repeat
import com.t8rin.imagetoolbox.core.resources.icons.RepeatOne
import com.t8rin.imagetoolbox.core.resources.icons.RotateLeft
import com.t8rin.imagetoolbox.core.resources.icons.Timelapse
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.isGif
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.screenLogic.GifToolsComponent
import kotlinx.collections.immutable.persistentMapOf
import kotlin.math.roundToInt

@Composable
internal fun GifMergeControls(
    component: GifToolsComponent,
    type: Screen.GifTools.Type.MergeGif
) {
    val addPicker = rememberFilePicker(
        mimeType = MimeType.Gif,
        onSuccess = { uris: List<Uri> ->
            component.addMergeUris(uris.filter(Uri::isGif))
        }
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        ImageReorderCarousel(
            images = type.gifUris,
            onReorder = component::reorderMergeUris,
            onNeedToAddImage = addPicker::pickFile,
            onNeedToRemoveImageAt = component::removeMergeUriAt,
            onNavigate = component.onNavigate,
            title = stringResource(R.string.gif_clips_order)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.container(
                shape = ShapeDefaults.large,
                resultPadding = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TitleItem(
                text = stringResource(R.string.params),
                icon = Icons.Rounded.Build,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            type.gifUris.orEmpty().forEachIndexed { index, uri ->
                var expanded by rememberSaveable { mutableStateOf(false) }
                val rotation by animateFloatAsState(if (expanded) 180f else 0f)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.large,
                            color = MaterialTheme.colorScheme.surface
                        )
                        .padding(8.dp)
                ) {
                    val item = component.mergeItem(uri)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .container(
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color.Transparent,
                                    resultPadding = 0.dp
                                )
                        ) {
                            Picture(
                                model = remember(uri) {
                                    ImageRequest.Builder(appContext)
                                        .data(uri)
                                        .static()
                                        .build()
                                },
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                shape = RectangleShape,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        MaterialTheme.colorScheme
                                            .surfaceContainer
                                            .copy(0.6f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = rememberFilename(uri) ?: uri.lastPathSegment.orEmpty(),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.weight(1f)
                        )
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            onClick = { expanded = !expanded }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "Expand",
                                modifier = Modifier.rotate(rotation)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = expanded,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            PreferenceRowSwitch(
                                title = stringResource(R.string.reverse),
                                subtitle = stringResource(R.string.reverse_sub),
                                checked = item.reverse,
                                onClick = { component.updateMergeItem(uri, reverse = it) },
                                startIcon = Icons.Rounded.RotateLeft,
                                shape = ShapeDefaults.top,
                                resultModifier = Modifier.padding(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            PreferenceRowSwitch(
                                title = stringResource(R.string.boomerang),
                                subtitle = stringResource(R.string.boomerang_sub),
                                checked = item.boomerang,
                                onClick = { component.updateMergeItem(uri, boomerang = it) },
                                startIcon = Icons.Rounded.Repeat,
                                shape = ShapeDefaults.bottom,
                                resultModifier = Modifier.padding(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PreferenceRowSwitch(
            title = stringResource(R.string.normalize_gif_frames),
            subtitle = stringResource(R.string.normalize_gif_frames_sub),
            checked = component.mergeParams.normalizeFrameSizes,
            onClick = {
                component.updateMergeParams(
                    component.mergeParams.copy(normalizeFrameSizes = it)
                )
            },
            startIcon = Icons.Outlined.FitScreen,
            containerColor = Color.Unspecified,
            shape = ShapeDefaults.extraLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = component.mergeParams.transitionDelayMillis,
            icon = Icons.Outlined.Timelapse,
            title = stringResource(R.string.delay_between_clips),
            valueRange = 0f..10_000f,
            internalStateTransformation = { it.roundToInt() },
            onValueChange = {
                component.updateMergeParams(
                    component.mergeParams.copy(transitionDelayMillis = it.roundToInt())
                )
            },
            shape = ShapeDefaults.extraLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        QualitySelector(
            imageFormat = ImageFormat.Jpeg,
            quality = Quality.Base(component.mergeParams.quality),
            onQualityChange = {
                component.updateMergeParams(
                    component.mergeParams.copy(quality = it.qualityValue)
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = component.mergeParams.repeatCount,
            icon = Icons.Rounded.RepeatOne,
            title = stringResource(R.string.repeat_count),
            valueRange = 0f..10f,
            steps = 10,
            valuesPreviewMapping = remember { persistentMapOf(0f to "∞") },
            internalStateTransformation = { it.roundToInt() },
            onValueChange = {
                component.updateMergeParams(
                    component.mergeParams.copy(repeatCount = it.roundToInt())
                )
            },
            shape = ShapeDefaults.extraLarge
        )
    }
}
