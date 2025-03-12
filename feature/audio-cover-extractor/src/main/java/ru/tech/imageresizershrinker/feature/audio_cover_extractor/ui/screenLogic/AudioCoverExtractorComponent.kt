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

package ru.tech.imageresizershrinker.feature.audio_cover_extractor.ui.screenLogic

import android.net.Uri
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.ui.utils.BaseComponent
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.feature.audio_cover_extractor.domain.AudioCoverRetriever
import ru.tech.imageresizershrinker.feature.audio_cover_extractor.ui.components.AudioWithCover

class AudioCoverExtractorComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val audioCoverRetriever: AudioCoverRetriever,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateCovers)
        }
    }

    private val _covers: MutableStateFlow<List<AudioWithCover>> = MutableStateFlow(emptyList())
    val covers: StateFlow<List<AudioWithCover>> = _covers.asStateFlow()


    fun updateCovers(uris: List<Uri>) {
        val audioUris = uris.distinct()

        componentScope.launch {
            _covers.update {
                audioUris.map {
                    AudioWithCover(
                        audioUri = it,
                        imageCoverUri = null,
                        isLoading = true
                    )
                }
            }

            val newCovers = audioUris.map { audioUri ->
                async {
                    val coverUri = audioCoverRetriever.loadCover(audioUri.toString()).getOrNull()

                    val newCover = AudioWithCover(
                        audioUri = audioUri,
                        imageCoverUri = coverUri?.toUri(),
                        isLoading = false
                    )

                    _covers.update { covers ->
                        covers.toMutableList().apply {
                            val index = indexOfFirst { it.audioUri == audioUri }.takeIf { it >= 0 }
                                ?: return@update covers

                            set(index, newCover)
                        }
                    }

                    newCover
                }
            }

            _covers.update {
                newCovers.awaitAll().filter { it.imageCoverUri != null }
            }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): AudioCoverExtractorComponent
    }
}