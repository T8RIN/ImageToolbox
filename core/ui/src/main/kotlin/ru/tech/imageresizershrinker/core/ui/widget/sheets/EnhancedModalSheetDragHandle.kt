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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedModalSheetDragHandle(
    modifier: Modifier = Modifier,
    color: Color = EnhancedBottomSheetDefaults.barContainerColor,
    drawStroke: Boolean = true,
    showDragHandle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val dragHandleWidth = LocalSettingsState.current.dragHandleWidth
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
            if (showDragHandle && dragHandleWidth > 0.dp) {
                BottomSheetDefaults.DragHandle(
                    width = dragHandleWidth,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
                )
            }
        }
        content()
    }
}