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

import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.remote.DownloadManager
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenCategory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Assume.assumeNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.util.Collections
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RunWith(AndroidJUnit4::class)
class EmojiKitchenCollectionTest {

    private val target = InstrumentationRegistry.getInstrumentation().targetContext
    private val root = File(
        target.cacheDir,
        "emoji-kitchen-test-${UUID.randomUUID()}"
    ).apply { mkdirs() }
    private val scopes = mutableListOf<AppScope>()
    private val failures = Collections.synchronizedList(mutableListOf<Throwable>())
    private val context = object : ContextWrapper(target) {
        override fun getFilesDir(): File = File(root, "files").apply { mkdirs() }
        override fun getCacheDir(): File = File(root, "cache").apply { mkdirs() }
    }
    private val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dataStore = PreferenceDataStoreFactory.create(
        scope = dataStoreScope,
        produceFile = { File(root, "emoji-kitchen.preferences_pb") }
    )
    private val dispatchers = object : DispatchersHolder {
        override val uiDispatcher = Dispatchers.Default
        override val ioDispatcher = Dispatchers.IO
        override val encodingDispatcher = Dispatchers.Default
        override val decodingDispatcher = Dispatchers.Default
        override val defaultDispatcher = Dispatchers.Default
    }

    @After
    fun tearDown() {
        runBlocking {
            scopes.forEach { it.coroutineContext.job.cancelAndJoin() }
            dataStoreScope.coroutineContext.job.cancelAndJoin()
        }
        root.deleteRecursively()
    }

    @Test
    fun originalAndHistoricalVariantsSurviveOfflineReopen() = runBlocking {
        val directory = File(root, "installed").apply { mkdirs() }
        fixture(File(directory, "collection.zip"))
        var progress = 0L
        EmojiKitchenIndex.build(directory) { completed, _ -> progress = completed }
        assertEquals(2L, progress)
        repeat(2) {
            EmojiKitchenIndex.open(directory, File(root, "images")).use { index ->
                val collection = index.collection()
                assertEquals(2, collection.emojis.size)
                assertEquals("grinning face", collection.emojis.first().name)
                assertEquals(EmojiKitchenCategory.Smileys, collection.emojis.first().category)
                assertEquals(listOf("happy", "joy"), collection.emojis.first().keywords)
                assertEquals(1, collection.combinationCount)
                assertEquals(2, collection.imageCount)
                val variants = index.variants("1f431", "1f600")
                assertEquals(listOf("20260429", "20201001"), variants.map { it.date })
                assertEquals(variants, index.variants("1f600", "1f431"))
                variants.forEach {
                    assertTrue(File(requireNotNull(it.uri.toUri().path)).isFile)
                }
                assertEquals(setOf("1f431"), index.compatibleEmojis("1f600"))
                assertEquals("1f431", index.explore("1f600").single().otherEmojiId)
                assertEquals("1f431" to "1f600", index.randomPair())
                assertTrue(index.variants("1f600", "1f600").isEmpty())
            }
        }
    }

    @Test
    fun suppliedCollectionArchiveCanBeInstalledAndQueriedOffline() = runBlocking {
        val archivePath = InstrumentationRegistry.getArguments().getString("emojiKitchenArchive")
        assumeNotNull(archivePath)
        val directory = File(root, "published").apply { mkdirs() }
        File(requireNotNull(archivePath)).copyTo(File(directory, "collection.zip"))
        EmojiKitchenIndex.build(directory) { _, _ -> }
        EmojiKitchenIndex.open(directory, File(root, "published-images")).use { index ->
            val collection = index.collection()
            assertTrue(collection.emojis.size >= 600)
            assertTrue(collection.combinationCount >= 100_000)
            assertTrue(collection.imageCount >= 100_000)
            repeat(25) {
                val (first, second) = requireNotNull(index.randomPair())
                val variants = index.variants(first, second)
                assertTrue(variants.isNotEmpty())
                assertEquals(variants, index.variants(second, first))
                variants.forEach {
                    assertTrue(File(requireNotNull(it.uri.toUri().path)).length() > 0L)
                }
            }
        }
    }

