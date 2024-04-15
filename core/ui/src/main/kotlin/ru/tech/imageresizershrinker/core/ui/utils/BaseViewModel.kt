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

package ru.tech.imageresizershrinker.core.ui.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.ui.utils.state.update

abstract class BaseViewModel(
    private val dispatchersHolder: DispatchersHolder
) : ViewModel(), DispatchersHolder by dispatchersHolder {

    protected open val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    open val isImageLoading: Boolean
        get() = _isImageLoading.value

    private var imageCalculationJob: Job? = null

    protected open fun debouncedImageCalculation(
        onFinish: suspend () -> Unit = {},
        delay: Long = 600L,
        action: suspend () -> Unit
    ) {
        _isImageLoading.update { false }
        imageCalculationJob?.cancelChildren()
        imageCalculationJob?.cancel()
        imageCalculationJob = viewModelScope.launch {
            _isImageLoading.update { true }
            delay(delay)
            if (!isActive) {
                _isImageLoading.update { false }
                return@launch
            }
            action()
            if (!isActive) {
                _isImageLoading.update { false }
                return@launch
            }
            _isImageLoading.update { false }
            onFinish()
        }
    }

}