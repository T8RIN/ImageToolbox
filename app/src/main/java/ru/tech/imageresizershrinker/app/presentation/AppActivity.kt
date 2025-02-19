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

package ru.tech.imageresizershrinker.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.retainedComponent
import com.arkivanov.essenty.backhandler.BackHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppActivity : ComponentActivity() {


    private val component: RootComponent by lazy {
        retainedComponent(factory = { RootComponent(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Children(
                stack = component.childStack.subscribeAsState().value,
                modifier = Modifier.fillMaxSize(),
                animation = toolboxPredictiveBackAnimation(
                    backHandler = component.backHandler,
                    onBack = component::navigateBack
                ),
                content = { child ->
                    child.instance.Content()
                }
            )
        }
    }
}

fun <NavigationChild : Any> toolboxPredictiveBackAnimation(
    backHandler: BackHandler,
    onBack: () -> Unit
): StackAnimation<Int, NavigationChild> = predictiveBackAnimation(
    backHandler = backHandler,
    onBack = onBack,
    fallbackAnimation = stackAnimation(
        fade(
            tween(
                durationMillis = 300,
                easing = AlphaEasing
            )
        ) + slide(
            tween(
                durationMillis = 400,
                easing = FancyTransitionEasing
            )
        ) + scale(
            tween(
                durationMillis = 500,
                easing = PointToPointEasing
            )
        )
    ),
    selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
)

val FancyTransitionEasing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)

val AlphaEasing = CubicBezierEasing(0.4f, 0.4f, 0.17f, 0.9f)

val PointToPointEasing = CubicBezierEasing(0.55f, 0.55f, 0f, 1f)