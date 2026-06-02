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

package com.t8rin.imagetoolbox.feature.help.presentation.screenLogic

import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.help.data.HelpCategory
import com.t8rin.imagetoolbox.feature.help.data.HelpRepository
import com.t8rin.imagetoolbox.feature.help.data.HelpTip
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HelpComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted("categoryName") initialCategory: String?,
    @Assisted("tipId") initialTipId: String?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit
) : ComponentContext by componentContext {

    val selectedTip: HelpTip? = initialTipId?.let(HelpRepository::getTip)

    val selectedCategory: HelpCategory? = selectedTip?.category
        ?: initialCategory?.let(HelpRepository::getCategory)

    val categories: List<HelpCategory> get() = HelpRepository.categories
    val tips: List<HelpTip> get() = HelpRepository.tips

    fun tipsFor(category: HelpCategory) = HelpRepository.getTipsForCategory(category)

    fun openCategory(category: HelpCategory) {
        onNavigate(
            Screen.Help(
                categoryName = category.key
            )
        )
    }

    fun openTip(tip: HelpTip) {
        onNavigate(
            Screen.Help(
                categoryName = tip.category.key,
                tipId = tip.id
            )
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("categoryName")
            initialCategory: String?,
            @Assisted("tipId")
            initialTipId: String?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): HelpComponent
    }
}
