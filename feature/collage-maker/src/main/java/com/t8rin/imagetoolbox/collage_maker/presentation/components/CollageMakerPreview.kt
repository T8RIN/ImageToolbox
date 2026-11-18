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

package com.t8rin.imagetoolbox.collage_maker.presentation.components

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.DrawableCompat
import com.t8rin.collages.components.Collage
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.core.data.image.utils.createShader
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Add
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.SwapHoriz
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import net.engawapg.lib.zoomable.ZoomDefaults
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun CollageMakerPreview(
    component: CollageMakerComponent,
    isPortrait: Boolean,
    isLoading: Boolean,
    resettingTrigger: Int,
    onLoadingChange: (Boolean) -> Unit,
    onOutputSizeChanged: (Int, Int) -> Unit,
    onEditSlot: (Int) -> Unit
) {
    var imagesReady by remember(
        component.uris,
        component.collageType.layoutId,
        resettingTrigger,
        component.collageResetKey
    ) {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(20.dp)
    ) {
        var infoHeight by remember(isPortrait) {
            mutableStateOf(0.dp)
        }
        val zoomState = rememberZoomState()
        val infoAlpha by animateFloatAsState(
            targetValue = if (zoomState.scale > 1.01f) 0f else 1f,
            animationSpec = tween(200)
        )
        var tapIndex by remember { mutableIntStateOf(-1) }
        var showItemMenu by remember { mutableStateOf(false) }

        val singlePicker = rememberImagePicker { uri: Uri ->
            component.replaceImageAt(
                index = tapIndex,
                uri = uri
            )
        }

        val addPicker = rememberImagePicker { uris: List<Uri> ->
            component.addImages(uris)
        }

        LaunchedEffect(resettingTrigger, component.collageResetKey) {
            showItemMenu = false
            tapIndex = -1
            zoomState.reset()
        }

        Column(Modifier.fillMaxSize()) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = if (isPortrait) 20.dp else 0.dp),
                contentAlignment = Alignment.Center
            ) {
                val ratio = component.aspectRatio.value
                val previewWidth = minOf(maxWidth, maxHeight * ratio)
                AnimatedContent(
                    targetState = resettingTrigger to component.collageResetKey,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { trigger ->
                    key(trigger) {
                        Box(
                            modifier = Modifier
                                .size(previewWidth, previewWidth / ratio)
                                .zoomable(
                                    zoomState = zoomState,
                                    zoomEnabled = !showItemMenu
                                )
                                .container(
                                    shape = ShapeDefaults.extraSmall,
                                    resultPadding = 0.dp
                                )
                                .shimmer(visible = isLoading || !imagesReady),
                            contentAlignment = Alignment.Center
                        ) {
                            Collage(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(ShapeDefaults.extraSmall)
                                    .transparencyChecker(),
                                images = component.uris ?: emptyList(),
                                collageType = component.collageType,
                                collageCreationTrigger = component.collageCreationTrigger,
                                onCollageCreated = {
                                    component.updateCollageBitmap(it)
                                    onLoadingChange(false)
                                },
                                onCollageCreationFailed = component::onCollageCreationFailed,
                                backgroundColor = component.backgroundColor,
                                backgroundShader = remember(component.backgroundGradient) {
                                    component.backgroundGradient?.let { gradient ->
                                        { width: Float, height: Float ->
                                            gradient.createShader(width, height)
                                        }
                                    }
                                },
                                spacing = component.params.spacing,
                                cornerRadius = component.params.cornerRadius,
                                aspectRatio = component.aspectRatio.value,
                                outputScaleRatio = component.params.outputScaleRatio,
                                onOutputSizeChanged = onOutputSizeChanged,
                                imageGesturesEnabled = showItemMenu,
                                maxImageZoom = ZoomDefaults.MaxZoom,
                                canvasScale = zoomState.scale,
                                outerPadding = component.params.outerPadding,
                                slotStyles = component.uris.orEmpty().indices.map(component::slotStyle),
                                slotBorderShaders = remember(
                                    component.params.borderGradient,
                                    component.params.slotBorderGradients,
                                    component.uris?.size
                                ) {
                                    component.uris.orEmpty().indices.map { index ->
                                        component.slotBorderGradient(index)
                                            ?.let { gradient ->
                                                { width: Float, height: Float ->
                                                    gradient.createShader(width, height)
                                                }
                                            }
                                    }
                                },
                                disableRotation = component.params.disableRotation,
                                enableSnapToBorders = component.params.enableSnapToBorders,
                                state = component.collageState,
                                onStateReady = component::updateCollageState,
                                onImagesLoaded = {
                                    imagesReady = true
                                    onLoadingChange(false)
                                },
                                onUserStateChange = component::onCollageStateChange,
                                onImageTap = { index ->
                                    if (index >= 0) {
                                        tapIndex = index
                                        showItemMenu = true
                                    } else {
                                        showItemMenu = false
                                    }
                                },
                                handleDrawable = rememberHandleDrawable()
                            )
                        }
                    }
                }
            }
            if (isPortrait) {
                val density = LocalDensity.current
                InfoContainer(
                    modifier = Modifier
                        .graphicsLayer { alpha = infoAlpha }
                        .onSizeChanged {
                            infoHeight = with(density) { it.height.toDp() }
                        },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    text = stringResource(R.string.collages_info)
                )
            }
        }
        AnimatedVisibility(
            visible = showItemMenu,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = infoHeight + 12.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .container(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = ShapeDefaults.large,
                        resultPadding = 0.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                EnhancedIconButton(onClick = {
                    onEditSlot(tapIndex)
                }) {
                    Icon(Icons.Rounded.Tune, contentDescription = null)
                }
                EnhancedIconButton(
                    onClick = {
                        showItemMenu = false
                        singlePicker.pickImage()
                    },
                    enabled = tapIndex >= 0
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SwapHoriz,
                        contentDescription = "Swap"
                    )
                }
                EnhancedIconButton(
                    onClick = {
                        showItemMenu = false
                        addPicker.pickImage()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add"
                    )
                }
                EnhancedIconButton(
                    onClick = {
                        showItemMenu = false
                        component.removeImageAt(tapIndex)
                    },
                    enabled = tapIndex >= 0
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberHandleDrawable(): Drawable {
    val context = LocalContext.current

    val width = with(LocalDensity.current) { 42.dp.roundToPx() }
    val height = width / 2
    val backgroundColor = MaterialTheme.colorScheme.tertiary
    val iconColor = MaterialTheme.colorScheme.onTertiary

    return remember(width, backgroundColor, iconColor, context) {
        val container = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.Transparent.toArgb())
            setSize(width, width)
        }

        val box = GradientDrawable().apply {
            mutate()
            shape = GradientDrawable.RECTANGLE
            cornerRadius = height / 2f
            setColor(backgroundColor.toArgb())
            setSize(width, height)
        }
        val icon = AppCompatResources
            .getDrawable(context, R.drawable.outline_drag_handle_24)!!
            .mutate()

        DrawableCompat.setTint(icon, iconColor.toArgb())

        LayerDrawable(arrayOf(container, box, icon)).apply {
            val insetH = 0
            val insetV = (width - height) / 2
            val inset = (width * 0.2f).toInt()

            setLayerInset(0, 0, 0, 0, 0)
            setLayerInset(1, insetH, insetV, insetH, insetV)
            setLayerInset(2, inset, inset, inset, inset)
            setBounds(0, 0, width, height)
        }
    }
}