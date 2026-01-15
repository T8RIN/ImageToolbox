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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Scanner
import androidx.compose.material3.Badge
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
import com.t8rin.imagetoolbox.core.resources.icons.HighRes
import com.t8rin.imagetoolbox.core.resources.icons.Jpg
import com.t8rin.imagetoolbox.core.resources.icons.Manga
import com.t8rin.imagetoolbox.core.resources.icons.NoiseAlt
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.blend
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
    NeuralModel.Type.COLORIZE -> Icons.Outlined.Colorize
    NeuralModel.Type.ARTIFACTS -> Icons.Rounded.BrokenImageAlt
    NeuralModel.Type.ENHANCE -> Icons.Outlined.AutoFixHigh
    NeuralModel.Type.ANIME -> Icons.Outlined.Manga
    NeuralModel.Type.SCANS -> Icons.Outlined.Scanner
    NeuralModel.Type.UPSCALE -> Icons.Outlined.HighRes
    NeuralModel.Type.REMOVEBG -> Icons.Rounded.Eraser
}

@Composable
fun NeuralModelTypeBadge(
    type: NeuralModel.Type,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.run {
                        tertiary.blend(
                            color = secondary,
                            fraction = 0.65f
                        )
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.run {
                    onTertiary.blend(
                        color = onSecondary,
                        fraction = 0.65f
                    )
                },
                modifier = Modifier.size(12.dp)
            )
        }
        Badge(
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Text(stringResource(type.title()))
        }
    }
}

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(
    isDarkTheme = true,
    keyColor = Color.Green
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NeuralModel.Type.entries.forEach {
            NeuralModelTypeBadge(it)
        }
    }
}