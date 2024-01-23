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

package ru.tech.imageresizershrinker.core.ui.widget.sheets

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import ru.tech.imageresizershrinker.core.ui.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.ProvideContainerDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    cancelable: Boolean = true,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    visible: MutableState<Boolean>,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val settingsState = LocalSettingsState.current
    var showSheet by visible

    val autoElevation by animateDpAsState(
        if (settingsState.drawContainerShadows) 16.dp
        else 0.dp
    )
    ProvideContainerDefaults(
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        ModalSheet(
            cancelable = cancelable,
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
                .autoElevatedBorder(
                    shape = BottomSheetDefaults.ExpandedShape,
                    autoElevation = autoElevation
                )
                .autoElevatedBorder(
                    height = 0.dp,
                    shape = BottomSheetDefaults.ExpandedShape,
                    autoElevation = autoElevation
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    cancelable: Boolean = true,
    confirmButton: @Composable RowScope.() -> Unit,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    title: @Composable () -> Unit,
    endConfirmButtonPadding: Dp = 16.dp,
    visible: MutableState<Boolean>,
    enableBackHandler: Boolean = true,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val settingsState = LocalSettingsState.current
    var showSheet by visible

    val autoElevation by animateDpAsState(
        if (settingsState.drawContainerShadows) 16.dp
        else 0.dp
    )

    ProvideContainerDefaults(
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        ModalSheet(
            cancelable = cancelable,
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
                .autoElevatedBorder(
                    shape = BottomSheetDefaults.ExpandedShape,
                    autoElevation = autoElevation
                )
                .autoElevatedBorder(
                    height = 0.dp,
                    shape = BottomSheetDefaults.ExpandedShape,
                    autoElevation = autoElevation
                )
                .animateContentSize(),
            elevation = 0.dp,
            visible = showSheet,
            onVisibleChange = { showSheet = it },
            content = {
                if (showSheet && enableBackHandler) BackHandler { showSheet = false }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    cancelable: Boolean = true,
    confirmButton: (@Composable RowScope.() -> Unit)? = null,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    title: (@Composable () -> Unit)? = null,
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val settingsState = LocalSettingsState.current
    val autoElevation by animateDpAsState(
        if (settingsState.drawContainerShadows) 16.dp
        else 0.dp
    )

    ProvideContainerDefaults(
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        ModalSheet(
            cancelable = cancelable,
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
                .autoElevatedBorder(
                    shape = BottomSheetDefaults.ExpandedShape,
                    autoElevation = autoElevation
                )
                .autoElevatedBorder(
                    height = 0.dp,
                    shape = BottomSheetDefaults.ExpandedShape,
                    autoElevation = autoElevation
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
}