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

package com.t8rin.imagetoolbox.feature.help.presentation

import androidx.compose.runtime.Composable
import com.t8rin.imagetoolbox.feature.help.presentation.components.HelpListContent
import com.t8rin.imagetoolbox.feature.help.presentation.components.HelpState
import com.t8rin.imagetoolbox.feature.help.presentation.components.TutorialCategoryContent
import com.t8rin.imagetoolbox.feature.help.presentation.components.TutorialDetailContent
import com.t8rin.imagetoolbox.feature.help.presentation.screenLogic.HelpComponent

@Composable
fun HelpContent(
    component: HelpComponent
) {
    when (val state = component.state) {
        is HelpState.Categories -> {
            HelpListContent(
                categories = state.categories,
                filteredTips = component.filteredTips,
                isSearching = component.isSearching,
                searchKeyword = component.searchKeyword,
                onOpenCategory = component::openCategory,
                onOpenTip = component::openTip,
                onSearchingChange = component::updateIsSearching,
                onSearchKeywordChange = component::updateSearch,
                onGoBack = component.onGoBack
            )
        }

        is HelpState.TutorialCategory -> {
            TutorialCategoryContent(
                category = state.category,
                filteredTips = component.filteredTips,
                isSearching = component.isSearching,
                searchKeyword = component.searchKeyword,
                onOpenTip = component::openTip,
                onSearchingChange = component::updateIsSearching,
                onSearchKeywordChange = component::updateSearch,
                onGoBack = component.onGoBack
            )
        }

        is HelpState.TutorialDetails -> {
            TutorialDetailContent(
                tip = state.tip,
                onGoBack = component.onGoBack,
                onNavigate = component.onNavigate
            )
        }
    }
}