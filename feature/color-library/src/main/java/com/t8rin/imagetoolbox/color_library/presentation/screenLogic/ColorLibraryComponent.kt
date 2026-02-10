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

package com.t8rin.imagetoolbox.color_library.presentation.screenLogic

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.smarttoolfactory.colordetector.parser.ColorNameParser
import com.smarttoolfactory.colordetector.parser.ColorWithName
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ColorLibraryComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private var baseColors: List<ColorWithName> = emptyList()

    private val _colors: MutableState<List<ColorWithName>> = mutableStateOf(emptyList())
    val colors by _colors

    private val _searchKeyword: MutableState<String> = mutableStateOf("")
    val searchKeyword by _searchKeyword

    init {
        componentScope.launch {
            baseColors = ColorNameParser.colorNames.values.sortedBy { it.name }.toList()

            _colors.value = baseColors
        }
    }

    fun updateSearch(keyword: String) {
        _searchKeyword.value = keyword

        debouncedImageCalculation(
            delay = 350
        ) {
            if (keyword.isBlank()) {
                _colors.value = baseColors
            } else {
                _colors.value = baseColors.filter {
                    it.name.contains(
                        other = searchKeyword,
                        ignoreCase = true
                    ) || searchKeyword.contains(
                        other = it.name,
                        ignoreCase = true
                    ) || it.color.toHex().contains(
                        other = searchKeyword,
                        ignoreCase = true
                    )
                }
            }
        }
    }

    fun clearSearch() {
        updateSearch("")
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
        ): ColorLibraryComponent
    }

}