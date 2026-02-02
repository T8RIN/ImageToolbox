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

package com.t8rin.imagetoolbox.core.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.navigation.coroutineScope
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.logger.makeLog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.launch as internalLaunch

@Stable
@Immutable
abstract class BaseComponent(
    private val dispatchersHolder: DispatchersHolder,
    private val componentContext: ComponentContext
) : ComponentContext by componentContext, DispatchersHolder by dispatchersHolder {

    val componentScope = coroutineScope

    protected open val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    open val isImageLoading: Boolean by _isImageLoading

    private var imageCalculationJob: Job? by smartJob {
        _isImageLoading.update { false }
    }

    inline fun debounce(
        time: Long = 150,
        crossinline block: suspend () -> Unit
    ) {
        componentScope.launch {
            delay(time)
            block()
        }
    }

    protected open val _haveChanges: MutableState<Boolean> = mutableStateOf(false)
    open val haveChanges: Boolean by _haveChanges

    fun trackProgress(
        action: suspend KeepAliveService.() -> Unit
    ): Job = componentScope.launch {
        keepAliveService.track(
            onFailure = { it.makeLog("CRITICAL") },
            action = action
        )
    }

    protected fun registerSave() {
        _haveChanges.update { false }
    }

    protected fun registerChangesCleared() {
        _haveChanges.update { false }
    }

    protected fun registerChanges() {
        _haveChanges.update { true }
    }

    private var resetJob by smartJob()

    protected open fun debouncedImageCalculation(
        onFinish: suspend () -> Unit = {},
        delay: Long = 600L,
        action: suspend () -> Unit
    ) {
        imageCalculationJob = componentScope.launch(
            CoroutineExceptionHandler { _, t ->
                t.makeLog()
                _isImageLoading.update { false }
            } + defaultDispatcher
        ) {
            _isImageLoading.update { true }
            delay(delay)

            ensureActive()

            action()

            ensureActive()

            _isImageLoading.update { false }

            onFinish()
        }
    }

    private fun cancelResetState() {
        resetJob?.cancel()
        resetJob = null
    }

    private fun resetStateDelayed() {
        resetJob = componentScope.launch {
            delay(1500)
            resetState()
        }
    }

    @Composable
    fun AttachLifecycle() {
        DisposableEffect(Unit) {
            cancelResetState()
            onDispose {
                resetStateDelayed()
            }
        }
    }

    fun cancelImageLoading() {
        _isImageLoading.update { false }
        imageCalculationJob?.cancel()
        imageCalculationJob = null
    }

    open fun resetState(): Unit =
        throw IllegalAccessException("Cannot reset state of ${this::class.simpleName}")

    fun CoroutineScope.launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = internalLaunch(context, start) {
        delay(50L)
        block()
    }

    companion object {
        internal var keepAliveService: KeepAliveService = object : KeepAliveService {
            override fun updateOrStart(
                title: String,
                description: String,
                progress: Float
            ) = Unit

            override fun stop(removeNotification: Boolean) = Unit
        }

        fun inject(keepAliveService: KeepAliveService) {
            this.keepAliveService = keepAliveService
        }
    }
}