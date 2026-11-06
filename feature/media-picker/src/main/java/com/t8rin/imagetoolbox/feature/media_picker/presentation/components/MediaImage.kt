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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.request.maxBitmapSize
import coil3.size.Precision
import coil3.size.Size
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.resources.icons.CheckCircle
import com.t8rin.imagetoolbox.core.resources.icons.Circle
import com.t8rin.imagetoolbox.core.resources.icons.Error
import com.t8rin.imagetoolbox.core.resources.icons.Star
import com.t8rin.imagetoolbox.core.resources.utils.compositeOverSafe
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.buttons.MediaCheckBox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.advancedShadow
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import kotlinx.coroutines.Dispatchers

@Composable
fun MediaImage(
    modifier: Modifier = Modifier,
    media: Media,
    cellSize: Dp,
    thumbnailSize: Int,
    isInSelection: Boolean = true,
    isSelected: Boolean,
    selectionIndex: Int,
    canClick: Boolean,
    onItemClick: (Media) -> Unit,
    onItemLongClick: (Media) -> Unit,
    showFileSize: Boolean = true,
    showFileExtension: Boolean = true,
    uncheckedIcon: Painter? = null,
) {
    val compact = cellSize < CompactMediaCellSize
    val haptics = LocalHapticFeedback.current
    val indication = LocalIndication.current

    val selection = remember { MutableTransitionState(isSelected) }

    selection.targetState = isSelected

    val transition = if (compact && selection.isIdle) null else rememberTransition(selection)

    val badgeScale = (cellSize / 72.dp).coerceIn(0f, 1f)

    val iconScale = (cellSize / 64.dp).coerceIn(0f, 1f)

    val uncheckedColor = White.copy(0.8f)

    val maxInset = minOf(12.dp, cellSize / 8)

    val selectedSize = transition?.animateDp { if (it) maxInset else 0.dp }?.value
        ?: if (isSelected) maxInset else 0.dp

    val scale = if (!compact || media.isFavorite) {
        transition?.animateFloat { if (it) 0.5f else 1f }
    } else null

    val targetScale = if (isSelected) 0.5f else 1f

    var isImageError by remember(media.uri) { mutableStateOf(false) }
    val checkedIcon = if (isImageError) Icons.Rounded.Error else Icons.Rounded.CheckCircle
    val checkboxState = remember {
        MutableTransitionState(
            Triple(
                isSelected,
                if (isSelected) checkedIcon else Icons.Outlined.Circle,
                selectionIndex
            )
        )
    }
    val drawUncheckedIcon = compact && isInSelection && !isSelected &&
            !checkboxState.currentState.first && checkboxState.isIdle && uncheckedIcon != null

    val strokeColor =
        if (isImageError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer

    val rawDevelopSettings =
        if (media.isRaw) LocalSettingsState.current.rawDevelopSettings else null

    var thumbnailCacheKey by remember(media.uri, media.timestamp, rawDevelopSettings) {
        mutableStateOf<MemoryCache.Key?>(null)
    }

    val model = remember(media.uri, media.timestamp, thumbnailSize, compact, rawDevelopSettings) {
        ImageRequest.Builder(appContext)
            .data(media.uri)
            .size(thumbnailSize)
            .precision(Precision.INEXACT)
            .memoryCacheKeyExtra("thumbnail", media.timestamp.toString())
            .placeholderMemoryCacheKey(thumbnailCacheKey)
            .apply {
                rawDevelopSettings?.let { memoryCacheKeyExtra("raw", it.toString()) }
            }
            .fetcherCoroutineContext(ThumbnailDispatcher)
            .decoderCoroutineContext(ThumbnailDispatcher)
            .maxBitmapSize(if (compact) Size(512, 512) else Size(4096, 4096))
            .crossfade(false)
            .allowHardware(false)
            .build()
    }

    Box(
        modifier = modifier
            .then(if (compact) Modifier else Modifier.clip(ShapeDefaults.extraSmall))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(
                if (canClick) {
                    Modifier.combinedClickable(
                        interactionSource = null,
                        indication = indication,
                        hapticFeedbackEnabled = false,
                        onClick = {
                            haptics.longPress()
                            onItemClick(media)
                        },
                        onLongClick = {
                            haptics.longPress()
                            onItemLongClick(media)
                        }
                    )
                } else Modifier
            )
            .aspectRatio(1f)
            .then(
                if (drawUncheckedIcon) Modifier.drawWithCache {
                    val inset = (4.dp * iconScale).roundToPx().toFloat()
                    val side = (24.dp * iconScale).roundToPx().toFloat()
                    val tint = ColorFilter.tint(uncheckedColor)
                    onDrawWithContent {
                        drawContent()
                        translate(
                            left = if (layoutDirection == LayoutDirection.Ltr) inset
                            else size.width - inset - side,
                            top = inset
                        ) {
                            with(uncheckedIcon) {
                                draw(
                                    androidx.compose.ui.geometry.Size(side, side),
                                    colorFilter = tint
                                )
                            }
                        }
                    }
                } else Modifier
            )
    ) {
        if (compact) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .padding(selectedSize)
                    .then(
                        if (isSelected) Modifier
                            .clip(ShapeDefaults.extraSmall)
                            .border(1.dp, strokeColor, ShapeDefaults.extraSmall)
                        else Modifier
                    ),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    isImageError = false
                    thumbnailCacheKey = it.result.memoryCacheKey
                },
                onError = { isImageError = true }
            )
            if (isImageError) MediaThumbnailError()
        } else {
            val shape = animateShape(
                if (isSelected) AutoCornersShape(16.dp) else AutoCornersShape(4.dp)
            )
            Picture(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .padding(selectedSize)
                    .clip(shape)
                    .then(
                        if (isSelected) Modifier.border(2.dp, strokeColor, shape)
                        else Modifier
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    isImageError = false
                    thumbnailCacheKey = it.result.memoryCacheKey
                },
                onError = { isImageError = true },
                error = { MediaThumbnailError() },
                filterQuality = FilterQuality.High,
                shimmerEnabled = thumbnailCacheKey == null
            )
        }

        if (!compact && showFileExtension) {
            MediaBadge(
                text = media.fileExtension.uppercase(),
                scale = badgeScale,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(selectedSize / 2)
                    .graphicsLayer {
                        scaleX = scale?.value ?: targetScale
                        scaleY = scale?.value ?: targetScale
                    }
            )
        }

        val showSize = !compact && showFileSize && media.fileSize > 0
        if (showSize || media.isFavorite) {
            val footerScale = if (compact) iconScale else badgeScale
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(selectedSize / 2)
                    .padding(horizontal = 8.dp * footerScale, vertical = 10.dp * footerScale),
                horizontalArrangement = if (showSize) Arrangement.SpaceBetween else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showSize) {
                    val style = MaterialTheme.typography.labelMedium
                    val fontSize = style.fontSize * badgeScale
                    AutoSizeText(
                        text = media.humanFileSize,
                        style = style.copy(
                            color = White,
                            fontSize = fontSize,
                            lineHeight = TextUnit.Unspecified
                        ),
                        maxLines = 1,
                        softWrap = false,
                        minFontSize = minOf(6f, fontSize.value).sp,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .graphicsLayer {
                                scaleX = scale?.value ?: targetScale
                                scaleY = scale?.value ?: targetScale
                                transformOrigin = TransformOrigin(0f, 0.5f)
                            }
                            .mediaBadgeShadow(footerScale)
                    )
                }
                if (media.isFavorite) {
                    if (showSize) Spacer(Modifier.width(4.dp * footerScale))
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale?.value ?: targetScale
                                scaleY = scale?.value ?: targetScale
                                transformOrigin = TransformOrigin(1f, 0.5f)
                            }
                            .mediaBadgeShadow(footerScale)
                            .size(16.dp * iconScale)
                    )
                }
            }
        }

        if (isInSelection && !drawUncheckedIcon) {
            val checkboxSize = 24.dp * iconScale
            val selectionModifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp * iconScale)
                .size(checkboxSize)
            val checkedColor = if (isImageError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary

            val checkedBackground = MaterialTheme.colorScheme.surfaceContainer
            val background = transition?.animateColor {
                if (it) checkedBackground else Color.Transparent
            }?.value ?: if (isSelected) checkedBackground else Color.Transparent

            MediaCheckBox(
                isChecked = isSelected,
                uncheckedColor = uncheckedColor,
                checkedColor = checkedColor,
                checkedIcon = checkedIcon,
                selectionIndex = selectionIndex,
                numberSize = checkboxSize,
                uncheckedIcon = uncheckedIcon,
                transitionState = checkboxState,
                modifier = selectionModifier
                    .clip(ShapeDefaults.circle)
                    .background(background)
            )
        }
    }
}

