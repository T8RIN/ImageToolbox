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

package ru.tech.imageresizershrinker.feature.crop.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropProperties
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility

@Composable
fun Cropper(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    crop: Boolean,
    onImageCropStarted: () -> Unit,
    onImageCropFinished: (Bitmap) -> Unit,
    cropProperties: CropProperties
) {
    Column {
        AnimatedContent(
            targetState = (cropProperties.aspectRatio != AspectRatio.Original) to bitmap,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { (fixedAspectRatio, bitmap) ->
            Box {
                var zoomLevel by remember {
                    mutableFloatStateOf(1f)
                }
                val bmp = remember(bitmap) { bitmap.asImageBitmap() }
                ImageCropper(
                    backgroundModifier = Modifier.transparencyChecker(),
                    imageBitmap = bmp,
                    contentDescription = null,
                    cropProperties = cropProperties.copy(fixedAspectRatio = fixedAspectRatio),
                    onCropStart = onImageCropStarted,
                    crop = crop,
                    cropStyle = CropDefaults.style(
                        overlayColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onZoomChange = { newZoom ->
                        zoomLevel = newZoom
                    },
                    onCropSuccess = { image ->
                        onImageCropFinished(image.asAndroidBitmap())
                    }
                )
                BoxAnimatedVisibility(
                    visible = zoomLevel > 1f,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Text(
                        text = stringResource(R.string.zoom) + " ${zoomLevel.roundToTwoDigits()}x",
                        modifier = Modifier
                            .background(Color.Black.copy(0.4f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
}