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

package ru.tech.imageresizershrinker.feature.root.presentation.components.dialogs

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.feature.root.presentation.screenLogic.RootComponent


@Composable
internal fun AppExitDialog(component: RootComponent) {
    val currentScreen =
        component.childStack.subscribeAsState().value.items.lastOrNull()?.configuration

    if (currentScreen == Screen.Main) {
        val context = LocalComponentActivity.current

        var showExitDialog by rememberSaveable { mutableStateOf(false) }

        val tiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        BackHandler(enabled = !tiramisu) {
            if (component.shouldShowDialog) showExitDialog = true
            else context.finishAffinity()
        }

        AppExitDialog(
            onDismiss = { showExitDialog = false },
            visible = showExitDialog && !tiramisu
        )
    }
}

@Composable
private fun AppExitDialog(
    onDismiss: () -> Unit,
    visible: Boolean
) {
    val activity = LocalComponentActivity.current

    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = activity::finishAffinity
            ) {
                Text(stringResource(R.string.close))
            }
        },
        confirmButton = {
            EnhancedButton(onClick = onDismiss) {
                Text(stringResource(R.string.stay))
            }
        },
        title = { Text(stringResource(R.string.app_closing)) },
        text = {
            Text(
                stringResource(R.string.app_closing_sub),
                textAlign = TextAlign.Center
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.DoorBack,
                contentDescription = null
            )
        }
    )
}