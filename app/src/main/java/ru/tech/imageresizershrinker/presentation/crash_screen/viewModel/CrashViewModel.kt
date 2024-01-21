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

package ru.tech.imageresizershrinker.presentation.crash_screen.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.settings.domain.use_case.GetSettingsStateFlowUseCase
import ru.tech.imageresizershrinker.core.settings.domain.use_case.GetSettingsStateUseCase
import javax.inject.Inject

@HiltViewModel
class CrashViewModel @Inject constructor(
    private val getSettingsStateUseCase: GetSettingsStateUseCase,
    getSettingsStateFlowUseCase: GetSettingsStateFlowUseCase,
) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    init {
        runBlocking {
            _settingsState.value = getSettingsStateUseCase()
        }
        getSettingsStateFlowUseCase().onEach {
            _settingsState.value = it
        }.launchIn(viewModelScope)
    }
}