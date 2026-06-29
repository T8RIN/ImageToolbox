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

@file:Suppress("FunctionNaming", "PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasUiState
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.screenLogic.VectorCanvasComponent
import io.ak1.drawbox.domain.model.Event
import io.ak1.drawbox.domain.model.State
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

@Composable
internal fun VectorCanvasEffects(
    state: State,
    dependencies: VectorCanvasEffectDependencies
) {
    TrackVectorCanvasChanges(
        state = state,
        onChange = dependencies.component::registerCanvasChange
    )
    CollectVectorCanvasEvents(
        controller = dependencies.controller,
        component = dependencies.component,
        uiState = dependencies.uiState
    )
    LaunchedEffect(dependencies.patternPainter, dependencies.patternTint) {
        dependencies.controller.setBackgroundPattern(
            dependencies.patternPainter,
            dependencies.patternTint
        )
    }
}

internal data class VectorCanvasEffectDependencies(
    val patternPainter: Painter?,
    val patternTint: Color,
    val controller: DrawBoxController,
    val component: VectorCanvasComponent,
    val uiState: VectorCanvasUiState
)

@Composable
private fun TrackVectorCanvasChanges(
    state: State,
    onChange: () -> Unit
) {
    val previousCanvas = remember {
        mutableStateOf(state.elements to state.bgColor)
    }

    LaunchedEffect(state.elements, state.bgColor) {
        val canvas = state.elements to state.bgColor
        if (canvas != previousCanvas.value) {
            previousCanvas.value = canvas
            onChange()
        }
    }
}

@Composable
private fun CollectVectorCanvasEvents(
    controller: DrawBoxController,
    component: VectorCanvasComponent,
    uiState: VectorCanvasUiState
) {
    LaunchedEffect(controller) {
        controller.events.collect { event ->
            when (event) {
                is Event.PngSaved -> {
                    val bitmap = event.bitmap
                    if (bitmap != null) {
                        component.savePng(bitmap, uiState.pendingPngLocation)
                    } else {
                        AppToastHost.showFailureToast(
                            event.throwable ?: IllegalStateException("Unable to export canvas")
                        )
                    }
                    uiState.pendingPngLocation = null
                }

                is Event.SvgExported -> component.saveSvg(event.svg)
                is Event.JsonExported -> component.saveJson(event.json)
                is Event.Error -> AppToastHost.showFailureToast(
                    event.throwable ?: IllegalStateException(event.message)
                )

                else -> Unit
            }
        }
    }
}
