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

package ru.tech.imageresizershrinker.app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.builtins.serializer
import ru.tech.imageresizershrinker.app.presentation.NavigationChild.ScreenA
import ru.tech.imageresizershrinker.app.presentation.NavigationChild.ScreenB

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navController = StackNavigation<Int>()

    val childStack: Value<ChildStack<Int, NavigationChild>> by lazy {
        childStack(
            source = navController,
            initialConfiguration = 0,
            serializer = Int.serializer(),
            handleBackButton = true,
            childFactory = { screen, context ->
                when (screen) {
                    0 -> ScreenA(
                        ScreenA.Component(
                            componentContext = context,
                            onForward = { navController.pushNew(1) }
                        )
                    )

                    else -> ScreenB(
                        ScreenB.Component(
                            componentContext = context,
                            onBack = { navController.pop() }
                        )
                    )
                }
            }
        )
    }

    fun navigateBack() = navController.pop()

}

sealed class NavigationChild {

    @Composable
    abstract fun Content()

    class ScreenA(val component: Component) : NavigationChild() {

        class Component(
            componentContext: ComponentContext,
            val onForward: () -> Unit
        ) : ComponentContext by componentContext

        @Composable
        override fun Content() {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Yellow),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    shapes = ButtonShapes(
                        shape = CircleShape,
                        pressedShape = IconButtonDefaults.smallPressedShape
                    ),
                    onClick = component.onForward
                ) {
                    Text("FORWARD")
                }
            }
        }
    }

    class ScreenB(val component: Component) : NavigationChild() {

        class Component(
            componentContext: ComponentContext,
            val onBack: () -> Unit
        ) : ComponentContext by componentContext

        @Composable
        override fun Content() {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                //!!! TODO: IMPORTANT, Flickering causes passing ButtonShapes
                //!!! TODO:  (no flickering if not using shapes, and passing just shape without press animation to smaller shape),
                //!!! TODO:  to be more accurate shapeByInteraction function inside sources
                Button(
                    shapes = ButtonShapes(
                        shape = CircleShape,
                        pressedShape = IconButtonDefaults.smallPressedShape
                    ),
                    onClick = component.onBack
                ) {
                    Text("Back")
                }
            }
        }
    }
}