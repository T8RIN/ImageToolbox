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

package com.t8rin.imagetoolbox.feature.emoji_kitchen.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenCombination
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenEmoji
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenFavorite
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenFavoriteCombination
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenRepository
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenState
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenVariant
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class EmojiKitchenComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val repository: EmojiKitchenRepository,
    private val fileController: FileController,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<Pair<EmojiKitchenEmoji?, EmojiKitchenEmoji?>>(
    dispatchersHolder,
    componentContext
) {

    private val _collectionState = mutableStateOf(repository.state.value)
    val collectionState: EmojiKitchenState by _collectionState

    private val _first = mutableStateOf<EmojiKitchenEmoji?>(null)
    val first: EmojiKitchenEmoji? by _first

    private val _second = mutableStateOf<EmojiKitchenEmoji?>(null)
    val second: EmojiKitchenEmoji? by _second

    private val _activeSlot = mutableIntStateOf(0)
    val activeSlot: Int by _activeSlot

    private val _selectedVariant = mutableStateOf<EmojiKitchenVariant?>(null)
    val selectedVariant: EmojiKitchenVariant? by _selectedVariant

    private val compatibility = mutableStateOf<Pair<String, Set<String>>?>(null)
    val compatibleEmojis: Set<String>
        get() = compatibility.value?.second.orEmpty()

    private val otherEmojiId: String?
        get() = if (activeSlot == 0) second?.id else first?.id

    private val _exploreEmoji = mutableStateOf<EmojiKitchenEmoji?>(null)
    val exploreEmoji: EmojiKitchenEmoji? by _exploreEmoji

    private val _isExploringLoading = mutableStateOf(false)
    val isExploringLoading: Boolean by _isExploringLoading

    private val _combinations = mutableStateOf<List<EmojiKitchenCombination>?>(null)
    val combinations: List<EmojiKitchenCombination>
        get() = _combinations.value.orEmpty()

    private val _favorites = mutableStateOf(repository.favorites.value)
    val favorites: Set<EmojiKitchenFavorite> by _favorites

    private val _favoriteCombinations = mutableStateOf(emptyList<EmojiKitchenFavoriteCombination>())
    val favoriteCombinations: List<EmojiKitchenFavoriteCombination> by _favoriteCombinations

    private val _isFavoritesLoading = mutableStateOf(false)
    val isFavoritesLoading: Boolean by _isFavoritesLoading

    val isFavorite: Boolean
        get() {
            val firstId = first?.id ?: return false
            val secondId = second?.id ?: return false
            return selectedVariant != null && favorites.any {
                it.matches(
                    first = firstId,
                    second = secondId
                )
            }
        }

    private val _isQuerying = mutableStateOf(false)
    val isQuerying: Boolean by _isQuerying

    private val _isSaving = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    val canExport: Boolean
        get() = selectedVariant != null && collectionState.progress == null

    private var queryJob: Job? by smartJob()
    private var compatibleJob: Job? by smartJob()
    private var exploreJob: Job? by smartJob()
    private var favoritesJob: Job? by smartJob()
    private var savingJob: Job? by smartJob()
    private var saveOperation = 0

    init {
        componentScope.launch {
            repository.favorites.collect { favorites ->
                _favorites.value = favorites
                updateFavorites()
            }
        }
        componentScope.launch {
            var initializeSelection = collectionState.collection != null
            repository.state.collect { state ->
                val previous = collectionState.collection
                _collectionState.value = state
                if (state.collection !== previous) {
                    applyHistorySnapshot(null to null)
                    clearHistory()
                    exploreJob = null
                    _exploreEmoji.value = null
                    _combinations.value = null
                    _isExploringLoading.value = false
                    initializeSelection = state.collection != null
                    _favoriteCombinations.value = emptyList()
                    updateFavorites()
                }
                if (initializeSelection && !state.isLoading && state.progress == null) {
                    initializeSelection = false
                    randomize()
                }
            }
        }
    }

    fun selectSlot(value: Int) {
        if (activeSlot == value) return
        _activeSlot.intValue = value
        updateCompatibleEmojis()
    }

    fun openExplore() {
        val emoji = if (activeSlot == 0) first ?: second else second ?: first
        (emoji ?: collectionState.collection?.emojis?.firstOrNull())?.let(::selectExploreEmoji)
    }

    fun selectExploreEmoji(emoji: EmojiKitchenEmoji) {
        if (emoji == exploreEmoji && (isExploringLoading || _combinations.value != null)) return
        _exploreEmoji.value = emoji
        _combinations.value = null
        _isExploringLoading.value = true
        exploreJob = componentScope.launch {
            try {
                runSuspendCatching {
                    _combinations.value = repository.explore(emoji.id)
                }.onFailure(AppToastHost::showFailureToast)
            } finally {
                if (currentCoroutineContext().isActive) {
                    _isExploringLoading.value = false
                }
            }
        }
    }

    fun selectEmoji(emoji: EmojiKitchenEmoji) {
        if (emoji == if (activeSlot == 0) first else second) return
        val otherId = otherEmojiId
        if (otherId != null && (compatibility.value?.first != otherId || emoji.id !in compatibleEmojis)) return

        val before = currentHistorySnapshot()
        if (activeSlot == 0) {
            _first.value = emoji
            if (second == null) {
                _activeSlot.intValue = 1
            }
        } else {
            _second.value = emoji
        }
        commitHistoryFrom(before)
        updateCombinations()
    }

    fun selectCombination(combination: EmojiKitchenCombination) {
        val before = currentHistorySnapshot()
        _first.value = exploreEmoji ?: return
        _second.value = collectionState.collection?.emojis?.find {
            it.id == combination.otherEmojiId
        }
        _activeSlot.intValue = 1
        commitHistoryFrom(before)
        updateCombinations()
    }

    fun toggleFavorite() {
        if (!canExport) return
        val firstId = first?.id ?: return
        val secondId = second?.id ?: return
        componentScope.launch {
            runSuspendCatching {
                repository.toggleFavorite(
                    first = firstId,
                    second = secondId
                )
            }.onFailure(AppToastHost::showFailureToast)
        }
    }

    fun selectFavorite(
        combination: EmojiKitchenFavoriteCombination
    ) {
        val before = currentHistorySnapshot()
        val emojis = collectionState.collection?.emojis.orEmpty()
        _first.value = emojis.find { it.id == combination.firstEmojiId }
        _second.value = emojis.find { it.id == combination.secondEmojiId }
        _activeSlot.intValue = 1
        commitHistoryFrom(before)
        updateCombinations()
    }

    fun removeFavorite(combination: EmojiKitchenFavoriteCombination) {
        componentScope.launch {
            runSuspendCatching {
                repository.removeFavorite(
                    first = combination.firstEmojiId,
                    second = combination.secondEmojiId
                )
            }.onFailure(AppToastHost::showFailureToast)
        }
    }

    suspend fun resolveCombination(
        combination: EmojiKitchenCombination
    ): String? = runSuspendCatching {
        repository.resolveVariant(
            first = exploreEmoji?.id ?: return@runSuspendCatching null,
            second = combination.otherEmojiId
        )
    }.getOrNull()

    suspend fun resolveFavorite(
        combination: EmojiKitchenFavoriteCombination
    ): String? = runSuspendCatching {
        repository.resolveVariant(
            first = combination.firstEmojiId,
            second = combination.secondEmojiId
        )
    }.getOrNull()

    fun randomize(slot: Int? = null) {
        if (collectionState.progress != null) return
        _isQuerying.value = true
        queryJob = componentScope.launch {
            try {
                runSuspendCatching {
                    val before = currentHistorySnapshot()
                    if (slot == null) {
                        val pair = repository.randomPair() ?: return@runSuspendCatching
                        val emojis = collectionState.collection?.emojis.orEmpty()
                        _first.value = emojis.find { it.id == pair.first }
                        _second.value = emojis.find { it.id == pair.second }
                    } else {
                        val other = if (slot == 0) second else first
                        val emojis = collectionState.collection?.emojis.orEmpty()
                        val available = other?.let { repository.compatibleEmojis(it.id) }
                        val emoji = emojis.filter {
                            available == null || it.id in available
                        }.randomOrNull()
                        if (slot == 0) {
                            _first.value = emoji
                        } else {
                            _second.value = emoji
                        }
                    }
                    if (history.isEmpty()) resetHistory()
                    else commitHistoryFrom(before)
                    updateCompatibleEmojis()
                    loadPair()
                }.onFailure {
                    _selectedVariant.value = null
                    AppToastHost.showFailureToast(it)
                }
            } finally {
                if (currentCoroutineContext().isActive) {
                    _isQuerying.value = false
                }
            }
        }
    }

    fun clearSelection() {
        val before = currentHistorySnapshot()
        applyHistorySnapshot(null to null)
        commitHistoryFrom(before)
    }

    override fun currentHistorySnapshot(): Pair<EmojiKitchenEmoji?, EmojiKitchenEmoji?> =
        first to second

    override fun applyHistorySnapshot(snapshot: Pair<EmojiKitchenEmoji?, EmojiKitchenEmoji?>) {
        queryJob = null
        _first.value = snapshot.first
        _second.value = snapshot.second
        _activeSlot.intValue = if (first == null) 0 else 1
        updateCombinations()
    }

    private fun updateCombinations() {
        updateCompatibleEmojis()
        if (first == null || second == null) {
            queryJob = null
            _selectedVariant.value = null
            _isQuerying.value = false
            return
        }
        _isQuerying.value = true
        queryJob = componentScope.launch {
            try {
                runSuspendCatching {
                    loadPair()
                }.onFailure {
                    _selectedVariant.value = null
                    AppToastHost.showFailureToast(it)
                }
            } finally {
                if (currentCoroutineContext().isActive) {
                    _isQuerying.value = false
                }
            }
        }
    }

    private suspend fun loadPair() {
        val firstId = first?.id
        val secondId = second?.id
        _selectedVariant.value = if (firstId != null && secondId != null) {
            repository.latestVariant(firstId, secondId)
        } else null
    }

    private fun updateCompatibleEmojis() {
        val otherId = otherEmojiId
        if (otherId == null || otherId == compatibility.value?.first) {
            compatibleJob = null
            if (otherId == null) compatibility.value = null
            return
        }

        compatibleJob = componentScope.launch {
            runSuspendCatching {
                compatibility.value = otherId to repository.compatibleEmojis(otherId)
            }.onFailure {
                compatibility.value = null
                AppToastHost.showFailureToast(it)
            }
        }
    }

    private fun updateFavorites() {
        _isFavoritesLoading.value = favoriteCombinations.isEmpty() && favorites.isNotEmpty()
        favoritesJob = componentScope.launch {
            try {
                runSuspendCatching {
                    _favoriteCombinations.value = repository.favoriteCombinations()
                }.onFailure(AppToastHost::showFailureToast)
            } finally {
                if (currentCoroutineContext().isActive) {
                    _isFavoritesLoading.value = false
                }
            }
        }
    }

    fun downloadCollection() = repository.downloadCollection()

    fun importCollection(uri: Uri) = repository.importCollection(uri.toString())

    fun deleteCollection() = repository.deleteCollection()

    fun cancelInstallation() = repository.cancel()

    fun saveImage(
        oneTimeSaveLocationUri: String?
    ) = exportImage { uri, _ ->
        val imageInfo = withContext(ioDispatcher) {
            val size = checkNotNull(uri.toUri().imageSize()) {
                getString(R.string.failed_to_open)
            }
            ImageInfo(
                width = size.width,
                height = size.height,
                imageFormat = ImageFormat.Png.Lossless
            )
        }
        parseSaveResult(
            fileController.save(
                saveTarget = ImageSaveTarget(
                    imageInfo = imageInfo,
                    originalUri = uri,
                    sequenceNumber = null,
                    data = ByteArray(0),
                    readFromUriInsteadOfData = true
                ),
                keepOriginalMetadata = false,
                oneTimeSaveLocationUri = oneTimeSaveLocationUri,
                allowOverwrite = false
            )
        )
    }

    fun shareImage() = cacheImage { uri ->
        componentScope.launch {
            shareProvider.shareUri(
                uri = uri.toString(),
                onComplete = AppToastHost::showConfetti
            )
        }
    }

    fun cacheImage(
        onComplete: (Uri) -> Unit
    ) = exportImage { uri, filename ->
        shareProvider.cacheDataOrThrow(
            filename = filename,
            writeData = {
                val result = fileController.transferBytes(uri, it)
                if (result is SaveResult.Error) throw result.throwable
            }
        ).let {
            onComplete(it.toUri())
        }
    }

    private fun exportImage(
        action: suspend (uri: String, filename: String) -> Unit
    ) {
        if (!canExport) return
        val firstId = first?.id ?: return
        val secondId = second?.id ?: return
        val operation = ++saveOperation

        _isSaving.value = true
        savingJob = trackProgress {
            try {
                runSuspendCatching {
                    val variant = checkNotNull(repository.latestVariant(firstId, secondId)) {
                        getString(R.string.failed_to_open)
                    }
                    val filename = "emoji_kitchen_${firstId.take(96)}_" +
                            "${secondId.take(96)}_${variant.date}.png"
                    currentCoroutineContext().ensureActive()
                    action(variant.uri, filename)
                }.onFailure(AppToastHost::showFailureToast)
            } finally {
                if (operation == saveOperation) {
                    _isSaving.value = false
                }
            }
        }
    }

    fun cancelSaving() {
        saveOperation++
        savingJob = null
        _isSaving.value = false
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): EmojiKitchenComponent
    }
}