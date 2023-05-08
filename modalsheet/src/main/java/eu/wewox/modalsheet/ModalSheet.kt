package eu.wewox.modalsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modal sheet that behaves like bottom sheet and draws over system UI.
 *
 * @param data Value that determines the content of the sheet. Sheet is closed (or remains closed) when null is passed.
 * @param onDataChange Called when data changes as a result of the visibility change.
 * @param cancelable When true, this modal sheet can be closed with swipe gesture, tap on scrim or tap on hardware back
 * button. Note: passing 'false' does not disable the interaction with the sheet. Only the resulting state after the
 * sheet settles.
 * @param shape The shape of the bottom sheet.
 * @param elevation The elevation of the bottom sheet.
 * @param backgroundColor The background color of the bottom sheet.
 * @param contentColor The preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [backgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
 * @param scrimColor The color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 * @param content The content of the bottom sheet.
 */
@ExperimentalSheetApi
@Composable
fun <T> ModalSheet(
    data: T?,
    onDataChange: (T?) -> Unit,
    cancelable: Boolean = true,
    shape: Shape = ModalSheetDefaults.shape,
    elevation: Dp = ModalSheetDefaults.elevation,
    backgroundColor: Color = ModalSheetDefaults.backgroundColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(backgroundColor),
    scrimColor: Color = ModalSheetDefaults.scrimColor,
    content: @Composable ColumnScope.(T) -> Unit,
) {
    var lastNonNullData by remember { mutableStateOf(data) }
    DisposableEffect(data) {
        if (data != null) lastNonNullData = data
        onDispose {}
    }

    ModalSheet(
        visible = data != null,
        onVisibleChange = { visible ->
            if (visible) {
                onDataChange(lastNonNullData)
            } else {
                onDataChange(null)
            }
        },
        cancelable = cancelable,
        shape = shape,
        elevation = elevation,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        scrimColor = scrimColor,
    ) {
        lastNonNullData?.let {
            content(it)
        }
    }
}

/**
 * Modal sheet that behaves like bottom sheet and draws over system UI.
 * Should be used on with the content which is not dependent on the outer data. For dynamic content use [ModalSheet]
 * overload with a 'data' parameter.
 *
 * @param visible True if modal should be visible.
 * @param onVisibleChange Called when visibility changes.
 * @param cancelable When true, this modal sheet can be closed with swipe gesture, tap on scrim or tap on hardware back
 * button. Note: passing 'false' does not disable the interaction with the sheet. Only the resulting state after the
 * sheet settles.
 * @param shape The shape of the bottom sheet.
 * @param elevation The elevation of the bottom sheet.
 * @param backgroundColor The background color of the bottom sheet.
 * @param contentColor The preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [backgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
 * @param scrimColor The color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 * @param content The content of the bottom sheet.
 */
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalSheetApi
@Composable
fun ModalSheet(
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    cancelable: Boolean = true,
    skipHalfExpanded: Boolean = true,
    shape: Shape = ModalSheetDefaults.shape,
    elevation: Dp = ModalSheetDefaults.elevation,
    backgroundColor: Color = ModalSheetDefaults.backgroundColor,
    contentColor: Color = contentColorFor(backgroundColor),
    scrimColor: Color = ModalSheetDefaults.scrimColor,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipHalfExpanded = skipHalfExpanded,
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            // Intercept and disallow hide gesture / action
            if (it == ModalBottomSheetValue.Hidden && !cancelable) {
                return@rememberModalBottomSheetState false
            }

            onVisibleChange(it == ModalBottomSheetValue.Expanded)

            true
        },
    )

    LaunchedEffect(visible) {
        if (visible) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (!visible && sheetState.currentValue == sheetState.targetValue && !sheetState.isVisible) {
        return
    }

    ModalSheet(
        sheetState = sheetState,
        onDismiss = {
            if (cancelable) {
                onVisibleChange(false)
            }
        },
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        scrimColor = scrimColor,
        content = content,
    )
}

/**
 * Modal sheet that behaves like bottom sheet and draws over system UI.
 * Takes [ModalBottomSheetState] as parameter to fine-tune sheet behavior.
 *
 * Note: In this case [ModalSheet] is always added to the composition. See [ModalSheet] overload with visible parameter,
 * or data object to conditionally add / remove modal sheet to / from the composition.
 *
 * @param sheetState The state of the underlying Material bottom sheet.
 * @param onDismiss Called when user taps on the hardware back button.
 * @param shape The shape of the bottom sheet.
 * @param elevation The elevation of the bottom sheet.
 * @param backgroundColor The background color of the bottom sheet.
 * @param contentColor The preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [backgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
 * @param scrimColor The color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 * @param content The content of the bottom sheet.
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@ExperimentalSheetApi
@Composable
fun ModalSheet(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState,
    onDismiss: (() -> Unit)?,
    shape: Shape = ModalSheetDefaults.shape,
    elevation: Dp = ModalSheetDefaults.elevation,
    backgroundColor: Color = ModalSheetDefaults.backgroundColor,
    contentColor: Color = contentColorFor(backgroundColor),
    scrimColor: Color = ModalSheetDefaults.scrimColor,
    content: @Composable ColumnScope.() -> Unit,
) {
    FullscreenPopup(
        onDismiss = onDismiss,
    ) {
        Box(Modifier.fillMaxSize()) {
            ModalBottomSheetLayout(
                modifier = modifier.align(Alignment.BottomCenter),
                sheetState = sheetState,
                sheetShape = shape,
                sheetElevation = elevation,
                sheetBackgroundColor = backgroundColor,
                sheetContentColor = contentColor,
                scrimColor = scrimColor,
                sheetContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BottomSheetDefaults.DragHandle()
                    }
                    content()
                },
                content = { /* Empty */ }
            )
        }
    }
}

/**
 * Contains the default values used by [ModalSheet].
 */
@ExperimentalSheetApi
object ModalSheetDefaults {

    /**
     * Default shape of the bottom sheet.
     */
    val shape: Shape
        @Composable
        get() = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    /**
     * Default elevation of the bottom sheet.
     */
    val elevation: Dp
        @Composable
        get() = 8.dp

    /**
     * Default background color of the bottom sheet.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    val backgroundColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)

    /**
     * Default color of the scrim that is applied to the rest of the screen when the bottom sheet
     * is visible.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    val scrimColor: Color
        @Composable
        get() = BottomSheetDefaults.ScrimColor
}
