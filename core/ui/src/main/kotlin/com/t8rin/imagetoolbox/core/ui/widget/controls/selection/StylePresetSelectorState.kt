/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
class StylePresetSelectorState internal constructor(
    showSheet: Boolean = false,
    styleToDelete: String = "",
    showDeleteDialog: Boolean = false
) {
    var showSheet by mutableStateOf(showSheet)
        internal set
    var styleToDelete by mutableStateOf(styleToDelete)
        internal set
    var showDeleteDialog by mutableStateOf(showDeleteDialog)
        internal set

    fun hide() {
        showSheet = false
        showDeleteDialog = false
    }

    internal companion object {
        val Saver = listSaver<StylePresetSelectorState, Any>(
            save = { listOf(it.showSheet, it.styleToDelete, it.showDeleteDialog) },
            restore = {
                StylePresetSelectorState(
                    it[0] as Boolean,
                    it[1] as String,
                    it[2] as Boolean
                )
            }
        )
    }
}

@Composable
fun rememberStylePresetSelectorState(): StylePresetSelectorState =
    rememberSaveable(saver = StylePresetSelectorState.Saver) { StylePresetSelectorState() }