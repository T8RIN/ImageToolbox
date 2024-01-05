package com.smarttoolfactory.gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventPass.Final
import androidx.compose.ui.input.pointer.PointerEventPass.Initial
import androidx.compose.ui.input.pointer.PointerEventPass.Main
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Reads [awaitFirstDown], and [AwaitPointerEventScope.awaitPointerEvent] to
 * get [PointerInputChange] and motion event states
 * [onDown], [onMove], and [onUp].
 *
 * To prevent other pointer functions that call [awaitFirstDown]
 * or [AwaitPointerEventScope.awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consume]  in [onMove]  or call
 * [PointerInputChange.consume] in [onDown] to prevent events
 * that check first pointer interaction.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove one or multiple pointers are being moved on screen.
 * @param onUp last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 * @param requireUnconsumed is `true` and the first
 * down is consumed in the [PointerEventPass.Main] pass, that gesture is ignored.
 * @param pass The enumeration of passes where [PointerInputChange]
 * traverses up and down the UI tree.
 *
 * PointerInputChanges traverse throw the hierarchy in the following passes:
 *
 * 1. [Initial]: Down the tree from ancestor to descendant.
 * 2. [Main]: Up the tree from descendant to ancestor.
 * 3. [Final]: Down the tree from ancestor to descendant.
 *
 * These passes serve the following purposes:
 *
 * 1. Initial: Allows ancestors to consume aspects of [PointerInputChange] before descendants.
 * This is where, for example, a scroller may block buttons from getting tapped by other fingers
 * once scrolling has started.
 * 2. Main: The primary pass where gesture filters should react to and consume aspects of
 * [PointerInputChange]s. This is the primary path where descendants will interact with
 * [PointerInputChange]s before parents. This allows for buttons to respond to a tap before a
 * container of the bottom to respond to a tap.
 * 3. Final: This pass is where children can learn what aspects of [PointerInputChange]s were
 * consumed by parents during the [Main] pass. For example, this is how a button determines that
 * it should no longer respond to fingers lifting off of it because a parent scroller has
 * consumed movement in a [PointerInputChange].
 */
suspend fun PointerInputScope.detectMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = Main
) {

    coroutineScope {
        awaitEachGesture {
            // Wait for at least one pointer to press down, and set first contact position
            val down: PointerInputChange = awaitFirstDown(requireUnconsumed)
            onDown(down)

            var pointer = down
            // Main pointer is the one that is down initially
            var pointerId = down.id

            // If a move event is followed fast enough down is skipped, especially by Canvas
            // to prevent it we add delay after first touch
            var waitedAfterDown = false

            launch {
                delay(delayAfterDownInMillis)
                waitedAfterDown = true
            }

            while (true) {

                val event: PointerEvent = awaitPointerEvent(pass)

                val anyPressed = event.changes.any { it.pressed }

                // There are at least one pointer pressed
                if (anyPressed) {
                    // Get pointer that is down, if first pointer is up
                    // get another and use it if other pointers are also down
                    // event.changes.first() doesn't return same order
                    val pointerInputChange =
                        event.changes.firstOrNull { it.id == pointerId }
                            ?: event.changes.first()

                    // Next time will check same pointer with this id
                    pointerId = pointerInputChange.id
                    pointer = pointerInputChange

                    if (waitedAfterDown) {
                        onMove(pointer)
                    }
                } else {
                    // All of the pointers are up
                    onUp(pointer)
                    break
                }
            }
        }
    }
}

/**
 * Reads [awaitFirstDown], and [AwaitPointerEventScope.awaitPointerEvent] to
 * get [PointerInputChange] and motion event states
 * [onDown], [onMove], and [onUp]. Unlike overload of this function [onMove] returns
 * list of [PointerInputChange] to get data about all pointers that are on the screen.
 *
 * To prevent other pointer functions that call [awaitFirstDown]
 * or [AwaitPointerEventScope.awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consume]  in [onMove]  or call
 * [PointerInputChange.consume] in [onDown] to prevent events
 * that check first pointer interaction.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove one or multiple pointers are being moved on screen.
 * @param onUp last pointer is up
 * @param delayAfterDownInMillis is optional delay after [onDown] This delay might be
 * required Composables like **Canvas** to process [onDown] before [onMove]
 * @param requireUnconsumed is `true` and the first
 * down is consumed in the [PointerEventPass.Main] pass, that gesture is ignored.
 * @param pass The enumeration of passes where [PointerInputChange]
 * traverses up and down the UI tree.
 *
 * PointerInputChanges traverse throw the hierarchy in the following passes:
 *
 * 1. [Initial]: Down the tree from ancestor to descendant.
 * 2. [Main]: Up the tree from descendant to ancestor.
 * 3. [Final]: Down the tree from ancestor to descendant.
 *
 * These passes serve the following purposes:
 *
 * 1. Initial: Allows ancestors to consume aspects of [PointerInputChange] before descendants.
 * This is where, for example, a scroller may block buttons from getting tapped by other fingers
 * once scrolling has started.
 * 2. Main: The primary pass where gesture filters should react to and consume aspects of
 * [PointerInputChange]s. This is the primary path where descendants will interact with
 * [PointerInputChange]s before parents. This allows for buttons to respond to a tap before a
 * container of the bottom to respond to a tap.
 * 3. Final: This pass is where children can learn what aspects of [PointerInputChange]s were
 * consumed by parents during the [Main] pass. For example, this is how a button determines that
 * it should no longer respond to fingers lifting off of it because a parent scroller has
 * consumed movement in a [PointerInputChange].
 *
 */
suspend fun PointerInputScope.detectMotionEventsAsList(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = Main
) {

    coroutineScope {
        awaitEachGesture {
            // Wait for at least one pointer to press down, and set first contact position
            val down: PointerInputChange = awaitFirstDown(requireUnconsumed)
            onDown(down)

            var pointer = down
            // Main pointer is the one that is down initially
            var pointerId = down.id

            // If a move event is followed fast enough down is skipped, especially by Canvas
            // to prevent it we add delay after first touch
            var waitedAfterDown = false

            launch {
                delay(delayAfterDownInMillis)
                waitedAfterDown = true
            }

            while (true) {

                val event: PointerEvent = awaitPointerEvent(pass)

                val anyPressed = event.changes.any { it.pressed }

                // There are at least one pointer pressed
                if (anyPressed) {
                    // Get pointer that is down, if first pointer is up
                    // get another and use it if other pointers are also down
                    // event.changes.first() doesn't return same order
                    val pointerInputChange =
                        event.changes.firstOrNull { it.id == pointerId }
                            ?: event.changes.first()

                    // Next time will check same pointer with this id
                    pointerId = pointerInputChange.id
                    pointer = pointerInputChange

                    if (waitedAfterDown) {
                        onMove(event.changes)
                    }

                } else {
                    // All of the pointers are up
                    onUp(pointer)
                    break
                }
            }
        }
    }
}
