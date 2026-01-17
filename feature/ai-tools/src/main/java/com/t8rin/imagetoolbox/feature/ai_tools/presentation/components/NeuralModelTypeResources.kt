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

package com.t8rin.imagetoolbox.feature.ai_tools.presentation.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Scanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.resources.icons.Eraser
import com.t8rin.imagetoolbox.core.resources.icons.Eyedropper
import com.t8rin.imagetoolbox.core.resources.icons.Jpg
import com.t8rin.imagetoolbox.core.resources.icons.Manga
import com.t8rin.imagetoolbox.core.resources.icons.NoiseAlt
import com.t8rin.imagetoolbox.core.resources.icons.Rabbit
import com.t8rin.imagetoolbox.core.resources.icons.Snail
import com.t8rin.imagetoolbox.core.resources.icons.Tortoise
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralConstants
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import java.io.File
import kotlin.random.Random

fun NeuralModel.Type.title(): Int = when (this) {
    NeuralModel.Type.DE_JPEG -> R.string.type_dejpeg
    NeuralModel.Type.DENOISE -> R.string.type_denoise
    NeuralModel.Type.COLORIZE -> R.string.type_colorize
    NeuralModel.Type.ARTIFACTS -> R.string.type_artifacts
    NeuralModel.Type.ENHANCE -> R.string.type_enhance
    NeuralModel.Type.ANIME -> R.string.type_anime
    NeuralModel.Type.SCANS -> R.string.type_scans
    NeuralModel.Type.UPSCALE -> R.string.type_upscale
    NeuralModel.Type.REMOVE_BG -> R.string.type_removebg
}

fun NeuralModel.Type.icon(): ImageVector = when (this) {
    NeuralModel.Type.DE_JPEG -> Icons.Outlined.Jpg
    NeuralModel.Type.DENOISE -> Icons.Outlined.NoiseAlt
    NeuralModel.Type.COLORIZE -> Icons.Outlined.Eyedropper
    NeuralModel.Type.ARTIFACTS -> Icons.Rounded.BrokenImageAlt
    NeuralModel.Type.ENHANCE -> Icons.Rounded.AutoFixHigh
    NeuralModel.Type.ANIME -> Icons.Rounded.Manga
    NeuralModel.Type.SCANS -> Icons.Rounded.Scanner
    NeuralModel.Type.UPSCALE -> Icons.Rounded.HighQuality
    NeuralModel.Type.REMOVE_BG -> Icons.Rounded.Eraser
}

fun NeuralModel.Speed.icon(): ImageVector = when (this) {
    is NeuralModel.Speed.VeryFast -> Icons.Rounded.Bolt
    is NeuralModel.Speed.Fast -> Icons.Rounded.Rabbit
    is NeuralModel.Speed.Normal -> Icons.AutoMirrored.Rounded.DirectionsWalk
    is NeuralModel.Speed.Slow -> Icons.Rounded.Tortoise
    is NeuralModel.Speed.VerySlow -> Icons.Rounded.Snail
}

fun NeuralModel.Speed.title(): Int = when (this) {
    is NeuralModel.Speed.VeryFast -> R.string.very_fast
    is NeuralModel.Speed.Fast -> R.string.fast
    is NeuralModel.Speed.Normal -> R.string.normal
    is NeuralModel.Speed.Slow -> R.string.slow
    is NeuralModel.Speed.VerySlow -> R.string.very_slow
}

