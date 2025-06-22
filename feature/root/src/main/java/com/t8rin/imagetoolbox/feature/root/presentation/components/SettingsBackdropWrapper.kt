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

package com.t8rin.imagetoolbox.feature.root.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.Surface
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.gesture.detectPointerTransformGestures
import com.t8rin.imagetoolbox.core.settings.domain.model.FastSettingsSide
import com.t8rin.imagetoolbox.core.ui.utils.animation.FancyTransitionEasing
import com.t8rin.imagetoolbox.core.ui.utils.helper.PredictiveBackObserver
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.modifier.toShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withLayoutCorners
import com.t8rin.imagetoolbox.feature.settings.presentation.SettingsContent
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@Composable
internal fun SettingsBackdropWrapper(
    currentScreen: Screen?,
    concealBackdropFlow: Flow<Boolean>,
    settingsComponent: SettingsComponent,
    children: @Composable () -> Unit
) {
    var shape by remember { mutableStateOf(RectangleShape) }
    val scaffoldState = rememberBackdropScaffoldState(
        initialValue = BackdropValue.Concealed,
        animationSpec = tween(
            durationMillis = 400,
            easing = FancyTransitionEasing
        )
    )
    val canExpandSettings = ((currentScreen?.id ?: -1) >= 0)
        .and(settingsComponent.settingsState.fastSettingsSide != FastSettingsSide.None)

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

    val isTargetRevealed = scaffoldState.targetValue == BackdropValue.Revealed

    BackdropScaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.withLayoutCorners {
            shape = it.toShape(1f)
            this
        },
        appBar = {},
        frontLayerContent = {
            val alpha by animateFloatAsState(
                if (isTargetRevealed) 1f else 0f
            )
            val color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha / 2f)
            var isWantOpenSettings by remember {
                mutableStateOf(false)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        drawRect(color)
                    }
            ) {
                Box(
                    modifier = Modifier.pointerInput(isWantOpenSettings) {
                        detectPointerTransformGestures(
                            consume = false,
                            onGestureEnd = {},
                            onGestureStart = {
                                isWantOpenSettings = false
                            },
                            onGesture = { _, _, _, _, _, _ -> }
                        )
                    },
                    content = {
                        children()

                        if (isTargetRevealed || scaffoldState.isRevealed) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color.Transparent
                            ) {}
                        }
                    }
                )

                SettingsOpenButton(
                    isWantOpenSettings = isWantOpenSettings,
                    onStateChange = { isWantOpenSettings = it },
                    scaffoldState = scaffoldState,
                    canExpandSettings = canExpandSettings
                )

                val progress = scaffoldState.progress(
                    from = BackdropValue.Revealed,
                    to = BackdropValue.Concealed
                ) * 20f

                EnhancedModalSheetDragHandle(
                    color = Color.Transparent,
                    drawStroke = false,
                    bendAngle = (-15f * (1f - progress)).coerceAtMost(0f),
                    modifier = Modifier.alpha(alpha)
                )
            }
        },
        backLayerContent = {
            if (canExpandSettings && (scaffoldState.isRevealed || isTargetRevealed)) {
                PredictiveBackObserver(
                    onProgress = {
                        predictiveBackProgress = it * 1.3f
                    },
                    onClean = { isCompleted ->
                        if (isCompleted) scaffoldState.conceal()
                        clean()
                    },
                    enabled = isTargetRevealed
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalScreenSize.current.height)
                        .alpha(1f - animatedPredictiveBackProgress)
                ) {
                    SettingsContent(
                        component = settingsComponent
                    )
                }
            }
        },
        peekHeight = 0.dp,
        headerHeight = 48.dp + WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding(),
        persistentAppBar = false,
        frontLayerElevation = 0.dp,
        backLayerBackgroundColor = MaterialTheme.colorScheme.surface,
        frontLayerBackgroundColor = MaterialTheme.colorScheme.surface,
        frontLayerScrimColor = Color.Transparent,
        frontLayerShape = RectangleShape,
        gesturesEnabled = scaffoldState.isRevealed
    )
}