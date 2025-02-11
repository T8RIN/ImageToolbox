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

package ru.tech.imageresizershrinker.core.ui.utils.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.essenty.backhandler.BackHandler
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen

fun fancySlideTransition(
    isForward: Boolean,
    screenWidthPx: Int,
    duration: Int = 600
): ContentTransform = if (isForward) {
    slideInHorizontally(
        animationSpec = tween(duration, easing = FancyTransitionEasing),
        initialOffsetX = { screenWidthPx }) + fadeIn(
        tween(300, 100)
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(duration, easing = FancyTransitionEasing),
        targetOffsetX = { -screenWidthPx }) + fadeOut(
        tween(300, 100)
    )
} else {
    slideInHorizontally(
        animationSpec = tween(600, easing = FancyTransitionEasing),
        initialOffsetX = { -screenWidthPx }) + fadeIn(
        tween(300, 100)
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(600, easing = FancyTransitionEasing),
        targetOffsetX = { screenWidthPx }) + fadeOut(
        tween(300, 100)
    )
}

val PageOpenTransition = slideInHorizontally(
    openCloseTransitionSpec()
) { -it / 3 } + fadeIn(
    openCloseTransitionSpec(500)
)

val PageCloseTransition = slideOutHorizontally(
    openCloseTransitionSpec()
) { -it / 3 } + fadeOut(
    openCloseTransitionSpec(500)
)

private fun <T> openCloseTransitionSpec(
    duration: Int = 500,
    delay: Int = 0
) = tween<T>(
    durationMillis = duration,
    delayMillis = delay,
    easing = TransitionEasing
)

fun <NavigationChild : Any> toolboxPredictiveBackAnimation(
    backHandler: BackHandler,
    onBack: () -> Unit
): StackAnimation<Screen, NavigationChild>? = predictiveBackAnimation(
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

@Composable
fun animateFloatingRangeAsState(
    range: ClosedFloatingPointRange<Float>,
    animationSpec: AnimationSpec<Float> = spring()
): State<ClosedFloatingPointRange<Float>> {
    val start = animateFloatAsState(
        targetValue = range.start,
        animationSpec = animationSpec
    )

    val end = animateFloatAsState(
        targetValue = range.endInclusive,
        animationSpec = animationSpec
    )

    return remember(start, end) {
        derivedStateOf {
            start.value..end.value
        }
    }
}