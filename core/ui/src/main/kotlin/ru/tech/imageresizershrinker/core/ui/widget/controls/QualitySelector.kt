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

package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Stream
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.QualityHigh
import ru.tech.imageresizershrinker.core.resources.icons.QualityLow
import ru.tech.imageresizershrinker.core.resources.icons.QualityMedium
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun QualitySelector(
    imageFormat: ImageFormat,
    enabled: Boolean = true,
    quality: Quality,
    onQualityChange: (Quality) -> Unit
) {
    val visible = imageFormat.canChangeCompressionValue

    LaunchedEffect(imageFormat, quality) {
        onQualityChange(
            quality.coerceIn(imageFormat)
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
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
                modifier = Modifier.container(RoundedCornerShape(24.dp))
            ) {
                imageFormat.compressionTypes.forEach { type ->
                    val currentIcon by remember(quality) {
                        derivedStateOf {
                            when {
                                imageFormat.isHighQuality(quality.qualityValue) -> Icons.Rounded.QualityHigh
                                imageFormat.isMidQuality(quality.qualityValue) -> Icons.Rounded.QualityMedium
                                else -> Icons.Rounded.QualityLow
                            }
                        }
                    }

                    val isQuality = type is ImageFormat.Companion.CompressionType.Quality
                    val isEffort = type is ImageFormat.Companion.CompressionType.Effort

                    val compressingLiteral = if (isQuality) "%" else ""

                    EnhancedSliderItem(
                        value = when (type) {
                            is ImageFormat.Companion.CompressionType.Effort -> {
                                when (quality) {
                                    is Quality.Base -> quality.qualityValue
                                    is Quality.Jxl -> quality.effort
                                    is Quality.PngLossy -> quality.compressionLevel
                                    is Quality.Heif -> quality.effort
                                }
                            }

                            is ImageFormat.Companion.CompressionType.Quality -> quality.qualityValue
                        },
                        title = if (isQuality) {
                            stringResource(R.string.quality)
                        } else stringResource(R.string.effort),
                        icon = if (isQuality) currentIcon else Icons.Rounded.Stream,
                        valueRange = type.compressionRange.let { it.first.toFloat()..it.last.toFloat() },
                        steps = type.compressionRange.let { it.last - it.first - 1 },
                        internalStateTransformation = {
                            it.toInt().coerceIn(type.compressionRange).toFloat()
                        },
                        onValueChange = {
                            when (type) {
                                is ImageFormat.Companion.CompressionType.Effort -> {
                                    onQualityChange(
                                        when (quality) {
                                            is Quality.Base -> quality.copy(qualityValue = it.toInt())
                                            is Quality.Jxl -> quality.copy(effort = it.toInt())
                                            is Quality.PngLossy -> quality.copy(compressionLevel = it.toInt())
                                            is Quality.Heif -> quality.copy(effort = it.toInt())
                                        }.coerceIn(imageFormat)
                                    )
                                }

                                is ImageFormat.Companion.CompressionType.Quality -> {
                                    onQualityChange(
                                        when (quality) {
                                            is Quality.Base -> quality.copy(qualityValue = it.toInt())
                                            is Quality.Jxl -> quality.copy(qualityValue = it.toInt())
                                            is Quality.PngLossy -> quality.copy(compressionLevel = it.toInt())
                                            is Quality.Heif -> quality.copy(qualityValue = it.toInt())
                                        }.coerceIn(imageFormat)
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
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                    .padding(6.dp)
                            )
                        }
                    }
                }
                AnimatedVisibility(imageFormat is ImageFormat.Jxl) {
                    val jxlQuality = quality as? Quality.Jxl
                    Column {
                        EnhancedSliderItem(
                            value = jxlQuality?.speed ?: 0,
                            title = stringResource(R.string.speed),
                            icon = Icons.Rounded.Speed,
                            valueRange = 0f..4f,
                            steps = 3,
                            internalStateTransformation = {
                                it.toInt().coerceIn(0..4).toFloat()
                            },
                            onValueChange = {
                                jxlQuality?.copy(
                                    speed = it.toInt()
                                )?.coerceIn(imageFormat)?.let(onQualityChange)
                            },
                            behaveAsContainer = false
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.speed_sub,
                                    0, 5
                                ),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp,
                                color = LocalContentColor.current.copy(0.5f),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .container(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                    .padding(6.dp)
                            )
                        }
                        val items = remember {
                            Quality.Channels.entries
                        }
                        ToggleGroupButton(
                            itemCount = items.size,
                            itemContent = {
                                Text(items[it].title)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .container(
                                    shape = RoundedCornerShape(20.dp),
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
                            indexChanged = {
                                jxlQuality?.copy(
                                    channels = Quality.Channels.fromInt(it)
                                )?.coerceIn(imageFormat)?.let(onQualityChange)
                            },
                            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                }
                AnimatedVisibility(imageFormat is ImageFormat.Png.Lossy) {
                    val pngLossyQuality = quality as? Quality.PngLossy
                    EnhancedSliderItem(
                        value = pngLossyQuality?.maxColors ?: 0,
                        title = stringResource(R.string.max_colors_count),
                        icon = Icons.Outlined.ColorLens,
                        valueRange = 2f..1024f,
                        internalStateTransformation = {
                            it.toInt().coerceIn(2..1024).toFloat()
                        },
                        onValueChange = {
                            pngLossyQuality?.copy(
                                maxColors = it.toInt()
                            )?.coerceIn(imageFormat)?.let(onQualityChange)
                        },
                        behaveAsContainer = false
                    )
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

private val Quality.Channels.title
    @Composable
    get() = when (this) {
        Quality.Channels.RGBA -> "RGBA"
        Quality.Channels.RGB -> "RGB"
        Quality.Channels.Monochrome -> stringResource(R.string.monochrome)
    }