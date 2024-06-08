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

package ru.tech.imageresizershrinker.core.ui.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop

@Composable
fun <T> NavController<T>.currentDestination(): T? = remember(backstack.entries) {
    derivedStateOf {
        backstack.entries.lastOrNull()?.destination
    }
}.value

fun <T> NavController<T>.navigateNew(destination: T): Boolean {
    if (backstack.entries.lastOrNull()?.destination != destination) {
        navigate(destination)
        return true
    }

    return false
}

fun <T> NavController<T>.safePop(): Boolean = if (backstack.entries.size > 1) pop() else false