    @Test
    fun invalidArchivesAreRejected() = runBlocking {
        val faults = listOf(
            "hash",
            "missing",
            "traversal",
            "size",
            "unknownEmoji",
            "unknownCategory",
            "schema",
            "hugePng",
            "truncatedPng",
            "damagedCrc"
        )
        for (fault in faults) {
            val directory = File(root, fault).apply { mkdirs() }
            fixture(File(directory, "collection.zip"), fault = fault)
            try {
                EmojiKitchenIndex.build(directory) { _, _ -> }
                fail("Accepted invalid archive: $fault")
            } catch (_: IllegalArgumentException) {
                // Every malformed archive is rejected before activation.
            } catch (_: IOException) {
                // Recent Android versions reject unsafe ZIP paths before opening the archive.
            }
        }
        assertFalse(File(root.parentFile, "escaped.png").exists())
    }

    @Test
    fun failedReplacementPreservesInstalledCollectionAndDeletionSurvivesRestart() = runBlocking {
        val repository = repository()
        withTimeout(10_000) {
            repository.state.first { !it.isLoading }
        }
        val initial = fixture(File(root, "first.zip"), version = "first")
        repository.importCollection(initial.toUri().toString())
        withTimeout(10_000) {
            repository.state.first { it.collection?.version == "first" && it.progress == null }
        }

        val invalid = fixture(File(root, "invalid.zip"), version = "invalid", fault = "hash")
        repository.importCollection(invalid.toUri().toString())
        withTimeout(10_000) {
            repository.state.first { it.progress == null }
        }
        assertEquals(1, failures.size)
        assertEquals("first", repository.state.value.collection?.version)
        assertEquals(2, repository.variants("1f600", "1f431").size)

        val reopened = repository()
        withTimeout(10_000) {
            reopened.state.first { !it.isLoading }
        }
        assertEquals("first", reopened.state.value.collection?.version)
        assertNotNull(reopened.randomPair())

        val replacement = fixture(File(root, "second.zip"), version = "second")
        reopened.importCollection(replacement.toUri().toString())
        withTimeout(10_000) {
            reopened.state.first { it.collection?.version == "second" && it.progress == null }
        }
        assertEquals(
            1,
            File(context.filesDir, "emoji_kitchen").listFiles().orEmpty()
                .count { it.isDirectory }
        )
        reopened.deleteCollection()
        withTimeout(10_000) {
            reopened.state.first { it.collection == null && it.progress == null }
        }
        val deleted = repository()
        withTimeout(10_000) {
            deleted.state.first { !it.isLoading }
        }
        assertEquals(null, deleted.state.value.collection)
    }

    @Test
    fun cancellingDownloadRemovesPartialArchiveAndKeepsPreviousCollection() = runBlocking {
        val started = CompletableDeferred<Unit>()
        val repository = repository { path ->
            File(path).writeBytes(byteArrayOf(1, 2, 3))
            started.complete(Unit)
            awaitCancellation()
        }
        withTimeout(10_000) {
            repository.state.first { !it.isLoading }
        }
        val initial = fixture(File(root, "first.zip"), version = "first")
        repository.importCollection(initial.toUri().toString())
        withTimeout(10_000) {
            repository.state.first { it.collection?.version == "first" && it.progress == null }
        }
        repository.downloadCollection()
        withTimeout(10_000) { started.await() }
        repository.cancel()
        withTimeout(10_000) {
            repository.state.first { it.progress == null }
        }
        assertEquals("first", repository.state.value.collection?.version)
        assertTrue(failures.isEmpty())
        assertEquals(
            1,
            File(context.filesDir, "emoji_kitchen").listFiles().orEmpty()
                .count { it.isDirectory }
        )
        repository.downloadCollection()
        repository.cancel()
        withTimeout(10_000) {
            repository.state.first { it.progress == null }
        }
        assertEquals("first", repository.state.value.collection?.version)
        assertTrue(failures.isEmpty())
    }

    @Test
    fun failedDownloadIsReportedOnceAndKeepsTheInstalledCollection() = runBlocking {
        val repository = repository { path ->
            File(path).writeBytes(byteArrayOf(1, 2, 3))
            throw IOException("Download failed")
        }
        withTimeout(10_000) {
            repository.state.first { !it.isLoading }
        }
        val archive = fixture(File(root, "collection.zip"))
        repository.importCollection(archive.toUri().toString())
        withTimeout(10_000) {
            repository.state.first { it.collection != null && it.progress == null }
        }

        repository.downloadCollection()
        withTimeout(10_000) {
            repository.state.first { it.progress == null }
        }
        assertEquals("test", repository.state.value.collection?.version)
        assertEquals("Download failed", failures.single().message)
        assertEquals(2, repository.variants("1f600", "1f431").size)
        assertEquals(
            1,
            File(context.filesDir, "emoji_kitchen").listFiles().orEmpty()
                .count { it.isDirectory }
        )
    }

