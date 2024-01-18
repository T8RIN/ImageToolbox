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

package ru.tech.imageresizershrinker.core.ui.widget.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.findActivity

@Composable
inline fun <reified VM : ViewModel> ScopedViewModelContainer(
    crossinline content: @Composable VM.(disposable: @Composable () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController(startDestination = 0)
    NavHost(navController) { nav ->
        with(hiltViewModel<VM>()) {
            content {
                DisposableEffect(Unit) {
                    onDispose {
                        if (context.findActivity()?.isChangingConfigurations == false) {
                            navController.navigate(nav + 1)
                        }
                    }
                }
            }
        }
    }
}