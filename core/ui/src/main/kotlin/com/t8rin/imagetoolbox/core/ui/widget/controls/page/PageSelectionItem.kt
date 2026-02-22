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

package com.t8rin.imagetoolbox.core.ui.widget.controls.page

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pages
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.utils.getString

@Composable
fun PageSelectionItem(
    value: List<Int>?,
    onValueChange: (List<Int>) -> Unit,
    pagesCount: Int
) {
    var showSelector by rememberSaveable {
        mutableStateOf(false)
    }
    PreferenceItem(
        title = stringResource(R.string.pages_selection),
        subtitle = remember(value, pagesCount) {
            derivedStateOf {
                value?.takeIf { it.isNotEmpty() }
                    ?.let {
                        if (it.size == pagesCount) {
                            getString(R.string.all)
                        } else {
                            PagesSelectionParser.formatPageOutput(it)
                        }
                    } ?: getString(R.string.none)
            }
        }.value,
        onClick = {
            showSelector = true
        },
        modifier = Modifier.fillMaxWidth(),
        startIcon = Icons.Rounded.Pages,
        endIcon = Icons.Rounded.MiniEdit
    )
    PageInputDialog(
        visible = showSelector,
        onDismiss = { showSelector = false },
        value = value,
        onValueChange = onValueChange
    )
}