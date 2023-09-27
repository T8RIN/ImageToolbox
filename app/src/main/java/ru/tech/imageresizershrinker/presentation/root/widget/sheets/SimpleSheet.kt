package ru.tech.imageresizershrinker.presentation.root.widget.sheets

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.t8rin.modalsheet.ModalSheet
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    sheetContent: @Composable ColumnScope.() -> Unit,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    visible: MutableState<Boolean>
) {
    val settingsState = LocalSettingsState.current
    var showSheet by visible

    ModalSheet(
        nestedScrollEnabled = nestedScrollEnabled,
        animationSpec = tween(
            durationMillis = 600,
            easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)
        ),
        dragHandle = dragHandle,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        sheetModifier = Modifier
            .statusBarsPadding()
            .offset(y = (settingsState.borderWidth + 1.dp))
            .border(
                width = settingsState.borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.3f,
                    onTopOf = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                ),
                shape = BottomSheetDefaults.ExpandedShape
            )
            .autoElevatedBorder(
                shape = BottomSheetDefaults.ExpandedShape,
                autoElevation = 16.dp
            )
            .autoElevatedBorder(
                height = 0.dp,
                shape = BottomSheetDefaults.ExpandedShape,
                autoElevation = 16.dp
            )
            .animateContentSize(),
        elevation = 0.dp,
        visible = showSheet,
        onVisibleChange = { showSheet = it },
        content = {
            if (showSheet) BackHandler { showSheet = false }
            sheetContent()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    sheetContent: @Composable ColumnScope.() -> Unit,
    confirmButton: @Composable RowScope.() -> Unit,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    title: @Composable () -> Unit,
    endConfirmButtonPadding: Dp = 16.dp,
    visible: MutableState<Boolean>
) {
    val settingsState = LocalSettingsState.current
    var showSheet by visible

    ModalSheet(
        nestedScrollEnabled = nestedScrollEnabled,
        animationSpec = tween(
            durationMillis = 600,
            easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)
        ),
        dragHandle = dragHandle,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        sheetModifier = Modifier
            .statusBarsPadding()
            .offset(y = (settingsState.borderWidth + 1.dp))
            .border(
                width = settingsState.borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.3f,
                    onTopOf = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                ),
                shape = BottomSheetDefaults.ExpandedShape
            )
            .autoElevatedBorder(
                shape = BottomSheetDefaults.ExpandedShape,
                autoElevation = 16.dp
            )
            .autoElevatedBorder(
                height = 0.dp,
                shape = BottomSheetDefaults.ExpandedShape,
                autoElevation = 16.dp
            )
            .animateContentSize(),
        elevation = 0.dp,
        visible = showSheet,
        onVisibleChange = { showSheet = it },
        content = {
            if (showSheet) BackHandler { showSheet = false }
            Column(
                modifier = Modifier.weight(1f, false),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = sheetContent
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawHorizontalStroke(true, autoElevation = 6.dp)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .padding(end = endConfirmButtonPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                title()
                Spacer(modifier = Modifier.weight(1f))
                confirmButton()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    sheetContent: @Composable ColumnScope.() -> Unit,
    confirmButton: (@Composable RowScope.() -> Unit)? = null,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    title: (@Composable () -> Unit)? = null,
    visible: Boolean,
    onDismiss: (Boolean) -> Unit
) {
    val settingsState = LocalSettingsState.current
    ModalSheet(
        nestedScrollEnabled = nestedScrollEnabled,
        animationSpec = tween(
            durationMillis = 600,
            easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)
        ),
        dragHandle = dragHandle,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        sheetModifier = Modifier
            .statusBarsPadding()
            .offset(y = (settingsState.borderWidth + 1.dp))
            .border(
                width = settingsState.borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.3f,
                    onTopOf = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                ),
                shape = BottomSheetDefaults.ExpandedShape
            )
            .autoElevatedBorder(
                shape = BottomSheetDefaults.ExpandedShape,
                autoElevation = 16.dp
            )
            .autoElevatedBorder(
                height = 0.dp,
                shape = BottomSheetDefaults.ExpandedShape,
                autoElevation = 16.dp
            )
            .animateContentSize(),
        elevation = 0.dp,
        visible = visible,
        onVisibleChange = onDismiss,
        content = {
            if (visible) BackHandler { onDismiss(false) }
            Column(
                modifier = Modifier.weight(1f, false),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = sheetContent
            )
            if (confirmButton != null && title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawHorizontalStroke(true, autoElevation = 6.dp)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    title()
                    Spacer(modifier = Modifier.weight(1f))
                    confirmButton()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDragHandle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp),
    drawStroke: Boolean = true,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier
            .then(
                if (drawStroke) {
                    Modifier
                        .drawHorizontalStroke(autoElevation = 3.dp)
                        .zIndex(Float.MAX_VALUE)
                } else Modifier
            )
            .background(color),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            BottomSheetDefaults.DragHandle()
        }
        content()
    }
}