    @Test
    fun corruptInstalledCollectionIsReportedOnceOnStartup() = runBlocking {
        val directory = File(context.filesDir, "emoji_kitchen/collection-${UUID.randomUUID()}")
            .apply { mkdirs() }
        File(directory, "collection.zip").writeBytes(byteArrayOf(1, 2, 3))
        val repository = repository()
        withTimeout(10_000) {
            repository.state.first { !it.isLoading }
        }
        assertEquals(null, repository.state.value.collection)
        assertEquals(null, repository.state.value.progress)
        assertEquals(1, failures.size)
    }

    @Test
    fun restoringSettingsWithoutCollectionMarkerKeepsTheInstalledCollection() = runBlocking {
        val interrupted = File(context.filesDir, "emoji_kitchen/staging-${UUID.randomUUID()}")
            .apply { mkdirs() }
        File(interrupted, "collection.zip").writeBytes(byteArrayOf(1, 2, 3))
        val repository = repository()
        withTimeout(10_000) {
            repository.state.first { !it.isLoading }
        }
        assertFalse(interrupted.exists())
        assertEquals(null, repository.state.value.collection)
        val archive = fixture(File(root, "collection.zip"))
        repository.importCollection(archive.toUri().toString())
        withTimeout(10_000) {
            repository.state.first { it.collection != null && it.progress == null }
        }
        val collectionDirectory = File(context.filesDir, "emoji_kitchen")
            .listFiles().orEmpty().single { it.isDirectory }
        val currentCollection = stringPreferencesKey("EMOJI_KITCHEN_COLLECTION")
        val incomplete = File(collectionDirectory.parentFile, "collection-${UUID.randomUUID()}")
            .apply { mkdirs() }
        fixture(File(incomplete, "collection.zip"), fault = "hash")
        try {
            EmojiKitchenIndex.build(incomplete) { _, _ -> }
            fail("Accepted an incomplete collection")
        } catch (_: IllegalArgumentException) {
            // A failed import may leave staging files when the process is killed.
        }
        incomplete.setLastModified(collectionDirectory.lastModified() + 1_000)

        for (marker in listOf(null, "collection-${UUID.randomUUID()}")) {
            dataStore.edit { preferences ->
                if (marker == null) preferences.remove(currentCollection)
                else preferences[currentCollection] = marker
            }
            val reopened = repository()
            withTimeout(10_000) {
                reopened.state.first { !it.isLoading }
            }
            assertEquals("test", reopened.state.value.collection?.version)
            assertTrue(failures.isEmpty())
            assertEquals(collectionDirectory.name, dataStore.data.first()[currentCollection])
            assertTrue(File(collectionDirectory, "collection.zip").isFile)
            assertEquals(2, reopened.variants("1f600", "1f431").size)
        }
        assertFalse(incomplete.exists())
    }

    @Test
    fun clearingCachePreservesPickerGlyphsAndRestoresSelectedImagesOnAccess() = runBlocking {
        val repository = repository()
        withTimeout(10_000) {
            repository.state.first { !it.isLoading }
        }
        val archive = fixture(
            file = File(root, "collection.zip"),
            additionalPair = true
        )
        repository.importCollection(archive.toUri().toString())
        withTimeout(10_000) {
            repository.state.first { it.collection != null && it.progress == null }
        }
        val emoji = requireNotNull(repository.state.value.collection).emojis.first()
        val glyph = File(requireNotNull(emoji.imageUri?.toUri()?.path))
        val selected = repository.variants("1f600", "1f431").last()
        val image = File(requireNotNull(selected.uri.toUri().path))
        val original = image.readBytes()
        repository.toggleFavorite("1f600", "1f600", selected)
        assertTrue(context.cacheDir.deleteRecursively())
        assertFalse(image.exists())
        assertTrue(glyph.isFile)
        assertTrue(glyph.path.startsWith(context.filesDir.path))
        val favorite = repository.favoriteCombinations().single()
        assertEquals(selected.uri, favorite.variant.uri)
        assertFalse(image.exists())
        assertEquals(selected.uri, repository.resolveFavorite(favorite))
        assertArrayEquals(original, image.readBytes())
        val cache = File(context.cacheDir, "emoji_kitchen")
        assertEquals(1, cache.listFiles().orEmpty().size)
        assertTrue(context.cacheDir.deleteRecursively())

        val newest = checkNotNull(repository.latestVariant("1f600", "1f431"))
        assertEquals("20260429", newest.date)
        assertEquals(newest, repository.latestVariant("1f431", "1f600"))
        assertEquals(1, cache.listFiles().orEmpty().size)
        assertFalse(image.exists())

        val restored = repository.variants("1f600", "1f431").find {
            it.uri == selected.uri && it.date == selected.date
        }
        assertNotNull(restored)
        assertArrayEquals(original, image.readBytes())
        assertEquals(newest, repository.latestVariant("1f600", "1f431"))
        val newestFile = File(requireNotNull(newest.uri.toUri().path))
        newestFile.setLastModified(1L)
        val pressure = File(cache, "old-cache.png")
        RandomAccessFile(pressure, "rw").use { file ->
            file.setLength(128L * 1024 * 1024)
        }
        assertEquals(selected.uri, repository.resolveFavorite(favorite))
        assertTrue(newestFile.isFile)
        assertTrue(image.isFile)
        assertFalse(pressure.exists())
    }

