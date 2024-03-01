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

package ru.tech.imageresizershrinker.core.ui.widget.other

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    canCancel: Boolean = true,
    onCancelLoading: () -> Unit = {}
) {
    var showWantDismissDialog by remember(canCancel) { mutableStateOf(false) }
    BasicAlertDialog(onDismissRequest = { showWantDismissDialog = canCancel }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        showWantDismissDialog = canCancel
                    }
                },
            contentAlignment = Alignment.Center
        ) { Loading(modifier = Modifier.size(108.dp)) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    done: Int,
    left: Int,
    canCancel: Boolean = true,
    onCancelLoading: () -> Unit
) {
    var showWantDismissDialog by remember(canCancel) { mutableStateOf(false) }
    BasicAlertDialog(onDismissRequest = { showWantDismissDialog = canCancel }) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        showWantDismissDialog = canCancel
                    }
                }
        ) { Loading(done, left) }
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