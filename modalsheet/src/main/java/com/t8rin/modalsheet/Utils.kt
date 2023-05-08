package com.t8rin.modalsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * Make the component clickable but omit the ripple effect.
 */
internal fun Modifier.clickableWithoutRipple(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}

/**
 * Consumes touch events.
 */
internal fun Modifier.consumeClicks() = clickableWithoutRipple(NoOpLambda)

/**
 * Empty lambda.
 */
internal val NoOpLambda: () -> Unit = {}