    @Test
    fun legacyFavoritesShowTheLatestPairAcrossReplacementAndReinstall() = runBlocking {
        val repository = repository()
        withTimeout(10_000) {
            repository.state.first { !it.isLoading }
        }
        val initial = fixture(File(root, "first.zip"), version = "first")
        repository.importCollection(initial.toUri().toString())
        withTimeout(10_000) {
            repository.state.first { it.collection?.version == "first" && it.progress == null }
        }
        val historical = repository.variants("1f600", "1f431").last()
        repository.toggleFavorite("1f600", "1f431", historical)
        withTimeout(10_000) {
            repository.favorites.first { it.size == 1 }
        }
        assertTrue(repository.favorites.value.single().matches("1f431", "1f600"))
        assertEquals("20201001", repository.favorites.value.single().date)
        val latest = repository.variants("1f600", "1f431").first()
        val favoritesKey = stringSetPreferencesKey("EMOJI_KITCHEN_FAVORITES")
        dataStore.edit { preferences ->
            preferences[favoritesKey] = preferences[favoritesKey].orEmpty() +
                    "1f431|1f600|images/${latest.uri.substringAfterLast('/')}|${latest.date}"
        }
        withTimeout(10_000) {
            repository.favorites.first { it.size == 2 }
        }
        assertEquals("20260429", repository.favoriteCombinations().single().variant.date)

        val reopened = repository()
        withTimeout(10_000) {
            reopened.state.first { !it.isLoading }
        }
        withTimeout(10_000) {
            reopened.favorites.first { it == repository.favorites.value }
        }
        assertEquals(repository.favorites.value, reopened.favorites.value)
        assertEquals("20260429", reopened.favoriteCombinations().single().variant.date)

        val replacement = fixture(File(root, "second.zip"), version = "second")
        reopened.importCollection(replacement.toUri().toString())
        withTimeout(10_000) {
            reopened.state.first { it.collection?.version == "second" && it.progress == null }
        }
        assertEquals("20260429", reopened.favoriteCombinations().single().variant.date)
        reopened.deleteCollection()
        withTimeout(10_000) {
            reopened.state.first { it.collection == null && it.progress == null }
        }
        assertEquals(2, reopened.favorites.value.size)
        assertTrue(reopened.favoriteCombinations().isEmpty())

        reopened.importCollection(initial.toUri().toString())
        withTimeout(10_000) {
            reopened.state.first { it.collection?.version == "first" && it.progress == null }
        }
        val restored = reopened.favoriteCombinations().single()
        assertEquals("20260429", restored.variant.date)
        assertEquals(restored.variant.uri, reopened.resolveFavorite(restored))
        assertTrue(reopened.favorites.value.any { it.date == "20201001" })
        reopened.toggleFavorite("1f431", "1f600", restored.variant)
        withTimeout(10_000) {
            reopened.favorites.first { it.isEmpty() }
        }
        assertTrue(reopened.favorites.value.isEmpty())
        assertTrue(reopened.favoriteCombinations().isEmpty())
    }

