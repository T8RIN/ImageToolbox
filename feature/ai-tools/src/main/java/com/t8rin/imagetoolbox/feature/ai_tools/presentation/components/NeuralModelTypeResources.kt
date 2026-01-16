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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Scanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel

fun NeuralModel.Type.title(): Int = when (this) {
    NeuralModel.Type.DEJPEG -> R.string.type_dejpeg
    NeuralModel.Type.DENOISE -> R.string.type_denoise
    NeuralModel.Type.COLORIZE -> R.string.type_colorize
    NeuralModel.Type.ARTIFACTS -> R.string.type_artifacts
    NeuralModel.Type.ENHANCE -> R.string.type_enhance
    NeuralModel.Type.ANIME -> R.string.type_anime
    NeuralModel.Type.SCANS -> R.string.type_scans
    NeuralModel.Type.UPSCALE -> R.string.type_upscale
    NeuralModel.Type.REMOVEBG -> R.string.type_removebg
}

fun NeuralModel.Type.icon(): ImageVector = when (this) {
    NeuralModel.Type.DEJPEG -> Icons.Outlined.Jpg
    NeuralModel.Type.DENOISE -> Icons.Outlined.NoiseAlt
    NeuralModel.Type.COLORIZE -> Icons.Outlined.Eyedropper
    NeuralModel.Type.ARTIFACTS -> Icons.Rounded.BrokenImageAlt
    NeuralModel.Type.ENHANCE -> Icons.Rounded.AutoFixHigh
    NeuralModel.Type.ANIME -> Icons.Rounded.Manga
    NeuralModel.Type.SCANS -> Icons.Rounded.Scanner
    NeuralModel.Type.UPSCALE -> Icons.Rounded.HighQuality
    NeuralModel.Type.REMOVEBG -> Icons.Rounded.Eraser
}

fun NeuralModel.Speed.icon(): ImageVector = when (this) {
    NeuralModel.Speed.VERY_FAST -> Icons.Rounded.Bolt
    NeuralModel.Speed.FAST -> Icons.Rounded.Rabbit
    NeuralModel.Speed.NORMAL -> Icons.AutoMirrored.Rounded.DirectionsWalk
    NeuralModel.Speed.SLOW -> Icons.Rounded.Tortoise
    NeuralModel.Speed.VERY_SLOW -> Icons.Rounded.Snail
}

@Composable
fun NeuralModelTypeBadge(
    type: NeuralModel.Type,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(22.dp)
            .container(
                color = MaterialTheme.colorScheme.run {
                    tertiaryContainer.blend(
                        color = secondaryContainer,
                        fraction = 0.3f
                    )
                },
                shape = CircleShape,
                resultPadding = 0.dp
            )
            .padding(start = 4.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val contentColor = MaterialTheme.colorScheme.run {
            onTertiaryContainer.blend(
                color = onSecondaryContainer,
                fraction = 0.65f
            )
        }

        Box(
            modifier = Modifier.size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = stringResource(type.title()),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun NeuralModelSpeedBadge(
    speed: NeuralModel.Speed,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .container(
                color = MaterialTheme.colorScheme.run {
                    tertiaryContainer.blend(
                        color = primaryContainer,
                        fraction = 0.5f
                    )
                },
                shape = CircleShape,
                resultPadding = 0.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        val contentColor = MaterialTheme.colorScheme.run {
            onTertiaryContainer.blend(
                color = onPrimaryContainer,
                fraction = 0.65f
            )
        }
        Icon(
            imageVector = speed.icon(),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
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
            NeuralModelSpeedBadge(it)
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
            NeuralModelTypeBadge(it)
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
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        NeuralModel.Type.entries.forEach {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NeuralModelTypeBadge(it)

                NeuralModelSpeedBadge(
                    speed = NeuralModel.Speed.entries.getOrNull(it.ordinal)
                        ?: NeuralModel.Speed.entries.random()
                )
            }
        }
    }
}