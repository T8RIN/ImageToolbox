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

package com.t8rin.imagetoolbox.collage_maker.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.UndoRedoButtons
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet

@Composable
internal fun CollageMakerActions(
    component: CollageMakerComponent,
    onToggleControls: () -> Unit
) {
    var editSheetData by remember {
        mutableStateOf(listOf<Uri>())
    }
    EnhancedIconButton(
        onClick = onToggleControls
    ) {
        Icon(
            imageVector = Icons.Rounded.Tune,
            contentDescription = stringResource(R.string.properties)
        )
    }
    ShareButton(
        onShare = component::performSharing,
        onCopy = {
            component.cacheImage(Clipboard::copy)
        },
        onEdit = {
            component.cacheImage {
                editSheetData = listOf(it)
            }
        }
    )
    UndoRedoButtons(
        canUndo = component.canUndo,
        canRedo = component.canRedo,
        onUndo = component::undo,
        onRedo = component::redo,
        modifier = Modifier.padding(2.dp)
    )
    ProcessImagesPreferenceSheet(
        uris = editSheetData,
        visible = editSheetData.isNotEmpty(),
        onDismiss = {
            editSheetData = emptyList()
        },
        onNavigate = component.onNavigate
    )
}