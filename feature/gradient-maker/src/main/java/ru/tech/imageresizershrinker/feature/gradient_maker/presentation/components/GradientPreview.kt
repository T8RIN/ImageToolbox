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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.resources.icons.BrokenImageAlt
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker

@Composable
fun GradientPreview(
    brush: ShaderBrush?,
    gradientAlpha: Float,
    allowPickingImage: Boolean?,
    gradientSize: IntegerSize,
    onSizeChanged: (Size) -> Unit,
    imageAspectRatio: Float,
    selectedUri: Uri
) {
    val alpha by animateFloatAsState(
        if (brush == null) 1f
        else gradientAlpha
    )
    val solidBrush = SolidColor(MaterialTheme.colorScheme.surfaceContainer)
    AnimatedContent(
        targetState = if (allowPickingImage == true) {
            imageAspectRatio
        } else {
            gradientSize
                .aspectRatio
                .roundToTwoDigits()
                .coerceIn(0.01f..100f)
        }
    ) { aspectRatio ->
        Box {
            Box(
                modifier = Modifier
                    .aspectRatio(aspectRatio)
                    .clip(MaterialTheme.shapes.medium)
                    .then(
                        if (allowPickingImage != true) {
                            Modifier.transparencyChecker()
                        } else Modifier
                    )
                    .drawBehind {
                        drawRect(
                            brush = brush ?: solidBrush,
                            alpha = alpha
                        )
                    }
                    .onGloballyPositioned {
                        onSizeChanged(
                            Size(
                                it.size.width.toFloat(),
                                it.size.height.toFloat()
                            )
                        )
                    }
                    .zIndex(2f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(visible = brush == null) {
                    Icon(
                        imageVector = Icons.Rounded.BrokenImageAlt,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
                }
            }
            if (allowPickingImage == true) {
                Picture(
                    model = selectedUri,
                    modifier = Modifier.matchParentSize(),
                    shape = MaterialTheme.shapes.medium
                )
            }
        }
    }
}