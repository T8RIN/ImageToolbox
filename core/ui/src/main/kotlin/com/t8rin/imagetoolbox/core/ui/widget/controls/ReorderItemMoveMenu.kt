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

package com.t8rin.imagetoolbox.core.ui.widget.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.LastPage
import com.t8rin.imagetoolbox.core.resources.icons.MoreVert
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun ReorderItemMoveMenu(
    canMoveToStart: Boolean,
    canMoveToEnd: Boolean,
    onMoveToStart: () -> Unit,
    onMoveToEnd: () -> Unit,
    button: @Composable (onClick: () -> Unit) -> Unit = {
        EnhancedIconButton(
            onClick = it,
            forceMinimumInteractiveComponentSize = false,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = null
            )
        }
    },
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier) {
        button { expanded = true }
        EnhancedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = ShapeDefaults.large
        ) {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(horizontal = 8.dp)
            ) {
                EnhancedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        expanded = false
                        scope.launch {
                            delay(300)
                            onMoveToStart()
                        }
                    },
                    shape = ShapeDefaults.top,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    enabled = canMoveToStart
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LastPage,
                        contentDescription = null,
                        modifier = Modifier.scale(scaleX = -1f, scaleY = 1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.move_to_start))
                }
                Spacer(Modifier.height(4.dp))
                EnhancedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        expanded = false
                        scope.launch {
                            delay(300)
                            onMoveToEnd()
                        }
                    },
                    shape = ShapeDefaults.bottom,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    enabled = canMoveToEnd
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LastPage,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.move_to_end))
                }
            }
        }
    }
}