    private fun repository(
        download: suspend (String) -> Unit = { error("Network is not needed") }
    ): AndroidEmojiKitchenRepository {
        val scope = object : AppScope {
            override val coroutineContext = SupervisorJob() + Dispatchers.Default
        }.also(scopes::add)
        return AndroidEmojiKitchenRepository(
            context = context,
            downloadManager = object : DownloadManager {
                override suspend fun download(
                    url: String,
                    destinationPath: String,
                    onStart: suspend () -> Unit,
                    onProgress: suspend (DownloadProgress) -> Unit,
                    onFinish: suspend (Throwable?) -> Unit
                ) {
                    try {
                        onStart()
                        download(destinationPath)
                        onFinish(null)
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Throwable) {
                        onFinish(error)
                        failures += error
                    }
                }

                override suspend fun downloadZip(
                    url: String,
                    destinationPath: String,
                    onStart: suspend () -> Unit,
                    onProgress: (DownloadProgress) -> Unit,
                    onFailure: (Throwable) -> Unit,
                    downloadOnlyNewData: Boolean
                ) = error("ZIP download should use the verified installer")
            },
            appScope = scope,
            keepAliveService = object : KeepAliveService by KeepAliveService.EMPTY {
                override fun send(error: Throwable) {
                    failures += error
                }
            },
            dataStore = dataStore,
            dispatchers = dispatchers
        )
    }

    private fun fixture(
        file: File,
        version: String = "test",
        fault: String? = null,
        additionalPair: Boolean = false
    ): File {
        val images = listOf(Color.YELLOW, Color.BLUE).map { color ->
            val bitmap = createBitmap(2, 2).apply { eraseColor(color) }
            ByteArrayOutputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                bitmap.recycle()
                output.toByteArray()
            }
        }.toMutableList()
        if (fault == "hugePng") images[0][16] = 1
        if (fault == "truncatedPng") images[0] = images[0].copyOf(24)
        if (fault == "damagedCrc") images[0][29] = (images[0][29].toInt() xor 1).toByte()
        val hashes = images.map { bytes ->
            MessageDigest.getInstance("SHA-256").digest(bytes)
                .joinToString("") { "%02x".format(it) }
        }
        val paths = hashes.map { "images/$it.png" }
        val variants = JSONArray().apply {
            for (i in images.indices) {
                put(
                    JSONObject().apply {
                        put("path", paths[i])
                        put("sha256", hashes[i])
                        put("size", images[i].size + if (fault == "size") 1 else 0)
                        put("date", if (i == 0) "20260429" else "20201001")
                        put("isLatest", i == 0)
                    }
                )
            }
        }
        val manifest = JSONObject().apply {
            put("schemaVersion", if (fault == "schema") 2 else 1)
            put("version", version)
            put(
                "emojis",
                JSONArray().apply {
                    for ((id, value) in listOf("1f600" to "😀", "1f431" to "🐱")) {
                        put(
                            JSONObject().apply {
                                put("id", id)
                                put("emoji", value)
                                put("name", if (id == "1f600") "grinning_face" else value)
                                if (id == "1f600") {
                                    put("keywords", JSONArray(listOf("happy", "joy")))
                                    put("image", paths[0])
                                    put("sha256", hashes[0])
                                    put("size", images[0].size)
                                }
                                put(
                                    "category",
                                    if (fault == "unknownCategory") "unknown" else "smileys"
                                )
                            }
                        )
                    }
                }
            )
            put(
                "combinations",
                JSONArray().put(
                    JSONObject().apply {
                        put("variants", variants)
                        put("first", "1f600")
                        put("second", if (fault == "unknownEmoji") "1f999" else "1f431")
                    }
                ).apply {
                    if (additionalPair) {
                        put(
                            JSONObject().apply {
                                put("first", "1f600")
                                put("second", "1f600")
                                put("variants", JSONArray().put(variants.getJSONObject(1)))
                            }
                        )
                    }
                }
            )
        }
        ZipOutputStream(file.outputStream()).use { zip ->
            zip.putNextEntry(ZipEntry("manifest.json"))
            zip.write(manifest.toString().toByteArray())
            zip.closeEntry()
            for (i in images.indices) {
                if (fault == "missing" && i == 0) continue
                zip.putNextEntry(ZipEntry(paths[i]))
                val bytes = images[i].clone()
                if (fault == "hash" && i == 0) bytes[bytes.lastIndex] = (bytes.last() + 1).toByte()
                zip.write(bytes)
                zip.closeEntry()
            }
            if (fault == "traversal") {
                zip.putNextEntry(ZipEntry("../escaped.png"))
                zip.write(images.first())
                zip.closeEntry()
            }
        }
        return file
    }
}