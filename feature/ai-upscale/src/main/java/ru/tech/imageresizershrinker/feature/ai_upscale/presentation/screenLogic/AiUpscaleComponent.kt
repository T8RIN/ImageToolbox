/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.ai_upscale.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.logger.makeLog
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResources
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResourcesStore
import ru.tech.imageresizershrinker.core.ui.utils.BaseComponent
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.ai_upscale.domain.AiUpscaler

class AiUpscaleComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    @Assisted initialUris: List<Uri>?,
    private val upscaler: AiUpscaler<Bitmap>,
    private val remoteResourcesStore: RemoteResourcesStore,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _uris = mutableStateOf(emptyList<Uri>())
    val uris by _uris

    private val _availableModels: MutableState<RemoteResources> =
        mutableStateOf(RemoteResources.Companion.OnnxUpscaleDefault)
    val availableModels by _availableModels

    private val _selectedModel: MutableState<Uri?> = mutableStateOf(null)
    val selectedModel by _selectedModel

    init {
        debounce {
            initialUris?.let(::setUris)
        }
        componentScope.launch {
            remoteResourcesStore.getResources(
                name = availableModels.name,
                forceUpdate = true,
                onDownloadRequest = { name ->
                    remoteResourcesStore.downloadResources(
                        name = name,
                        onProgress = {
                            it.makeLog("LOADING")
                        },
                        onFailure = {},
                        downloadOnlyNewData = true
                    )
                }
            )?.let { models ->
                _availableModels.update { models }
                _selectedModel.update { models.list.first().uri.toUri() }
            }
        }
    }

    fun setUris(uris: List<Uri>) {
        _uris.update { uris }
    }

    fun preformUpscale(
        onSuccess: (Bitmap) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        componentScope.launch {
            upscaler.upscale(
                imageUri = uris.first().toString(),
                upscaleModelUri = selectedModel?.toString() ?: ""
            ).onSuccess(onSuccess).onFailure(onFailure)
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
            initialUris: List<Uri>?,
        ): AiUpscaleComponent
    }

}