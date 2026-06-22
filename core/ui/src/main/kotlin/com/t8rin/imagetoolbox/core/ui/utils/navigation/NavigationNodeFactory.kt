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

package com.t8rin.imagetoolbox.core.ui.utils.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import kotlin.reflect.KClass

interface NavigationChild {
    @Composable
    fun Content()
}

interface NavigationHost {
    val isUpdateAvailable: Value<Boolean>

    fun navigateBack()

    fun navigateTo(screen: Screen)

    fun navigateToNew(screen: Screen)

    fun replaceTo(screen: Screen)

    fun tryGetUpdate(isNewRequest: Boolean)

    fun updateUris(uris: List<Uri>)
}

interface NavigationNodeFactory {
    val screenClass: KClass<out Screen>

    fun create(
        config: Screen,
        componentContext: ComponentContext,
        host: NavigationHost
    ): NavigationChild
}
