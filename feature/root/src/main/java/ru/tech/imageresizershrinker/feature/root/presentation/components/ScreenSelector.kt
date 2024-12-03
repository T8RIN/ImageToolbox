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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.ui.utils.animation.toolboxPredictiveBackAnimation
import ru.tech.imageresizershrinker.core.ui.widget.modifier.toShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withLayoutCorners
import ru.tech.imageresizershrinker.feature.root.presentation.components.utils.ResetThemeOnGoBack
import ru.tech.imageresizershrinker.feature.root.presentation.components.utils.ScreenBasedMaxBrightnessEnforcement
import ru.tech.imageresizershrinker.feature.root.presentation.screenLogic.RootComponent
import kotlin.random.Random

@Composable
internal fun ScreenSelector(
    component: RootComponent
) {
    ResetThemeOnGoBack(component)

    val childStack by component.childStack.subscribeAsState()
    val currentScreen = childStack.items.lastOrNull()?.configuration
    var shape by remember { mutableStateOf<Shape>(RectangleShape) }
    val scaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    val canExpandSettings = (currentScreen?.id ?: -1) >= 0

    LaunchedEffect(canExpandSettings) {
        if (!canExpandSettings) {
            scaffoldState.conceal()
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
            Children(
                stack = childStack,
                modifier = Modifier.fillMaxSize(),
                animation = toolboxPredictiveBackAnimation(
                    backHandler = component.backHandler,
                    onBack = component::navigateBack
                ),
                content = { child ->
                    child.instance.Content()
                }
            )
        },
        backLayerContent = {
            val scope = rememberCoroutineScope()
            BackHandler(
                enabled = scaffoldState.isRevealed
            ) {
                scope.launch {
                    scaffoldState.conceal()
                }
            }
            LazyColumn(
                modifier = Modifier.clip(shape)
            ) {
                items(100) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(
                                remember {
                                    Color(Random.nextInt())
                                }
                            )
                    )
                }
            }
        },
        peekHeight = 0.dp,
        headerHeight = 64.dp,
        persistentAppBar = false,
        backLayerBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        frontLayerBackgroundColor = MaterialTheme.colorScheme.surface,
        frontLayerScrimColor = MaterialTheme.colorScheme.scrim.copy(0.5f),
        frontLayerShape = shape,
        gesturesEnabled = canExpandSettings
    )

    //TODO: How to add settings on each screen

    ScreenBasedMaxBrightnessEnforcement(currentScreen)
}