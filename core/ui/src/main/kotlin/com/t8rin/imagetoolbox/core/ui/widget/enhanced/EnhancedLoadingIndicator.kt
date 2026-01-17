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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.animation.RotationEasing
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText

@Composable
fun EnhancedLoadingIndicator(modifier: Modifier = Modifier) {
    EnhancedLoadingIndicatorContainer(
        modifier = modifier
            .then(
                if (modifier == Modifier) {
                    Modifier.sizeIn(maxWidth = 76.dp, maxHeight = 76.dp)
                } else Modifier
            )
            .aspectRatio(1f),
        content = {
            LoadingIndicator(
                modifier = Modifier.size(this.minHeight / 1.2f)
            )
        }
    )
}

@Composable
fun BoxScope.EnhancedLoadingIndicator(
    progress: Float,
    loaderSize: Dp = 60.dp,
    additionalContent: @Composable (Dp) -> Unit = {}
) {
    EnhancedLoadingIndicatorContainer(
        modifier = Modifier
            .size(108.dp)
            .align(Alignment.Center),
        content = {
            BoxWithConstraints(
                modifier = Modifier.size(loaderSize),
                contentAlignment = Alignment.Center
            ) {
                EnhancedCircularProgressIndicator(
                    modifier = Modifier.size(this.maxWidth),
                    color = MaterialTheme.colorScheme.secondary.copy(0.3f),
                    trackColor = MaterialTheme.colorScheme.surfaceContainer
                )
                val progressAnimated = animateFloatAsState(targetValue = progress)
                EnhancedCircularProgressIndicator(
                    modifier = Modifier.size(maxWidth),
                    progress = { progressAnimated.value },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Transparent
                )
                additionalContent(maxWidth)
            }
        }
    )
}

@Composable
fun BoxScope.EnhancedLoadingIndicator(done: Int, left: Int) {
    val progress = done / left.toFloat()

    if (progress.isFinite() && progress >= 0 && left > 1) {
        EnhancedLoadingIndicator(
            progress = progress,
            additionalContent = {
                AutoSizeText(
                    text = "$done / $left",
                    maxLines = 1,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(it * 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        )
    } else {
        EnhancedLoadingIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(108.dp)
        )
    }
}

@Composable
private fun EnhancedLoadingIndicatorContainer(
    modifier: Modifier,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 1300,
                easing = RotationEasing
            )
        )
    )

    BoxWithConstraints(
        modifier = modifier.keepScreenOn(),
        contentAlignment = Alignment.Center
    ) {
        val settingsState = LocalSettingsState.current
        val borderWidth = settingsState.borderWidth
        val backgroundColor = takeColorFromScheme {
            surfaceContainerHighest.blend(
                color = primaryContainer,
                fraction = 0.1f
            )
        }

        Spacer(
            modifier = Modifier
                .size(minHeight)
                .rotate(rotation)
                .then(
                    if (borderWidth <= 0.dp && settingsState.drawContainerShadows) {
                        Modifier.rsBlurShadow(
                            radius = 1.05.dp,
                            shape = MaterialStarShape,
                            isAlphaContentClip = true,
                            offset = DpOffset.Zero,
                            spread = if (settingsState.isNightMode) 1.15.dp else 0.44.dp,
                            color = MaterialTheme.colorScheme.scrim.copy(0.31f)
                        )
                    } else Modifier
                )
                .background(
                    color = backgroundColor,
                    shape = MaterialStarShape
                )
                .border(
                    width = borderWidth,
                    color = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f,
                        onTopOf = backgroundColor
                    ),
                    shape = MaterialStarShape
                )
        )

        content()
    }
}