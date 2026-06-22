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

package com.t8rin.imagetoolbox.feature.root.presentation.components.navigation

import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.ui.utils.navigation.NavigationNodeFactory
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.root.presentation.screenLogic.RootComponent
import javax.inject.Inject

internal class ChildProvider @Inject constructor(
    factories: Set<@JvmSuppressWildcards NavigationNodeFactory>
) {
    private val factoriesByScreen = factories.associateBy { it.screenClass }

    fun RootComponent.createChild(
        config: Screen,
        componentContext: ComponentContext
    ): NavigationChild {
        val host = this
        val factory = factoriesByScreen[config::class]
            ?: error("No navigation node factory for ${config::class.qualifiedName}")

        return factory.create(
            config = config,
            componentContext = componentContext,
            host = host
        )
    }
}
