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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

fun fancySlideTransition(
    isForward: Boolean,
    screenWidthDp: Int
): ContentTransform = if (isForward) {
    slideInHorizontally(
        animationSpec = tween(600, easing = FancyTransitionEasing),
        initialOffsetX = { screenWidthDp }) + fadeIn(
        tween(300, 100)
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(600, easing = FancyTransitionEasing),
        targetOffsetX = { -screenWidthDp }) + fadeOut(
        tween(300, 100)
    )
} else {
    slideInHorizontally(
        animationSpec = tween(600, easing = FancyTransitionEasing),
        initialOffsetX = { -screenWidthDp }) + fadeIn(
        tween(300, 100)
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(600, easing = FancyTransitionEasing),
        targetOffsetX = { screenWidthDp }) + fadeOut(
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

val ModalSheetAnimationSpec = tween<Float>(
    durationMillis = 600,
    easing = FancyTransitionEasing
)

private fun <T> openCloseTransitionSpec(
    duration: Int = 500,
    delay: Int = 0
) = tween<T>(
    durationMillis = duration,
    delayMillis = delay,
    easing = TransitionEasing
)