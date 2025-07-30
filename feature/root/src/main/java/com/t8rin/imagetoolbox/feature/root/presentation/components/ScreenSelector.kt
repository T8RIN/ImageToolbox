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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.t8rin.imagetoolbox.core.ui.utils.animation.toolboxPredictiveBackAnimation
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalCurrentScreen
import com.t8rin.imagetoolbox.feature.root.presentation.components.utils.ResetThemeOnGoBack
import com.t8rin.imagetoolbox.feature.root.presentation.components.utils.ScreenBasedMaxBrightnessEnforcement
import com.t8rin.imagetoolbox.feature.root.presentation.screenLogic.RootComponent

@Composable
internal fun ScreenSelector(
    component: RootComponent
) {
    ResetThemeOnGoBack(component)

    val childStack by component.childStack.subscribeAsState()
    val currentScreen = LocalCurrentScreen.current

    SettingsBackdropWrapper(
        currentScreen = currentScreen,
        concealBackdropFlow = component.concealBackdropFlow,
        settingsComponent = component.settingsComponent,
        children = {
            Children(
                stack = childStack,
                modifier = Modifier.fillMaxSize(),
                animation = remember(component) {
                    toolboxPredictiveBackAnimation(
                        backHandler = component.backHandler,
                        onBack = component::navigateBack
                    )
                },
                content = { child ->
                    child.instance.Content()
                }
            )
        }
    )

    ScreenBasedMaxBrightnessEnforcement(currentScreen)
}