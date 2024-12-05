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

package ru.tech.imageresizershrinker.feature.root.presentation.components

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import ru.tech.imageresizershrinker.core.ui.utils.animation.FancyTransitionEasing
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.modifier.toShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withLayoutCorners
import ru.tech.imageresizershrinker.feature.settings.presentation.SettingsContent
import ru.tech.imageresizershrinker.feature.settings.presentation.screenLogic.SettingsComponent
import kotlin.coroutines.cancellation.CancellationException

@Composable
internal fun SettingsBackdropWrapper(
    currentScreen: Screen?,
    concealBackdropFlow: Flow<Boolean>,
    settingsComponent: SettingsComponent,
    children: @Composable () -> Unit
) {
    var shape by remember { mutableStateOf<Shape>(RectangleShape) }
    val scaffoldState = rememberBackdropScaffoldState(
        initialValue = BackdropValue.Concealed,
        animationSpec = tween(
            durationMillis = 400,
            easing = FancyTransitionEasing
        )
    )
    val canExpandSettings =
        (currentScreen?.id ?: -1) >= 0 //TODO: && settingsComponent.settingsState.addFastSettings

    var predictiveBackProgress by remember {
        mutableFloatStateOf(0f)
    }
    val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)

    val clean = {
        predictiveBackProgress = 0f
    }

    LaunchedEffect(canExpandSettings) {
        if (!canExpandSettings) {
            clean()
            scaffoldState.conceal()
        }
    }

    LaunchedEffect(concealBackdropFlow) {
        concealBackdropFlow
            .debounce(200)
            .collectLatest {
                if (it) {
                    clean()
                    scaffoldState.conceal()
                }
            }
    }

    BackdropScaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.withLayoutCorners {
            shape = it.toShape(1f)
            this
        },
        appBar = {},
        frontLayerContent = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                children()

                val alpha by animateFloatAsState(
                    if (scaffoldState.targetValue == BackdropValue.Revealed) 1f else 0f
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha / 2f))
                )

                EnhancedModalSheetDragHandle(
                    color = Color.Transparent,
                    drawStroke = false,
                    modifier = Modifier.alpha(alpha)
                )
            }
        },
        backLayerContent = {
            if (scaffoldState.isRevealed) {
                PredictiveBackHandler { progress ->
                    try {
                        progress.collect { event ->
                            if (event.progress <= 0.05f) {
                                clean()
                            }
                            predictiveBackProgress = event.progress * 1.3f
                        }
                        scaffoldState.conceal()
                        clean()
                    } catch (_: CancellationException) {
                        clean()
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .alpha(1f - animatedPredictiveBackProgress)
            ) {
                SettingsContent(
                    component = settingsComponent
                )
            }
        },
        peekHeight = 0.dp,
        headerHeight = 72.dp,
        persistentAppBar = false,
        backLayerBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        frontLayerBackgroundColor = MaterialTheme.colorScheme.surface,
        frontLayerScrimColor = Color.Transparent,
        frontLayerShape = shape,
        gesturesEnabled = canExpandSettings
    )
}