internal val CompactMediaCellSize = 48.dp

private val ThumbnailDispatcher = Dispatchers.IO.limitedParallelism(4)

@Composable
private fun MediaThumbnailError() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                takeColorFromScheme { isNightMode ->
                    errorContainer.copy(if (isNightMode) 0.25f else 1f).compositeOverSafe(surface)
                }
            )
    ) {
        Icon(
            imageVector = Icons.Rounded.BrokenImageAlt,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.5f),
            tint = MaterialTheme.colorScheme.onErrorContainer.copy(0.8f)
        )
    }
}

@Composable
private fun MediaBadge(
    text: String,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val style = MaterialTheme.typography.labelMedium
    Text(
        text = text,
        modifier = modifier.mediaBadge(scale),
        color = White,
        style = style,
        fontSize = style.fontSize * scale,
        lineHeight = style.lineHeight * scale,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis
    )
}

private fun Modifier.mediaBadge(scale: Float): Modifier = this
    .padding(8.dp * scale)
    .padding(vertical = 2.dp * scale)
    .mediaBadgeShadow(scale)
    .padding(horizontal = 2.dp * scale)

private fun Modifier.mediaBadgeShadow(scale: Float): Modifier = advancedShadow(
    cornersRadius = 4.dp * scale,
    shadowBlurRadius = 6.dp * scale,
    alpha = 0.4f
)