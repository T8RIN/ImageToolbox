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

package ru.tech.imageresizershrinker.core.ui.widget.other

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Loading(modifier: Modifier = Modifier) {
    val borderWidth = LocalSettingsState.current.borderWidth

    BoxWithConstraints(
        modifier
            .then(
                if (modifier == Modifier) {
                    Modifier.sizeIn(maxWidth = 84.dp, maxHeight = 84.dp)
                } else Modifier
            )
            .aspectRatio(1f)
            .then(
                if (borderWidth <= 0.dp) {
                    Modifier.rsBlurShadow(
                        radius = 2.dp,
                        shape = MaterialStarShape,
                        isAlphaContentClip = true
                    )
                } else Modifier
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = MaterialStarShape
            )
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.1f,
                    onTopOf = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                shape = MaterialStarShape
            )
    ) {
        CircularWavyProgressIndicator(
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .size(minHeight / 2),
            trackColor = MaterialTheme.colorScheme.secondary.copy(0.3f),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    KeepScreenOn()
}

@Composable
fun BoxScope.Loading(
    done: Int,
    left: Int
) {
    val progress = done / left.toFloat()
    if (progress.isFinite() && progress >= 0 && left > 1) {
        Loading(progress = progress) {
            AutoSizeText(
                text = "$done / $left",
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(it * 0.8f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Loading(
            modifier = Modifier
                .align(Alignment.Center)
                .size(108.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BoxScope.Loading(
    progress: Float,
    loaderSize: Dp = 56.dp,
    additionalContent: @Composable (Dp) -> Unit = {}
) {
    val borderWidth = LocalSettingsState.current.borderWidth
    Column(
        modifier = Modifier
            .size(108.dp)
            .then(
                if (borderWidth <= 0.dp) {
                    Modifier.rsBlurShadow(radius = 2.dp, shape = MaterialStarShape)
                } else Modifier
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = MaterialStarShape
            )
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.1f,
                    onTopOf = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                shape = MaterialStarShape
            )
            .align(Alignment.Center),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier.size(loaderSize),
            contentAlignment = Alignment.Center
        ) {
            CircularWavyProgressIndicator(
                modifier = Modifier.size(maxWidth),
                color = MaterialTheme.colorScheme.secondary.copy(0.3f),
                trackColor = MaterialTheme.colorScheme.surfaceContainer
            )
            val progressAnimated by animateFloatAsState(targetValue = progress)
            CircularWavyProgressIndicator(
                modifier = Modifier.size(maxWidth),
                progress = { progressAnimated },
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
            additionalContent(maxWidth)
        }
    }
    KeepScreenOn()
}