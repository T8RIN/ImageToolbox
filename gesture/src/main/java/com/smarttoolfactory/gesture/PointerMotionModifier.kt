package com.smarttoolfactory.gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventPass.Final
import androidx.compose.ui.input.pointer.PointerEventPass.Initial
import androidx.compose.ui.input.pointer.PointerEventPass.Main
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * To prevent other pointer functions that call [awaitFirstDown]
 * or [AwaitPointerEventScope.awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consume]  in [onMove]  or call
 * [PointerInputChange.consume] in [onDown] to prevent events
 * that check first pointer interaction.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
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
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with a different [key1].
 */
fun Modifier.pointerMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = Main,
    key1: Any? = Unit
) = this.then(
    Modifier.pointerInput(key1) {
        detectMotionEvents(
            onDown,
            onMove,
            onUp,
            delayAfterDownInMillis,
            requireUnconsumed,
            pass
        )
    }
)

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * To prevent other pointer functions that call [awaitFirstDown]
 * or [AwaitPointerEventScope.awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consume]  in [onMove]  or call
 * [PointerInputChange.consume] in [onDown] to prevent events
 * that check first pointer interaction.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
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
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with a different [key1] or [key2].
 */
fun Modifier.pointerMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = Main,
    key1: Any?,
    key2: Any?
) = this.then(
    Modifier.pointerInput(key1, key2) {
        detectMotionEvents(
            onDown,
            onMove,
            onUp,
            delayAfterDownInMillis,
            requireUnconsumed,
            pass
        )
    }
)

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * To prevent other pointer functions that call [awaitFirstDown]
 * or [AwaitPointerEventScope.awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consume]  in [onMove]  or call
 * [PointerInputChange.consume] in [onDown] to prevent events
 * that check first pointer interaction.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
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
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with any different [keys].
 */
fun Modifier.pointerMotionEvents(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = Main,
    vararg keys: Any?,
) = this.then(
    Modifier.pointerInput(*keys) {
        detectMotionEvents(
            onDown,
            onMove,
            onUp,
            delayAfterDownInMillis,
            requireUnconsumed,
            pass
        )
    }
)

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * To prevent other pointer functions that call [awaitFirstDown]
 * or [AwaitPointerEventScope.awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consume]  in [onMove]  or call
 * [PointerInputChange.consume] in [onDown] to prevent events
 * that check first pointer interaction.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
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
 *  The pointer input handling block will be cancelled and re-started when pointerInput
 *  is recomposed with a different [key1].
 */
fun Modifier.pointerMotionEventList(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = Main,
    key1: Any? = Unit
) = this.then(
    Modifier.pointerInput(key1) {
        detectMotionEventsAsList(
            onDown,
            onMove,
            onUp,
            delayAfterDownInMillis,
            requireUnconsumed,
            pass
        )
    }
)

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * To prevent other pointer functions that call [awaitFirstDown]
 * or [AwaitPointerEventScope.awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consume]  in [onMove]  or call
 * [PointerInputChange.consume] in [onDown] to prevent events
 * that check first pointer interaction.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
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
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with a different [key1] or [key2].
 */
fun Modifier.pointerMotionEventList(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = Main,
    key1: Any? = Unit,
    key2: Any? = Unit
) = this.then(
    Modifier.pointerInput(key1, key2) {
        detectMotionEventsAsList(
            onDown,
            onMove,
            onUp,
            delayAfterDownInMillis,
            requireUnconsumed,
            pass
        )
    }
)

/**
 * Create a modifier for processing pointer motion input within the region of the modified element.
 *
 * After [AwaitPointerEventScope.awaitFirstDown] returned a [PointerInputChange] then
 * [onDown] is called at first pointer contact.
 * Moving any pointer causes [AwaitPointerEventScope.awaitPointerEvent] then [onMove] is called.
 * When last pointer is up [onUp] is called.
 *
 * To prevent other pointer functions that call [awaitFirstDown]
 * or [AwaitPointerEventScope.awaitPointerEvent]
 * (scroll, swipe, detect functions)
 * receiving changes call [PointerInputChange.consume]  in [onMove]  or call
 * [PointerInputChange.consume] in [onDown] to prevent events
 * that check first pointer interaction.
 *
 * @param onDown is invoked when first pointer is down initially.
 * @param onMove is invoked when one or multiple pointers are being moved on screen.
 * @param onUp is invoked when last pointer is up
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
 * The pointer input handling block will be cancelled and re-started when pointerInput
 * is recomposed with any different [keys].
 */
fun Modifier.pointerMotionEventList(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = Main,
    vararg keys: Any?
) = this.then(
    Modifier.pointerInput(*keys) {
        detectMotionEventsAsList(
            onDown,
            onMove,
            onUp,
            delayAfterDownInMillis,
            requireUnconsumed,
            pass
        )
    }
)