@Composable
fun NeuralModelTypeBadge(
    type: NeuralModel.Type,
    isInverted: Boolean?,
    modifier: Modifier = Modifier,
    height: Dp = 22.dp,
    endPadding: Dp = 6.dp,
    startPadding: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
    style: TextStyle = MaterialTheme.typography.labelSmall
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(
        modifier = modifier
            .height(height)
            .container(
                color = takeColorFromScheme {
                    when (isInverted) {
                        true -> tertiary.blend(
                            color = secondary,
                            fraction = 0.3f
                        )

                        false -> tertiaryContainer.blend(
                            color = secondaryContainer,
                            fraction = 0.3f
                        )

                        null -> surfaceVariant.blend(
                            color = secondaryContainer,
                            fraction = 0.3f
                        )
                    }
                },
                shape = shapeByInteraction(
                    shape = CircleShape,
                    pressedShape = ShapeDefaults.pressed,
                    interactionSource = interactionSource
                ),
                resultPadding = 0.dp
            )
            .then(
                if (onClick != null) {
                    Modifier.hapticsClickable(
                        indication = LocalIndication.current,
                        onClick = onClick,
                        interactionSource = interactionSource
                    )
                } else {
                    Modifier
                }
            )
            .padding(start = startPadding, end = endPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val contentColor = takeColorFromScheme {
            when (isInverted) {
                true -> onTertiary.blend(
                    color = onSecondary,
                    fraction = 0.65f
                )

                false -> onTertiaryContainer.blend(
                    color = onSecondaryContainer,
                    fraction = 0.65f
                )

                null -> onSurfaceVariant.blend(
                    color = onSecondaryContainer,
                    fraction = 0.65f
                )
            }
        }

        Box(
            modifier = Modifier.size((height - 2.dp).coerceAtMost(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            )
        }
        Text(
            text = stringResource(type.title()),
            color = contentColor,
            style = style
        )
    }
}

@Composable
fun NeuralModelSpeedBadge(
    speed: NeuralModel.Speed,
    isInverted: Boolean?,
    modifier: Modifier = Modifier,
    height: Dp = 22.dp,
    endPadding: Dp = 6.dp,
    startPadding: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
    showTitle: Boolean = false,
    style: TextStyle = MaterialTheme.typography.labelSmall
) {
    val hasValue = showTitle || speed.speedValue > 0f

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(
        modifier = modifier
            .then(
                if (hasValue) {
                    Modifier.height(height)
                } else {
                    Modifier.size(height)
                }
            )
            .container(
                color = takeColorFromScheme {
                    when (isInverted) {
                        true -> primary
                        false -> primaryContainer
                        null -> surfaceVariant.blend(
                            color = primaryContainer,
                            fraction = 0.2f
                        )
                    }
                },
                shape = shapeByInteraction(
                    shape = CircleShape,
                    pressedShape = ShapeDefaults.pressed,
                    interactionSource = interactionSource
                ),
                resultPadding = 0.dp
            )
            .then(
                if (onClick != null) {
                    Modifier.hapticsClickable(
                        indication = LocalIndication.current,
                        onClick = onClick,
                        interactionSource = interactionSource
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (hasValue) {
                    Modifier.padding(start = startPadding, end = endPadding)
                } else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
    ) {
        val contentColor = takeColorFromScheme {
            when (isInverted) {
                true -> onPrimary
                false -> onPrimaryContainer
                null -> onSurfaceVariant.blend(
                    color = onPrimaryContainer,
                    fraction = 0.3f
                )
            }
        }
        Box(
            modifier = Modifier.size((height - 2.dp).coerceAtMost(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = speed.icon(),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            )
        }
        if (hasValue) {
            val speedValue by remember(speed.speedValue) {
                derivedStateOf {
                    speed.speedValue.roundTo(
                        when {
                            speed.speedValue > 10f -> 1
                            speed.speedValue > 5f -> 2
                            else -> 3
                        }
                    ).toString().trimTrailingZero()
                }
            }

            Text(
                text = if (showTitle) {
                    stringResource(speed.title())
                } else {
                    speedValue
                },
                color = contentColor,
                style = style
            )
        }
    }
}

@Composable
fun NeuralModelSizeBadge(
    model: NeuralModel,
    isInverted: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(22.dp)
            .container(
                color = takeColorFromScheme {
                    if (isInverted) surface
                    else surfaceVariant
                },
                shape = CircleShape,
                resultPadding = 0.dp
            )
            .padding(start = 4.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val contentColor = takeColorFromScheme {
            if (isInverted) onSurface
            else onSurfaceVariant
        }

        val context = LocalContext.current
        val modelFile by remember(context, model.name) {
            derivedStateOf {
                File(File(context.filesDir, NeuralConstants.DIR), model.name)
            }
        }
        val size by remember(model.downloadSize, modelFile) {
            derivedStateOf {
                modelFile.length().takeIf { it > 0 }?.let(::humanFileSize)
                    ?: humanFileSize(model.downloadSize)
            }
        }

        Box(
            modifier = Modifier.size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (modelFile.exists()) {
                    Icons.AutoMirrored.Rounded.InsertDriveFile
                } else {
                    Icons.Rounded.Cloud
                },
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = size,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview
@Composable
private fun PreviewSpeed() = ImageToolboxThemeForPreview(
    isDarkTheme = true,
    keyColor = Color.Green
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        NeuralModel.Speed.entries.forEach {
            NeuralModelSpeedBadge(
                speed = it.clone(12.21f),
                isInverted = Random.nextBoolean(),
                height = 36.dp,
                endPadding = 12.dp,
                startPadding = 6.dp,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview
@Composable
private fun PreviewType() = ImageToolboxThemeForPreview(
    isDarkTheme = true,
    keyColor = Color.Green
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        NeuralModel.Type.entries.forEach {
            NeuralModelTypeBadge(
                type = it,
                isInverted = Random.nextBoolean(),
                height = 36.dp,
                endPadding = 12.dp,
                startPadding = 6.dp,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview
@Composable
private fun PreviewMixed() = ImageToolboxThemeForPreview(
    isDarkTheme = true,
    keyColor = Color.Green
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        NeuralModel.Type.entries.forEach {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val inv = Random.nextBoolean()
                NeuralModelTypeBadge(it, inv)

                NeuralModelSpeedBadge(
                    speed = (NeuralModel.Speed.entries.getOrNull(it.ordinal)
                        ?: NeuralModel.Speed.entries.random())
                        .clone(Random.nextFloat() * 20), inv
                )

                NeuralModelSizeBadge(
                    model = NeuralModel.entries.first(),
                    isInverted = inv
                )
            }
        }
    }
}