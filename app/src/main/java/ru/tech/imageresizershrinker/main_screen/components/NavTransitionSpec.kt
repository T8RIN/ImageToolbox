package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavTransitionScope
import ru.tech.imageresizershrinker.utils.navigation.Screen

val NavTransitionSpec: NavTransitionScope.(NavAction, Screen, Screen) -> ContentTransform =
    { action, _, _ ->
        fun <T> animationSpec(
            duration: Int = 500,
            delay: Int = 0
        ) = tween<T>(
            durationMillis = duration,
            delayMillis = delay,
            easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
        )
        if (action == NavAction.Pop) {
            slideInHorizontally(
                animationSpec()
            ) { -it / 3 } + fadeIn(
                animationSpec(500)
            ) togetherWith slideOutHorizontally(
                animationSpec()
            ) { it / 3 } + fadeOut(
                animationSpec(150)
            )
        } else {
            slideInHorizontally(
                animationSpec()
            ) { it / 3 } + fadeIn(
                animationSpec(),
            ) togetherWith slideOutHorizontally(
                animationSpec()
            ) { -it / 3 } + fadeOut(
                animationSpec(250)
            )
        }
    }