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

package com.t8rin.imagetoolbox.feature.emoji_kitchen.data

import android.content.Context
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.t8rin.imagetoolbox.core.domain.EMOJI_KITCHEN_HOME_URL
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.remote.DownloadManager
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenCollection
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenCombination
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenFavorite
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenFavoriteCombination
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenProgress
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenRepository
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenState
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenVariant
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

internal class AndroidEmojiKitchenRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadManager: DownloadManager,
    private val appScope: AppScope,
    private val keepAliveService: KeepAliveService,
    private val dataStore: DataStore<Preferences>,
    dispatchers: DispatchersHolder
) : EmojiKitchenRepository,
    DispatchersHolder by dispatchers {

    private val directory = File(context.filesDir, "emoji_kitchen")
    private val cache = File(context.cacheDir, "emoji_kitchen")
    private val mutex = Mutex()
    private var index: EmojiKitchenIndex? = null
    private var installedDirectory: File? = null
    private var operation: Job? = null
    private var lastProgressUpdate = 0L
    private var selectedImage: String? = null

    private val _state = MutableStateFlow(EmojiKitchenState())
    override val state = _state.asStateFlow()
    override val favorites = dataStore.data.map { preferences ->
        preferences.readFavorites()
    }.stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = emptySet()
    )

    init {
        appScope.launch(ioDispatcher) {
            var loadedCollection: EmojiKitchenCollection? = null
            try {
                mutex.withLock {
                    val name = dataStore.data.first()[CURRENT_COLLECTION].orEmpty()
                    directory.listFiles()
                        ?.filter { it.isDirectory && it.name.matches(STAGING_DIRECTORY) }
                        ?.forEach { it.deleteRecursively() }
                    val candidates = directory.listFiles().orEmpty()
                        .filter { it.isDirectory && it.name.matches(COLLECTION_DIRECTORY) }
                        .sortedWith(
                            compareByDescending<File> { it.name == name }
                                .thenByDescending(File::lastModified)
                        )
                    var failure: Exception? = null
                    for (candidate in candidates) {
                        coroutineContext.ensureActive()
                        val (opened, collection) = try {
                            val opened = EmojiKitchenIndex.open(candidate, cache)
                            try {
                                opened to opened.collection()
                            } catch (error: Throwable) {
                                opened.close()
                                throw error
                            }
                        } catch (error: Exception) {
                            if (error is CancellationException) throw error
                            failure = error
                            continue
                        }
                        try {
                            if (candidate.name != name) {
                                dataStore.edit { it[CURRENT_COLLECTION] = candidate.name }
                            }
                            index = opened
                            installedDirectory = candidate
                            loadedCollection = collection
                        } catch (error: Throwable) {
                            opened.close()
                            throw error
                        }
                        break
                    }
                    if (installedDirectory != null) {
                        directory.listFiles()?.filter { it.isDirectory && it != installedDirectory }
                            ?.forEach { it.deleteRecursively() }
                        trimCache()
                    } else if (failure != null) {
                        keepAliveService.send(failure)
                    }
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                keepAliveService.send(error)
            } finally {
                _state.update { it.copy(isLoading = false, collection = loadedCollection) }
            }
        }
    }

    override fun downloadCollection() = install(EmojiKitchenProgress.Stage.Downloading) { target ->
        var downloaded = false
        channelFlow {
            downloadManager.download(
                url = EMOJI_KITCHEN_COLLECTION_URL,
                destinationPath = target.absolutePath,
                onProgress = ::trySend,
                onFinish = { error ->
                    downloaded = error == null
                    close()
                }
            )
        }.collect { progress ->
            updateProgress(
                stage = EmojiKitchenProgress.Stage.Downloading,
                completed = (progress.currentPercent * progress.currentTotalSize).toLong(),
                total = progress.currentTotalSize
            )
        }
        if (downloaded) verifyDownload(target)
        downloaded
    }

    override fun importCollection(uri: String) =
        install(EmojiKitchenProgress.Stage.Importing) { target ->
            val source = uri.toUri()
            val total = runCatching {
                context.contentResolver.openAssetFileDescriptor(source, "r")?.use { it.length }
            }.getOrNull()?.coerceAtLeast(0L) ?: 0L
            val buffer = ByteArray(256 * 1024)
            var completed = 0L
            requireNotNull(context.contentResolver.openInputStream(source)) {
                "Cannot open the collection archive"
            }.use { input ->
                target.outputStream().use { output ->
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        completed += count
                        output.write(buffer, 0, count)
                        updateProgress(
                            stage = EmojiKitchenProgress.Stage.Importing,
                            completed = completed,
                            total = total
                        )
                    }
                }
            }
            updateProgress(
                stage = EmojiKitchenProgress.Stage.Importing,
                completed = completed,
                total = total
            )
            true
        }

    @Synchronized
    private fun install(
        stage: EmojiKitchenProgress.Stage,
        obtainArchive: suspend (File) -> Boolean
    ) {
        if (state.value.progress != null || state.value.isLoading) return
        _state.update {
            it.copy(
                progress = EmojiKitchenProgress(stage)
            )
        }
        operation = appScope.launch(
            context = ioDispatcher,
            start = CoroutineStart.LAZY
        ) {
            var staging: File? = null
            try {
                keepAliveService.track {
                    check(directory.mkdirs() || directory.isDirectory)
                    val id = UUID.randomUUID()
                    val temporary = File(directory, "staging-$id")
                    staging = temporary
                    check(temporary.mkdir()) { "Cannot create collection directory" }
                    if (!obtainArchive(File(temporary, "collection.zip"))) return@track
                    updateProgress(
                        stage = EmojiKitchenProgress.Stage.Installing,
                        completed = 0,
                        total = 0
                    )
                    EmojiKitchenIndex.build(temporary) { completed, total ->
                        updateProgress(
                            stage = EmojiKitchenProgress.Stage.Installing,
                            completed = completed,
                            total = total
                        )
                    }
                    currentCoroutineContext().ensureActive()
                    val nextDirectory = File(directory, "collection-$id")
                    check(temporary.renameTo(nextDirectory)) { "Cannot activate collection directory" }
                    staging = nextDirectory
                    val next = EmojiKitchenIndex.open(nextDirectory, cache)
                    var committed = false
                    try {
                        val collection = next.collection()
                        currentCoroutineContext().ensureActive()
                        mutex.withLock {
                            withContext(NonCancellable) {
                                dataStore.edit { preferences ->
                                    preferences[CURRENT_COLLECTION] = nextDirectory.name
                                }
                                index?.close()
                                index = next
                                installedDirectory = nextDirectory
                                committed = true
                                selectedImage = null
                                staging = null
                                _state.update { it.copy(collection = collection) }
                                directory.listFiles()
                                    ?.filter { it.isDirectory && it != nextDirectory }
                                    ?.forEach { it.deleteRecursively() }
                                trimCache()
                            }
                        }
                    } finally {
                        if (!committed) next.close()
                    }
                }
            } finally {
                staging?.deleteRecursively()
            }
        }
        operation?.invokeOnCompletion {
            _state.update { it.copy(progress = null) }
        }
        operation?.start()
    }

    @Synchronized
    override fun deleteCollection() {
        if (state.value.progress != null || state.value.isLoading) return
        _state.update {
            it.copy(
                progress = EmojiKitchenProgress(EmojiKitchenProgress.Stage.Installing)
            )
        }
        operation = appScope.launch(
            context = ioDispatcher,
            start = CoroutineStart.LAZY
        ) {
            try {
                mutex.withLock {
                    withContext(NonCancellable) {
                        dataStore.edit { it.remove(CURRENT_COLLECTION) }
                        index?.close()
                        index = null
                        directory.listFiles()?.filter(File::isDirectory)
                            ?.forEach { it.deleteRecursively() }
                        installedDirectory = null
                        selectedImage = null
                        cache.deleteRecursively()
                        _state.update { it.copy(collection = null) }
                    }
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                keepAliveService.send(error)
            }
        }
        operation?.invokeOnCompletion {
            _state.update { it.copy(progress = null) }
        }
        operation?.start()
    }

    override fun cancel() {
        operation?.cancel()
    }

    override suspend fun latestVariant(
        first: String,
        second: String
    ): EmojiKitchenVariant? = withContext(ioDispatcher) {
        mutex.withLock {
            index?.latestVariant(first, second).also { variant ->
                selectedImage = variant?.uri
                trimCache()
            }
        }
    }

    override suspend fun compatibleEmojis(id: String): Set<String> = withContext(ioDispatcher) {
        mutex.withLock { index?.compatibleEmojis(id).orEmpty() }
    }

    override suspend fun explore(
        id: String
    ): List<EmojiKitchenCombination> = withContext(ioDispatcher) {
        mutex.withLock { index?.explore(id).orEmpty() }
    }

    override suspend fun randomPair(): Pair<String, String>? = withContext(ioDispatcher) {
        mutex.withLock { index?.randomPair() }
    }

    override suspend fun toggleFavorite(
        first: String,
        second: String
    ): Unit = withContext(ioDispatcher) {
        val variant = mutex.withLock {
            index?.latestVariant(first, second, materialize = false)
        }
        dataStore.edit { preferences ->
            val current = preferences[FAVORITE_COMBINATIONS].orEmpty()
            val matching = preferences.favoriteKeys(first, second)
            preferences[FAVORITE_COMBINATIONS] = if (matching.isNotEmpty()) {
                current - matching
            } else {
                checkNotNull(variant) { "Combination is not installed" }
                current + EmojiKitchenFavorite(
                    first = minOf(first, second),
                    second = maxOf(first, second),
                    path = "images/${variant.uri.substringAfterLast('/')}",
                    date = variant.date
                ).toPreference()
            }
        }
    }

    override suspend fun removeFavorite(
        first: String,
        second: String
    ): Unit = withContext(ioDispatcher) {
        dataStore.edit { preferences ->
            preferences[FAVORITE_COMBINATIONS] = preferences[FAVORITE_COMBINATIONS].orEmpty() -
                    preferences.favoriteKeys(first, second)
        }
    }

    override suspend fun favoriteCombinations(): List<EmojiKitchenFavoriteCombination> =
        withContext(ioDispatcher) {
            val favorites = dataStore.data.first().readFavorites()
            mutex.withLock { index?.favoriteCombinations(favorites).orEmpty() }
        }

    override suspend fun resolveVariant(
        first: String,
        second: String
    ): String? = withContext(ioDispatcher) {
        mutex.withLock {
            index?.latestVariant(first, second)?.uri.also { uri ->
                trimCache(protectedUri = uri)
            }
        }
    }

    private fun Preferences.favoriteKeys(first: String, second: String): Set<String> =
        readFavorites().filter { it.matches(first, second) }
            .mapTo(mutableSetOf()) { it.toPreference() }

    private fun Preferences.readFavorites(): Set<EmojiKitchenFavorite> =
        this[FAVORITE_COMBINATIONS].orEmpty().mapNotNullTo(mutableSetOf()) { value ->
            val parts = value.split('|')
            if (parts.size != 4) return@mapNotNullTo null
            EmojiKitchenFavorite(
                first = parts[0],
                second = parts[1],
                path = parts[2],
                date = parts[3]
            )
        }

    private fun EmojiKitchenFavorite.toPreference(): String = "$first|$second|$path|$date"

    private suspend fun verifyDownload(file: File) {
        val total = file.length()
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(256 * 1024)
        var completed = 0L
        file.inputStream().use { input ->
            while (true) {
                currentCoroutineContext().ensureActive()
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
                completed += count
                updateProgress(
                    stage = EmojiKitchenProgress.Stage.Installing,
                    completed = completed,
                    total = total
                )
            }
        }
        val checksum = digest.digest().toHexString()
        require(checksum == EMOJI_KITCHEN_COLLECTION_SHA256) {
            "Downloaded archive checksum does not match the collection"
        }
    }

    private fun updateProgress(
        stage: EmojiKitchenProgress.Stage,
        completed: Long,
        total: Long
    ) {
        val now = System.currentTimeMillis()
        if (state.value.progress?.stage == stage && now - lastProgressUpdate < 100 &&
            total !in 1..completed
        ) return

        lastProgressUpdate = now

        val progress = (state.value.progress ?: EmojiKitchenProgress(stage)).copy(
            stage = stage,
            completed = completed.coerceAtLeast(0),
            total = total.coerceAtLeast(0)
        )

        _state.update { it.copy(progress = progress) }
    }

    private fun trimCache(protectedUri: String? = null) {
        val files = cache.listFiles().orEmpty().map { it to it.length() }
        var total = files.sumOf { it.second }
        if (total <= MAX_CACHE_SIZE) return
        val protected = setOfNotNull(selectedImage, protectedUri)
        val oldestFirst = files.map { (file, size) -> Triple(file, size, file.lastModified()) }
            .sortedBy { it.third }
        for ((file, size) in oldestFirst) {
            if (total <= MAX_CACHE_SIZE) break
            if (file.toUri().toString() in protected) continue
            if (file.delete()) total -= size
        }
    }

    private companion object {
        val COLLECTION_DIRECTORY = Regex("collection-[a-f0-9-]{36}")
        val STAGING_DIRECTORY = Regex("staging-[a-f0-9-]{36}")

        const val MAX_CACHE_SIZE = 128L * 1024 * 1024

        const val EMOJI_KITCHEN_COLLECTION_URL =
            "$EMOJI_KITCHEN_HOME_URL/resolve/2ea4c4b87e7ee7223b58fe5bb3403e753b535b1c/emoji-kitchen.zip"
        const val EMOJI_KITCHEN_COLLECTION_SHA256 =
            "d0c2d555f85f094473df6401dc5857acda0490a453ae271afa90447559776390"
    }
}

private val FAVORITE_COMBINATIONS = stringSetPreferencesKey("EMOJI_KITCHEN_FAVORITES")
private val CURRENT_COLLECTION = stringPreferencesKey("EMOJI_KITCHEN_COLLECTION")