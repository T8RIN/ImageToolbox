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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.rounded.Stream
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.TiffCompressionScheme
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.QualityHigh
import com.t8rin.imagetoolbox.core.resources.icons.QualityLow
import com.t8rin.imagetoolbox.core.resources.icons.QualityMedium
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun QualitySelector(
    imageFormat: ImageFormat,
    enabled: Boolean = true,
    quality: Quality,
    onQualityChange: (Quality) -> Unit
) {
    var actualImageFormat by remember {
        mutableStateOf(imageFormat)
    }

    LaunchedEffect(imageFormat, quality) {
        if (
            actualImageFormat.canChangeCompressionValue == imageFormat.canChangeCompressionValue
            || !actualImageFormat.canChangeCompressionValue
        ) {
            actualImageFormat = imageFormat
        } else {
            launch {
                delay(1000)
            }.invokeOnCompletion {
                actualImageFormat = imageFormat
            }
        }
        onQualityChange(
            quality.coerceIn(imageFormat)
        )
    }

    AnimatedVisibility(
        visible = imageFormat.canChangeCompressionValue,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = Modifier.fillMaxWidth()
    ) {
        ProvideTextStyle(
            value = TextStyle(
                color = if (!enabled) {
                    MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.38f)
                        .compositeOver(MaterialTheme.colorScheme.surface)
                } else Color.Unspecified
            )
        ) {
            Column(
                modifier = Modifier.container(ShapeDefaults.extraLarge)
            ) {
                actualImageFormat.compressionTypes.forEach { type ->
                    val currentIcon by remember(quality) {
                        derivedStateOf {
                            when {
                                actualImageFormat.isHighQuality(quality.qualityValue) -> Icons.Rounded.QualityHigh
                                actualImageFormat.isMidQuality(quality.qualityValue) -> Icons.Rounded.QualityMedium
                                else -> Icons.Rounded.QualityLow
                            }
                        }
                    }

                    val isQuality = type is ImageFormat.CompressionType.Quality
                    val isEffort = type is ImageFormat.CompressionType.Effort

                    val compressingLiteral = if (isQuality) "%" else ""

                    EnhancedSliderItem(
                        value = when (type) {
                            is ImageFormat.CompressionType.Effort -> {
                                when (quality) {
                                    is Quality.Base -> quality.qualityValue
                                    is Quality.Jxl -> quality.effort
                                    is Quality.PngLossy -> quality.compressionLevel
                                    is Quality.Avif -> quality.effort
                                    is Quality.Tiff -> quality.qualityValue
                                }
                            }

                            is ImageFormat.CompressionType.Quality -> quality.qualityValue
                        },
                        title = if (isQuality) {
                            stringResource(R.string.quality)
                        } else stringResource(R.string.effort),
                        icon = if (isQuality) currentIcon else Icons.Rounded.Stream,
                        valueRange = type.compressionRange.let { it.first.toFloat()..it.last.toFloat() },
                        steps = type.compressionRange.let { it.last - it.first - 1 },
                        internalStateTransformation = {
                            it.roundToInt().coerceIn(type.compressionRange).toFloat()
                        },
                        onValueChange = {
                            when (type) {
                                is ImageFormat.CompressionType.Effort -> {
                                    onQualityChange(
                                        when (quality) {
                                            is Quality.Base -> quality.copy(qualityValue = it.toInt())
                                            is Quality.Jxl -> quality.copy(effort = it.toInt())
                                            is Quality.PngLossy -> quality.copy(compressionLevel = it.toInt())
                                            is Quality.Avif -> quality.copy(effort = it.toInt())
                                            is Quality.Tiff -> quality.copy(compressionScheme = it.toInt())
                                        }.coerceIn(actualImageFormat)
                                    )
                                }

                                is ImageFormat.CompressionType.Quality -> {
                                    onQualityChange(
                                        when (quality) {
                                            is Quality.Base -> quality.copy(qualityValue = it.toInt())
                                            is Quality.Jxl -> quality.copy(qualityValue = it.toInt())
                                            is Quality.PngLossy -> quality.copy(compressionLevel = it.toInt())
                                            is Quality.Avif -> quality.copy(qualityValue = it.toInt())
                                            is Quality.Tiff -> quality.copy(compressionScheme = it.toInt())
                                        }.coerceIn(actualImageFormat)
                                    )
                                }
                            }
                        },
                        valueSuffix = " $compressingLiteral",
                        behaveAsContainer = false
                    ) {
                        AnimatedVisibility(isEffort) {
                            Text(
                                text = stringResource(
                                    R.string.effort_sub,
                                    type.compressionRange.first,
                                    type.compressionRange.last
                                ),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp,
                                color = LocalContentColor.current.copy(0.5f),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .container(
                                        shape = ShapeDefaults.large,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                    .padding(6.dp)
                            )
                        }
                    }
                }
                AnimatedVisibility(actualImageFormat is ImageFormat.Jxl) {
                    val jxlQuality = quality as? Quality.Jxl
                    Column {
                        EnhancedSliderItem(
                            value = jxlQuality?.speed ?: 0,
                            title = stringResource(R.string.speed),
                            icon = Icons.Outlined.Speed,
                            valueRange = 0f..4f,
                            steps = 3,
                            internalStateTransformation = {
                                it.roundToInt().coerceIn(0..4).toFloat()
                            },
                            onValueChange = {
                                jxlQuality?.copy(
                                    speed = it.roundToInt()
                                )?.coerceIn(actualImageFormat)?.let(onQualityChange)
                            },
                            behaveAsContainer = false
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.speed_sub,
                                    0, 4
                                ),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp,
                                color = LocalContentColor.current.copy(0.5f),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .container(
                                        shape = ShapeDefaults.large,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                    .padding(6.dp)
                            )
                        }
                        val items = remember {
                            Quality.Channels.entries
                        }
                        EnhancedButtonGroup(
                            itemCount = items.size,
                            itemContent = {
                                Text(items[it].title)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .container(
                                    shape = ShapeDefaults.large,
                                    color = MaterialTheme.colorScheme.surface
                                )
                                .padding(4.dp),
                            title = {
                                Text(
                                    text = stringResource(R.string.channels_configuration),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            },
                            selectedIndex = items.indexOfFirst { it == jxlQuality?.channels },
                            onIndexChange = {
                                jxlQuality?.copy(
                                    channels = Quality.Channels.fromInt(it)
                                )?.coerceIn(actualImageFormat)?.let(onQualityChange)
                            },
                            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                }
                AnimatedVisibility(actualImageFormat is ImageFormat.Png.Lossy) {
                    val pngLossyQuality = quality as? Quality.PngLossy
                    EnhancedSliderItem(
                        value = pngLossyQuality?.maxColors ?: 0,
                        title = stringResource(R.string.max_colors_count),
                        icon = Icons.Outlined.ColorLens,
                        valueRange = 2f..1024f,
                        internalStateTransformation = {
                            it.roundToInt().coerceIn(2..1024).toFloat()
                        },
                        onValueChange = {
                            pngLossyQuality?.copy(
                                maxColors = it.roundToInt()
                            )?.coerceIn(actualImageFormat)?.let(onQualityChange)
                        },
                        behaveAsContainer = false
                    )
                }
                AnimatedVisibility(actualImageFormat is ImageFormat.Tiff || actualImageFormat is ImageFormat.Tif) {
                    val tiffQuality = quality as? Quality.Tiff
                    val compressionItems = TiffCompressionScheme.safeEntries
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TitleItem(
                            text = stringResource(R.string.tiff_compression_scheme),
                            modifier = Modifier
                                .padding(top = 12.dp, start = 12.dp, bottom = 8.dp, end = 12.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .container(
                                    shape = ShapeDefaults.large,
                                    color = MaterialTheme.colorScheme.surface
                                )
                        ) {
                            val state = rememberLazyStaggeredGridState()
                            LazyHorizontalStaggeredGrid(
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 8.dp,
                                    alignment = Alignment.CenterVertically
                                ),
                                state = state,
                                horizontalItemSpacing = 8.dp,
                                rows = StaggeredGridCells.Adaptive(30.dp),
                                modifier = Modifier
                                    .heightIn(max = 100.dp)
                                    .fadingEdges(
                                        scrollableState = state,
                                        isVertical = false,
                                        spanCount = 2
                                    ),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(compressionItems) {
                                    val selected by remember(it, tiffQuality?.compressionScheme) {
                                        derivedStateOf {
                                            tiffQuality?.compressionScheme == it.ordinal
                                        }
                                    }
                                    EnhancedChip(
                                        selected = selected,
                                        onClick = {
                                            tiffQuality?.copy(
                                                compressionScheme = it.ordinal
                                            )?.coerceIn(actualImageFormat)?.let(onQualityChange)
                                        },
                                        selectedColor = MaterialTheme.colorScheme.tertiary,
                                        contentPadding = PaddingValues(
                                            horizontal = 12.dp,
                                            vertical = 8.dp
                                        ),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        AutoSizeText(
                                            text = compressionItems[it.ordinal].title,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ImageFormat.isHighQuality(quality: Int): Boolean {
    val range = compressionTypes[0].compressionRange.run { endInclusive - start }
    return quality > range * (4 / 5f)
}

private fun ImageFormat.isMidQuality(quality: Int): Boolean {
    val range = compressionTypes[0].compressionRange.run { endInclusive - start }
    return quality > range * (2 / 5f)
}

private val TiffCompressionScheme.title: String
    get() = when (this) {
        TiffCompressionScheme.CCITTRLE -> "RLE"
        TiffCompressionScheme.CCITTFAX3 -> "FAX 3"
        TiffCompressionScheme.CCITTFAX4 -> "FAX 4"
        TiffCompressionScheme.ADOBE_DEFLATE -> "ADOBE DEFLATE"
        else -> this.name
    }

private val Quality.Channels.title
    @Composable
    get() = when (this) {
        Quality.Channels.RGBA -> "RGBA"
        Quality.Channels.RGB -> "RGB"
        Quality.Channels.Monochrome -> stringResource(R.string.monochrome)
    }