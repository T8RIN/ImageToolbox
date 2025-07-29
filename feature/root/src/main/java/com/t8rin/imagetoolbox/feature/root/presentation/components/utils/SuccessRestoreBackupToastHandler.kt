/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.root.presentation.components.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolbox.feature.root.presentation.screenLogic.RootComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun SuccessRestoreBackupToastHandler(component: RootComponent) {
    val confettiHostState = LocalConfettiHostState.current
    val context = LocalComponentActivity.current
    val toastHostState = LocalToastHostState.current
    LaunchedEffect(component) {
        component.backupRestoredEvents.collectLatest { restored ->
            if (restored) {
                launch {
                    confettiHostState.showConfetti()
                    //Wait for confetti to appear, then trigger font scale adjustment
                    delay(300L)
                    context.recreate()
                }
                toastHostState.showToast(
                    context.getString(R.string.settings_restored),
                    Icons.Rounded.Save
                )
            }
        }
    }
}