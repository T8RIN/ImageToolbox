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

import android.database.sqlite.SQLiteDatabase
import android.util.JsonReader
import androidx.core.database.sqlite.transaction
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenCategory
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenCollection
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenCombination
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenEmoji
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenFavorite
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenFavoriteCombination
import com.t8rin.imagetoolbox.feature.emoji_kitchen.domain.EmojiKitchenVariant
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.Closeable
import java.io.DataInputStream
import java.io.File
import java.io.FilterReader
import java.io.Reader
import java.nio.ByteBuffer
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.CRC32
import java.util.zip.ZipFile
import kotlin.random.Random

internal class EmojiKitchenIndex private constructor(
    private val database: SQLiteDatabase,
    private val archive: ZipFile,
    private val cache: File,
    private val emojiDirectory: File
) : Closeable {

    private val pairCount by lazy { count("SELECT COUNT(*) FROM pairs") }

    suspend fun collection(): EmojiKitchenCollection {
        val emojis = database.rawQuery(
            "SELECT id, emoji, name, category, image, keywords FROM emojis ORDER BY position",
            null
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    currentCoroutineContext().ensureActive()
                    add(
                        EmojiKitchenEmoji(
                            id = cursor.getString(0),
                            emoji = cursor.getString(1),
                            name = cursor.getString(2),
                            category = requireNotNull(
                                EmojiKitchenCategory.fromKey(cursor.getString(3))
                            ),
                            imageUri = imageUri(
                                path = requireNotNull(cursor.getString(4)) { "Missing emoji image" },
                                directory = emojiDirectory
                            ),
                            keywords = cursor.getString(5).split('\n').filter(String::isNotBlank)
                        )
                    )
                }
            }
        }
        require(emojis.isNotEmpty() && pairCount > 0) { "The collection is empty" }
        return EmojiKitchenCollection(
            emojis = emojis,
            combinationCount = pairCount.toInt(),
            imageCount = count("SELECT COUNT(*) FROM images").toInt(),
            size = File(archive.name).length() + File(database.path).length() +
                    emojiDirectory.listFiles().orEmpty().sumOf(File::length)
        )
    }

    fun compatibleEmojis(id: String): Set<String> = database.rawQuery(
        "SELECT second FROM pairs WHERE first = ? UNION SELECT first FROM pairs WHERE second = ?",
        arrayOf(id, id)
    ).use { cursor ->
        buildSet {
            while (cursor.moveToNext()) add(cursor.getString(0))
        }
    }

    suspend fun explore(id: String): List<EmojiKitchenCombination> = database.rawQuery(
        "SELECT first, second, path, date FROM pairs WHERE first = ? OR second = ? " +
                "ORDER BY first, second",
        arrayOf(id, id)
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                currentCoroutineContext().ensureActive()
                add(
                    EmojiKitchenCombination(
                        otherEmojiId = cursor.getString(0).takeUnless { it == id }
                            ?: cursor.getString(1),
                        variant = EmojiKitchenVariant(
                            uri = File(cache, cursor.getString(2).substringAfterLast('/'))
                                .toUri().toString(),
                            date = cursor.getString(3),
                            isLatest = true
                        )
                    )
                )
            }
        }
    }

    fun latestVariant(
        first: String,
        second: String,
        materialize: Boolean = true
    ): EmojiKitchenVariant? = database.rawQuery(
        "SELECT path, date FROM pairs WHERE first = ? AND second = ?",
        arrayOf(minOf(first, second), maxOf(first, second))
    ).use { cursor ->
        if (!cursor.moveToFirst()) return@use null
        EmojiKitchenVariant(
            uri = if (materialize) {
                imageUri(cursor.getString(0))
            } else {
                File(cache, cursor.getString(0).substringAfterLast('/')).toUri().toString()
            },
            date = cursor.getString(1),
            isLatest = true
        )
    }

    suspend fun favoriteCombinations(
        favorites: Set<EmojiKitchenFavorite>
    ): List<EmojiKitchenFavoriteCombination> = favorites.reversed()
        .distinctBy { it.first to it.second }
        .mapNotNull { favorite ->
            currentCoroutineContext().ensureActive()
            latestVariant(
                first = favorite.first,
                second = favorite.second,
                materialize = false
            )?.let { variant ->
                EmojiKitchenFavoriteCombination(
                    firstEmojiId = favorite.first,
                    secondEmojiId = favorite.second,
                    variant = variant
                )
            }
        }

    fun randomPair(): Pair<String, String>? {
        if (pairCount == 0L) return null
        return database.rawQuery(
            "SELECT first, second FROM pairs WHERE rowid = ?",
            arrayOf((Random.nextLong(pairCount) + 1).toString())
        ).use {
            if (it.moveToFirst()) it.getString(0) to it.getString(1) else null
        }
    }

    private fun imageUri(
        path: String,
        directory: File = cache
    ): String {
        val entry = requireNotNull(archive.getEntry(path)) { "Missing image: $path" }
        val file = File(directory, path.substringAfterLast('/'))
        if (file.length() != entry.size) {
            check(directory.mkdirs() || directory.isDirectory)
            val temporary = File(directory, "${file.name}.tmp")
            try {
                archive.getInputStream(entry).use { input ->
                    temporary.outputStream().use { input.copyTo(it) }
                }
                check(temporary.renameTo(file)) { "Cannot cache image" }
            } finally {
                temporary.delete()
            }
        }
        if (directory == cache) file.setLastModified(System.currentTimeMillis())
        return file.toUri().toString()
    }

    private fun count(query: String): Long = database.rawQuery(query, null).use {
        check(it.moveToFirst())
        it.getLong(0)
    }

    override fun close() {
        database.close()
        archive.close()
    }

    companion object {
        private val imagePath = Regex("images/[a-f0-9]{64}\\.png")
        private val emojiId = Regex("[a-f0-9]+(?:-[a-f0-9]+)*")
        private val imageDate = Regex("[0-9]{8}")
        private val pngSignature = byteArrayOf(-119, 80, 78, 71, 13, 10, 26, 10)

        fun open(
            directory: File,
            cache: File
        ): EmojiKitchenIndex {
            val archive = ZipFile(File(directory, "collection.zip"))
            try {
                val database = SQLiteDatabase.openDatabase(
                    File(directory, "index.db").path,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                )
                return EmojiKitchenIndex(
                    database = database,
                    archive = archive,
                    cache = cache,
                    emojiDirectory = File(directory, "emoji")
                )
            } catch (error: Throwable) {
                archive.close()
                throw error
            }
        }

        suspend fun build(
            directory: File,
            onProgress: (completed: Long, total: Long) -> Unit
        ) = coroutineScope {
            val jobContext = currentCoroutineContext()
            val file = File(directory, "collection.zip")
            ZipFile(file).use { archive ->
                val entries = archive.entries()
                while (entries.hasMoreElements()) {
                    jobContext.ensureActive()
                    val entry = entries.nextElement()
                    require(
                        entry.name.length <= 256 && !entry.name.startsWith('/') &&
                                '\\' !in entry.name && entry.name.split('/').none { it == ".." }
                    ) { "Invalid archive path" }
                }
                val manifest = requireNotNull(archive.getEntry("manifest.json")) {
                    "This archive does not contain manifest.json"
                }
                SQLiteDatabase.openOrCreateDatabase(File(directory, "index.db"), null).use { db ->
                    db.execSQL(
                        """
                            CREATE TABLE emojis (
                                id TEXT PRIMARY KEY,
                                emoji TEXT NOT NULL,
                                name TEXT NOT NULL,
                                category TEXT NOT NULL,
                                image TEXT NOT NULL,
                                position INTEGER,
                                keywords TEXT NOT NULL
                            )
                        """.trimIndent()
                    )
                    db.execSQL(
                        """
                            CREATE TABLE images (
                                path TEXT PRIMARY KEY,
                                sha256 TEXT NOT NULL,
                                size INTEGER NOT NULL
                            )
                        """.trimIndent()
                    )
                    db.execSQL(
                        """
                            CREATE TABLE variants (
                                first TEXT NOT NULL,
                                second TEXT NOT NULL,
                                path TEXT NOT NULL,
                                date TEXT NOT NULL,
                                latest INTEGER NOT NULL,
                                PRIMARY KEY (first, second, path, date)
                            )
                        """.trimIndent()
                    )
                    db.transaction {
                        val imageInsert = db.compileStatement(
                            "INSERT OR IGNORE INTO images VALUES (?, ?, ?)"
                        )
                        val variantInsert = db.compileStatement(
                            "INSERT OR IGNORE INTO variants VALUES (?, ?, ?, ?, ?)"
                        )

                        fun addImage(
                            path: String,
                            sha: String,
                            size: Long
                        ) {
                            require(imagePath.matches(path) && path == "images/$sha.png") {
                                "Invalid image path or checksum"
                            }
                            val entry =
                                requireNotNull(archive.getEntry(path)) { "Missing image: $path" }
                            require(entry.size == size) { "Image size does not match manifest: $path" }
                            imageInsert.bindString(1, path)
                            imageInsert.bindString(2, sha)
                            imageInsert.bindLong(3, size)
                            imageInsert.executeInsert()
                        }

                        var emojiCount = 0
                        val manifestReader = ManifestReader(
                            archive.getInputStream(manifest).bufferedReader()
                        )
                        JsonReader(manifestReader).use { reader ->
                            reader.beginObject()
                            while (reader.hasNext()) {
                                when (reader.nextName()) {
                                    "emojis" -> {
                                        reader.beginArray()
                                        while (reader.hasNext()) {
                                            jobContext.ensureActive()
                                            require(++emojiCount <= 10_000) { "Too many emojis" }
                                            var id = ""
                                            var emoji = ""
                                            var name = ""
                                            var category = ""
                                            val keywords = mutableListOf<String>()
                                            var image = ""
                                            var sha = ""
                                            var size = 0L
                                            reader.beginObject()
                                            while (reader.hasNext()) {
                                                when (reader.nextName()) {
                                                    "id" -> id = reader.text(128)
                                                    "emoji" -> emoji = reader.text(64)
                                                    "name" -> name =
                                                        reader.text(256).replace('_', ' ')

                                                    "category" -> category = reader.text(64)
                                                    "keywords" -> {
                                                        reader.beginArray()
                                                        while (reader.hasNext()) {
                                                            require(keywords.size < 128) {
                                                                "Too many emoji keywords"
                                                            }
                                                            keywords.add(
                                                                reader.text(256).replace('_', ' ')
                                                            )
                                                        }
                                                        reader.endArray()
                                                    }

                                                    "image" -> image = reader.text(128)
                                                    "sha256" -> sha = reader.text(64)
                                                    "size" -> size = reader.nextLong()
                                                    else -> reader.skipValue()
                                                }
                                            }
                                            reader.endObject()
                                            require(
                                                emojiId.matches(id) &&
                                                        emoji.isNotBlank() && name.isNotBlank()
                                            ) {
                                                "Invalid emoji metadata"
                                            }
                                            requireNotNull(EmojiKitchenCategory.fromKey(category)) {
                                                "Unknown emoji category: $category"
                                            }
                                            addImage(image, sha, size)
                                            db.execSQL(
                                                "INSERT INTO emojis VALUES (?, ?, ?, ?, ?, ?, ?)",
                                                arrayOf<Any>(
                                                    id,
                                                    emoji,
                                                    name,
                                                    category,
                                                    image,
                                                    emojiCount,
                                                    keywords.joinToString("\n")
                                                )
                                            )
                                        }
                                        reader.endArray()
                                    }

                                    "combinations" -> {
                                        reader.beginArray()
                                        while (reader.hasNext()) {
                                            jobContext.ensureActive()
                                            var first = ""
                                            var second = ""
                                            val variants = mutableListOf<ManifestVariant>()
                                            reader.beginObject()
                                            while (reader.hasNext()) {
                                                when (reader.nextName()) {
                                                    "first" -> first = reader.text(128)
                                                    "second" -> second = reader.text(128)
                                                    "variants" -> {
                                                        reader.beginArray()
                                                        while (reader.hasNext()) {
                                                            jobContext.ensureActive()
                                                            var path = ""
                                                            var sha = ""
                                                            var date = ""
                                                            var size = 0L
                                                            var latest = false
                                                            reader.beginObject()
                                                            while (reader.hasNext()) {
                                                                when (reader.nextName()) {
                                                                    "path" -> path =
                                                                        reader.text(128)

                                                                    "sha256" -> sha =
                                                                        reader.text(64)

                                                                    "size" -> size =
                                                                        reader.nextLong()

                                                                    "date" -> date = reader.text(32)
                                                                    "isLatest" -> latest =
                                                                        reader.nextBoolean()

                                                                    else -> reader.skipValue()
                                                                }
                                                            }
                                                            reader.endObject()
                                                            require(imageDate.matches(date)) {
                                                                "Invalid image date"
                                                            }
                                                            addImage(path, sha, size)
                                                            variants.add(
                                                                ManifestVariant(
                                                                    path = path,
                                                                    date = date,
                                                                    latest = latest
                                                                )
                                                            )
                                                        }
                                                        reader.endArray()
                                                    }

                                                    else -> reader.skipValue()
                                                }
                                            }
                                            reader.endObject()
                                            require(emojiId.matches(first) && emojiId.matches(second) && variants.isNotEmpty()) {
                                                "Invalid emoji combination"
                                            }
                                            val pair = listOf(first, second).sorted()
                                            for ((path, date, latest) in variants) {
                                                variantInsert.bindString(1, pair[0])
                                                variantInsert.bindString(2, pair[1])
                                                variantInsert.bindString(3, path)
                                                variantInsert.bindString(4, date)
                                                variantInsert.bindLong(
                                                    5,
                                                    if (latest) 1 else 0
                                                )
                                                variantInsert.executeInsert()
                                            }
                                        }
                                        reader.endArray()
                                    }

                                    else -> reader.skipValue()
                                }
                            }
                            reader.endObject()
                        }
                        imageInsert.close()
                        variantInsert.close()
                        db.rawQuery(
                            """
                                SELECT 1 FROM variants
                                WHERE first NOT IN (SELECT id FROM emojis)
                                OR second NOT IN (SELECT id FROM emojis)
                                LIMIT 1
                            """.trimIndent(),
                            null
                        ).use {
                            require(!it.moveToFirst()) { "Combination references an unknown emoji" }
                        }
                        db.execSQL(
                            """
                                CREATE TABLE pairs (
                                    first TEXT NOT NULL,
                                    second TEXT NOT NULL,
                                    path TEXT NOT NULL,
                                    date TEXT NOT NULL,
                                    PRIMARY KEY (first, second)
                                )
                            """.trimIndent()
                        )
                        db.execSQL(
                            """
                                INSERT OR IGNORE INTO pairs
                                SELECT first, second, path, date FROM variants
                                ORDER BY latest DESC, date DESC, path
                            """.trimIndent()
                        )
                        db.execSQL("CREATE INDEX pairs_second ON pairs (second)")
                        val buffer = ByteArray(64 * 1024)
                        val digest = MessageDigest.getInstance("SHA-256")
                        val crc = CRC32()
                        db.rawQuery(
                            "SELECT path, sha256, size FROM images ORDER BY path",
                            null
                        ).use { images ->
                            val total = images.count.toLong()
                            var completed = 0L
                            while (images.moveToNext()) {
                                jobContext.ensureActive()
                                val path = images.getString(0)
                                val expectedSize = images.getLong(2)
                                digest.reset()
                                DataInputStream(
                                    DigestInputStream(
                                        archive.getInputStream(archive.getEntry(path)),
                                        digest
                                    ).buffered()
                                ).use { input ->
                                    val signature = ByteArray(8)
                                    input.readFully(signature)
                                    require(signature.contentEquals(pngSignature)) { "Not a PNG image: $path" }
                                    val chunkType = ByteArray(4)
                                    var remaining = expectedSize - signature.size
                                    var firstChunk = true
                                    var hasImageData = false
                                    while (true) {
                                        jobContext.ensureActive()
                                        require(remaining >= 12) { "Incomplete PNG: $path" }
                                        val length = input.readInt()
                                        require(length >= 0 && length.toLong() <= remaining - 12) {
                                            "Invalid PNG chunk size: $path"
                                        }
                                        input.readFully(chunkType)
                                        val type = String(chunkType, Charsets.US_ASCII)
                                        require(if (firstChunk) type == "IHDR" && length == 13 else type != "IHDR") {
                                            "Invalid PNG header: $path"
                                        }
                                        crc.reset()
                                        crc.update(chunkType)
                                        var left = length
                                        while (left > 0) {
                                            jobContext.ensureActive()
                                            val count = minOf(left, buffer.size)
                                            input.readFully(buffer, 0, count)
                                            crc.update(buffer, 0, count)
                                            if (firstChunk) {
                                                val dimensions = ByteBuffer.wrap(buffer, 0, 8)
                                                require(dimensions.int in 1..5000 && dimensions.int in 1..5000) {
                                                    "Image dimensions are too large: $path"
                                                }
                                            }
                                            left -= count
                                        }
                                        require(
                                            crc.value == (input.readInt().toLong() and 0xffffffffL)
                                        ) {
                                            "PNG chunk checksum does not match: $path"
                                        }
                                        remaining -= length + 12L
                                        if (type == "IDAT" && length > 0) hasImageData = true
                                        if (type == "IEND") {
                                            require(length == 0 && hasImageData && remaining == 0L && input.read() == -1) {
                                                "Invalid PNG ending: $path"
                                            }
                                            break
                                        }
                                        firstChunk = false
                                    }
                                }
                                require(digest.digest().toHexString() == images.getString(1)) {
                                    "Image checksum does not match: $path"
                                }
                                completed++
                                if (completed % 32L == 0L || completed == total) onProgress(
                                    completed,
                                    total
                                )
                            }
                        }
                    }
                }
            }
        }

        private class ManifestReader(reader: Reader) : FilterReader(reader) {
            private var depth = 0
            private var inString = false
            private var escaped = false

            override fun read(): Int = super.read().also {
                if (it >= 0) checkCharacter(it.toChar())
            }

            override fun read(
                buffer: CharArray,
                offset: Int,
                count: Int
            ): Int = super.read(buffer, offset, count).also { read ->
                for (index in offset until offset + read) checkCharacter(buffer[index])
            }

            private fun checkCharacter(char: Char) {
                if (inString) {
                    if (escaped) escaped = false
                    else when (char) {
                        '\\' -> escaped = true
                        '"' -> inString = false
                    }
                } else {
                    when (char) {
                        '"' -> inString = true
                        '{', '[' -> require(++depth <= 32) { "Manifest nesting is too deep" }
                        '}', ']' -> depth--
                    }
                }
            }
        }

        private data class ManifestVariant(
            val path: String,
            val date: String,
            val latest: Boolean
        )

        private fun JsonReader.text(maxLength: Int): String = nextString().also {
            require(it.length <= maxLength) { "Manifest value is too long" }
        }
    }
}
