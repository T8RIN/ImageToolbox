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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FitScreen
import com.t8rin.imagetoolbox.core.resources.icons.Repeat
import com.t8rin.imagetoolbox.core.resources.icons.RepeatOne
import com.t8rin.imagetoolbox.core.resources.icons.RotateLeft
import com.t8rin.imagetoolbox.core.resources.icons.Timelapse
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
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
        type.gifUris.orEmpty().forEach { uri ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .container(ShapeDefaults.large)
                    .padding(8.dp)
            ) {
                val item = component.mergeItem(uri)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Picture(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.size(42.dp)
                    )
                    Text(
                        text = rememberFilename(uri) ?: uri.lastPathSegment.orEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PreferenceRowSwitch(
                        title = stringResource(R.string.reverse),
                        subtitle = stringResource(R.string.reverse_sub),
                        checked = item.reverse,
                        onClick = { component.updateMergeItem(uri, reverse = it) },
                        startIcon = Icons.Rounded.RotateLeft,
                        containerColor = MaterialTheme.colorScheme.surface,
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
                        containerColor = MaterialTheme.colorScheme.surface,
                        shape = ShapeDefaults.bottom,
                        resultModifier = Modifier.padding(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
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
        Spacer(modifier = Modifier.height(16.dp))
    }
}
