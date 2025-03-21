/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent

@Composable
internal fun GradientMakerCompareButton(component: GradientMakerComponent) {
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }
    CompareButton(
        onClick = { showCompareSheet = true },
        visible = component.brush != null && component.allowPickingImage == true && component.selectedUri != Uri.EMPTY
    )

    CompareSheet(
        beforeContent = {
            Picture(
                model = component.selectedUri,
                modifier = Modifier.aspectRatio(
                    component.imageAspectRatio
                )
            )
        },
        afterContent = {
            if (component.isMeshGradient) {
                MeshGradientPreview(
                    meshGradientState = component.meshGradientState,
                    gradientAlpha = component.gradientAlpha,
                    allowPickingImage = component.allowPickingImage,
                    gradientSize = component.gradientSize,
                    selectedUri = component.selectedUri,
                    imageAspectRatio = component.imageAspectRatio
                )
            } else {
                val gradientState = rememberGradientState()
                LaunchedEffect(component.brush) {
                    gradientState.gradientType = component.gradientType
                    gradientState.linearGradientAngle = component.angle
                    gradientState.centerFriction = component.centerFriction
                    gradientState.radiusFriction = component.radiusFriction
                    gradientState.colorStops.apply {
                        clear()
                        addAll(component.colorStops)
                    }
                    gradientState.tileMode = component.tileMode
                }
                GradientPreview(
                    brush = gradientState.brush,
                    gradientAlpha = component.gradientAlpha,
                    allowPickingImage = component.allowPickingImage,
                    gradientSize = component.gradientSize,
                    onSizeChanged = {
                        gradientState.size = it
                    },
                    selectedUri = component.selectedUri,
                    imageAspectRatio = component.imageAspectRatio
                )
            }
        },
        visible = showCompareSheet,
        onDismiss = {
            showCompareSheet = false
        }
    )
}