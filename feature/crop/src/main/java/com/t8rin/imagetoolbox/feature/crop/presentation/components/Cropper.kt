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

package com.t8rin.imagetoolbox.feature.crop.presentation.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropProperties
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.opencv_tools.free_corners_crop.compose.FreeCornersCropper
import com.yalantis.ucrop.compose.UCropper
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Cropper(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    crop: Boolean,
    onImageCropStarted: () -> Unit,
    onImageCropFinished: (Bitmap?) -> Unit,
    cropType: CropType,
    coercePointsToImageArea: Boolean,
    rotationState: MutableFloatState,
    cropProperties: CropProperties,
    addVerticalInsets: Boolean
) {
    LaunchedEffect(crop) {
        if (crop) onImageCropStarted()
    }
    AnimatedContent(
        targetState = cropType,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { type ->
        when (type) {
            CropType.Default -> {
                val scope = rememberCoroutineScope()
                UCropper(
                    imageModel = bitmap,
                    aspectRatio = if (cropProperties.aspectRatio != AspectRatio.Original) {
                        cropProperties.aspectRatio.value
                    } else null,
                    modifier = Modifier.transparencyChecker(),
                    containerModifier = Modifier.fillMaxSize(),
                    hapticsStrength = LocalSettingsState.current.hapticsStrength,
                    croppingTrigger = crop,
                    onCropped = {
                        scope.launch {
                            BitmapFactory.decodeFile(it.toFile().absolutePath)
                                .apply(onImageCropFinished)
                        }
                    },
                    isOverlayDraggable = true,
                    rotationAngleState = rotationState,
                    onLoadingStateChange = {
                        if (it) onImageCropStarted()
                        else onImageCropFinished(null)
                    },
                    contentPadding = WindowInsets.systemBars.union(WindowInsets.displayCutout).let {
                        if (addVerticalInsets) it.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                        else it.only(WindowInsetsSides.Horizontal)
                    }.asPaddingValues()
                )
            }

            CropType.NoRotation -> {
                AnimatedContent(
                    targetState = (cropProperties.aspectRatio != AspectRatio.Original) to bitmap,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = modifier.fillMaxSize(),
                ) { (fixedAspectRatio, bitmap) ->
                    Box {
                        var zoomLevel by remember {
                            mutableFloatStateOf(1f)
                        }
                        val bmp = remember(bitmap) { bitmap.asImageBitmap() }
                        val minDimension = remember(cropProperties.handleSize) {
                            val size = (cropProperties.handleSize * 3).roundToInt()

                            IntSize(size, size)
                        }
                        ImageCropper(
                            backgroundModifier = Modifier.transparencyChecker(),
                            imageBitmap = bmp,
                            contentDescription = null,
                            cropProperties = cropProperties.copy(
                                fixedAspectRatio = fixedAspectRatio,
                                minDimension = minDimension
                            ),
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
                                    .background(
                                        color = MaterialTheme.colorScheme.scrim.copy(0.4f),
                                        shape = ShapeDefaults.circle
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            CropType.FreeCorners -> {
                val settingsState = LocalSettingsState.current

                FreeCornersCropper(
                    bitmap = bitmap,
                    croppingTrigger = crop,
                    onCropped = {
                        onImageCropFinished(it)
                    },
                    coercePointsToImageArea = coercePointsToImageArea,
                    modifier = Modifier.transparencyChecker(),
                    contentPadding = WindowInsets.systemBars.union(WindowInsets.displayCutout)
                        .let {
                            if (addVerticalInsets) it.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                            else it.only(WindowInsetsSides.Horizontal)
                        }
                        .union(
                            WindowInsets(
                                left = 16.dp,
                                top = 16.dp,
                                right = 16.dp,
                                bottom = 16.dp
                            )
                        )
                        .asPaddingValues(),
                    showMagnifier = settingsState.magnifierEnabled
                )
            }
        }
    }
}