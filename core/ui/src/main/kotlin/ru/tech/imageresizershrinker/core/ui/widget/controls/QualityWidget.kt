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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.QualityHigh
import ru.tech.imageresizershrinker.core.ui.icons.material.QualityLow
import ru.tech.imageresizershrinker.core.ui.icons.material.QualityMedium
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

//TODO: Add jxl speed handling

@Composable
fun QualityWidget(
    imageFormat: ImageFormat,
    enabled: Boolean,
    quality: Float,
    onQualityChange: (Float) -> Unit
) {
    val visible = imageFormat.canChangeCompressionValue

    val isQuality = imageFormat.compressionType is ImageFormat.Companion.CompressionType.Quality
    val isEffort = imageFormat.compressionType is ImageFormat.Companion.CompressionType.Effort

    val compressingLiteral = if (isQuality) "%" else ""

    val currentIcon by remember(quality) {
        derivedStateOf {
            when {
                imageFormat.isHighQuality(quality) -> Icons.Rounded.QualityHigh
                imageFormat.isMidQuality(quality) -> Icons.Rounded.QualityMedium
                else -> Icons.Rounded.QualityLow
            }
        }
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
            EnhancedSliderItem(
                value = quality,
                title = if (!isEffort) {
                    stringResource(R.string.quality)
                } else stringResource(R.string.effort),
                icon = currentIcon,
                valueRange = imageFormat.compressionRange.let { it.first.toFloat()..it.last.toFloat() },
                steps = imageFormat.compressionRange.let { it.last - it.first - 1 },
                internalStateTransformation = {
                    it.toInt().coerceIn(imageFormat.compressionRange).toFloat()
                },
                onValueChange = {
                    onQualityChange(it)
                },
                valueSuffix = " $compressingLiteral",
                shape = RoundedCornerShape(24.dp)
            ) {
                AnimatedVisibility(isEffort) {
                    Text(
                        text = stringResource(
                            R.string.effort_sub,
                            imageFormat.compressionRange.first,
                            imageFormat.compressionRange.last
                        ),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp,
                        color = LocalContentColor.current.copy(0.5f),
                        modifier = Modifier
                            .padding(4.dp)
                            .container(RoundedCornerShape(20.dp))
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

private fun ImageFormat.isHighQuality(quality: Float): Boolean {
    val range = compressionRange.run { endInclusive - start }
    return quality > range * (4 / 5f)
}

private fun ImageFormat.isMidQuality(quality: Float): Boolean {
    val range = compressionRange.run { endInclusive - start }
    return quality > range * (2 / 5f)
}