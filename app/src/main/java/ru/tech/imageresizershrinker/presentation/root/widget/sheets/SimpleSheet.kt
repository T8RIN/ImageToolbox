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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.modalsheet.ModalSheet
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    sheetContent: @Composable ColumnScope.() -> Unit,
    dragHandle: @Composable ColumnScope.() -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            BottomSheetDefaults.DragHandle()
        }
    },
    visible: MutableState<Boolean>
) {
    val settingsState = LocalSettingsState.current
    var showSheet by visible

    ModalSheet(
        nestedScrollEnabled = nestedScrollEnabled,
        animationSpec = tween(
            durationMillis = 600,
            easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
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
            .fabBorder(
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
            )
            .fabBorder(
                height = 0.dp,
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
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
            easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
        ),
        dragHandle = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)),
                horizontalArrangement = Arrangement.Center
            ) {
                BottomSheetDefaults.DragHandle()
            }
        },
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
            .fabBorder(
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
            )
            .fabBorder(
                height = 0.dp,
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
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
    title: (@Composable () -> Unit)? = null,
    visible: Boolean,
    onDismiss: (Boolean) -> Unit
) {
    val settingsState = LocalSettingsState.current
    ModalSheet(
        nestedScrollEnabled = nestedScrollEnabled,
        animationSpec = tween(
            durationMillis = 600,
            easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
        ),
        dragHandle = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)),
                horizontalArrangement = Arrangement.Center
            ) {
                BottomSheetDefaults.DragHandle()
            }
        },
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
            .fabBorder(
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
            )
            .fabBorder(
                height = 0.dp,
                shape = BottomSheetDefaults.ExpandedShape,
                elevation = 16.dp
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