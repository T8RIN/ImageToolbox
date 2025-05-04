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

package ru.tech.imageresizershrinker.core.ui.widget.dialogs

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.BasicEnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.KeepScreenOn
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator

@Composable
fun LoadingDialog(
    visible: Boolean,
    onCancelLoading: () -> Unit = {},
    canCancel: Boolean = true,
    isForSaving: Boolean = true
) {
    var showWantDismissDialog by remember(canCancel, visible) { mutableStateOf(false) }
    BasicEnhancedAlertDialog(
        visible = visible,
        onDismissRequest = { showWantDismissDialog = canCancel }
    ) {
        val focus = LocalFocusManager.current
        LaunchedEffect(focus) {
            focus.clearFocus()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        showWantDismissDialog = canCancel
                    }
                },
            contentAlignment = Alignment.Center,
            content = {
                LoadingIndicator(modifier = Modifier.size(108.dp))
            }
        )
    }
    WantCancelLoadingDialog(
        visible = showWantDismissDialog,
        onCancelLoading = onCancelLoading,
        onDismissDialog = {
            showWantDismissDialog = false
        },
        isForSaving = isForSaving
    )
    KeepScreenOn()
}

@Composable
fun LoadingDialog(
    visible: Boolean,
    done: Int,
    left: Int,
    onCancelLoading: () -> Unit,
    canCancel: Boolean = true,
) {
    if (left < 0) {
        LoadingDialog(
            visible = visible,
            onCancelLoading = onCancelLoading,
            canCancel = canCancel && visible
        )
    } else {
        ProgressLoadingDialog(
            visible = visible,
            done = done,
            left = left,
            onCancelLoading = onCancelLoading,
            canCancel = canCancel && visible
        )
    }
}

@Composable
private fun ProgressLoadingDialog(
    visible: Boolean,
    done: Int,
    left: Int,
    onCancelLoading: () -> Unit,
    canCancel: Boolean = true,
) {
    var showWantDismissDialog by remember(canCancel, visible) { mutableStateOf(false) }
    BasicEnhancedAlertDialog(
        visible = visible,
        onDismissRequest = { showWantDismissDialog = canCancel }
    ) {
        val focus = LocalFocusManager.current
        LaunchedEffect(focus) {
            focus.clearFocus()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        showWantDismissDialog = canCancel
                    }
                },
            contentAlignment = Alignment.Center,
            content = {
                LoadingIndicator(
                    done = done,
                    left = left
                )
            }
        )
    }
    WantCancelLoadingDialog(
        visible = showWantDismissDialog,
        onCancelLoading = onCancelLoading,
        onDismissDialog = {
            showWantDismissDialog = false
        }
    )
    KeepScreenOn()
}