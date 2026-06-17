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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.help.domain.HelpRepository
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpCategory
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpPage
import com.t8rin.imagetoolbox.feature.help.domain.model.HelpTip
import com.t8rin.imagetoolbox.feature.help.presentation.components.HelpState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.Locale

class HelpComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted("categoryName") initialCategory: String?,
    @Assisted("tipId") initialTipId: String?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    helpRepository: HelpRepository,
    resourceManager: ResourceManager,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext), ResourceManager by resourceManager {

    private val baseTips: List<HelpTip> = initialCategory
        ?.let(helpRepository::getCategory)
        ?.let(helpRepository::getTipsForCategory)
        ?: helpRepository.tips

    private val _isSearching: MutableState<Boolean> = mutableStateOf(false)
    val isSearching by _isSearching

    private val _searchKeyword: MutableState<String> = mutableStateOf("")
    val searchKeyword by _searchKeyword

    private val _filteredTips: MutableState<List<HelpTip>> = mutableStateOf(baseTips)
    val filteredTips by _filteredTips

    val state: HelpState = run {
        initialTipId?.let(helpRepository::getTip)?.let {
            return@run HelpState.TutorialDetails(it)
        }

        initialCategory?.let(helpRepository::getCategory)?.let {
            return@run HelpState.TutorialCategory(it)
        }

        HelpState.Categories(helpRepository.categories)
    }

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

    fun updateIsSearching(isSearching: Boolean) {
        _isSearching.value = isSearching
        if (!isSearching) {
            clearSearch()
        }
    }

    fun updateSearch(keyword: String) {
        _searchKeyword.value = keyword

        if (keyword.isBlank()) {
            updateFilteredTips()
            return
        }

        debouncedImageCalculation(
            delay = 350,
            action = { updateFilteredTips() }
        )
    }

    private fun clearSearch() {
        updateSearch("")
    }

    private fun updateFilteredTips() {
        val keyword = searchKeyword

        _filteredTips.value = if (keyword.isBlank()) {
            baseTips
        } else {
            baseTips.filter { it.matchesSearch(keyword) }
        }
    }

    private fun HelpTip.matchesSearch(keyword: String): Boolean {
        return title.matchesSearch(keyword) ||
                subtitle.matchesSearch(keyword) ||
                category.matchesSearch(keyword) ||
                pages.any { it.matchesSearch(keyword) }
    }

    private fun HelpCategory.matchesSearch(keyword: String): Boolean {
        return title.matchesSearch(keyword) ||
                subtitle.matchesSearch(keyword)
    }

    private fun HelpPage.matchesSearch(keyword: String): Boolean {
        return title.matchesSearch(keyword) ||
                description.matchesSearch(keyword) ||
                steps.any { it.matchesSearch(keyword) }
    }

    private fun Int.matchesSearch(keyword: String): Boolean {
        return getString(this).contains(
            other = keyword,
            ignoreCase = true
        ) || getStringLocalized(
            resId = this,
            language = Locale.ENGLISH.language
        ).contains(
            other = keyword,
            ignoreCase = true
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
