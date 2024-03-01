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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText


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
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialStarShape
            )
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    0.1f,
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = MaterialStarShape
            )
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .size(minHeight / 2),
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
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
    if (progress.isFinite() && progress >= 0 && left > 0) {
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

@Composable
fun BoxScope.Loading(
    progress: Float,
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
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialStarShape
            )
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    0.1f,
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = MaterialStarShape
            )
            .align(Alignment.Center),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(maxWidth),
                color = MaterialTheme.colorScheme.tertiary.copy(0.5f),
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                strokeCap = StrokeCap.Round,
            )
            val progressAnimated by animateFloatAsState(targetValue = progress)
            CircularProgressIndicator(
                modifier = Modifier.size(maxWidth),
                progress = { progressAnimated },
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round,
            )
            additionalContent(maxWidth)
        }
    }
    KeepScreenOn()
}