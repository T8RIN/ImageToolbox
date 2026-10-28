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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Place
import com.t8rin.imagetoolbox.core.settings.domain.model.DialogPosition
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
fun DialogPositionSettingItem(
    onValueChange: (DialogPosition) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val position = settingsState.dialogPosition
    val entries = DialogPosition.entries

    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = modifier,
        title = stringResource(R.string.dialog_position),
        subtitle = position.title,
        startIcon = Icons.Outlined.Place,
        endIcon = Icons.Rounded.MiniEdit,
        shape = shape,
        onClick = { showDialog = true }
    )

    EnhancedAlertDialog(
        visible = showDialog,
        onDismissRequest = { showDialog = false },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.dialog_position)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.dialog_position_sub))
                EnhancedButtonGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .container(shape = ShapeDefaults.default),
                    items = entries.map { it.title },
                    selectedIndex = entries.indexOf(position),
                    onIndexChange = {
                        onValueChange(entries[it])
                    },
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                    isScrollable = false
                )
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = { showDialog = false },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

private val DialogPosition.title: String
    @Composable
    get() = stringResource(
        when (this) {
            DialogPosition.Center -> R.string.center_position
            DialogPosition.Top -> R.string.top
            DialogPosition.Bottom -> R.string.bottom
        }
    )