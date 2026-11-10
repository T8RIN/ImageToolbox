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

package com.t8rin.neural_tools.ocr

import com.t8rin.neural_tools.NeuralTool
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.zip.ZipFile

class GalleryOcrModels(private val directory: File) : NeuralTool() {

    val isInstalled: Boolean
        get() = File(root, ".complete").isFile

    private val root get() = File(directory, VERSION)
    internal val detector get() = File(root, DETECTOR)
    internal val router get() = File(root, ROUTER)
    internal val labels
        get() = JSONArray(File(root, LABELS).readText()).strings().also {
            check(it.size == 27) { "Invalid OCR router labels" }
        }
    internal val config get() = Config(JSONObject(File(root, CONFIG).readText()))

    internal fun tessdata(profile: Profile) = File(
        root, if (profile.imported) "languages" else "models/tesseract-scripts"
    )

    suspend fun download(onProgress: suspend (Float) -> Unit) = coroutineScope {
        if (isInstalled) return@coroutineScope

        val archive = File.createTempFile("gallery-ocr-", ".zip", context.cacheDir)
        try {
            downloader.download(
                url = URL,
                destinationPath = archive.absolutePath,
                onProgress = { onProgress(it.currentPercent.coerceIn(0f, 1f)) }
            )
            currentCoroutineContext().ensureActive()
            install(archive)
        } finally {
            archive.delete()
        }
    }

    suspend fun install(archive: File) = coroutineScope {
        if (isInstalled) return@coroutineScope

        check(
            archive.length() == ARCHIVE_SIZE && archive.inputStream()
                .use { it.sha256() } == SHA256) {
            "Invalid gallery OCR archive"
        }

        directory.mkdirs()
        val staging = File(directory, "$VERSION.tmp")
        staging.deleteRecursively()
        check(staging.mkdir())
        try {
            ZipFile(archive).use { zip ->
                val assets = JSONObject(
                    zip.getInputStream(zip.getEntry("manifest.json"))
                        .bufferedReader().use { it.readText() }
                ).getJSONObject("assets")

                fun assetSize(path: String) = assets.getJSONObject(path).getLong("bytes")

                for (path in listOf(DETECTOR, ROUTER, LABELS, CONFIG)) {
                    zip.extract(path, File(staging, path), assetSize(path))
                }
                val config = Config(JSONObject(File(staging, CONFIG).readText()))
                config.routes.values.filterNot { it.imported }
                    .flatMap { it.codes }.distinct().forEach { code ->
                        val path = "models/tesseract-scripts/tessdata/$code.traineddata"
                        zip.extract(path, File(staging, path), assetSize(path))
                    }

                val importPath = config.json.getJSONObject("import_zip").getString("file")
                val languages = File(staging, "languages.zip")
                zip.extract(importPath, languages, assetSize(importPath))
                ZipFile(languages).use { import ->
                    val entries = config.json.getJSONObject("imported_languages")
                    val codes = (config.routes.values.filter { it.imported } + config.fallback)
                        .flatMap { it.codes }.distinct()
                    for (code in codes) {
                        val entry = entries.getJSONObject(code)
                        // Each code has one selected variant in profiles.json.
                        import.extract(
                            entry.getString("entry"),
                            File(staging, "languages/tessdata/$code.traineddata"),
                            entry.getLong("bytes")
                        )
                    }
                }
                check(languages.delete())
            }
            currentCoroutineContext().ensureActive()
            check(File(staging, ".complete").createNewFile())
            check(!root.exists() || root.deleteRecursively())
            check(staging.renameTo(root)) { "Could not install gallery OCR models" }
        } finally {
            staging.deleteRecursively()
        }
    }

    private suspend fun ZipFile.extract(path: String, file: File, size: Long) = coroutineScope {
        val entry = checkNotNull(getEntry(path)) { "Missing OCR asset $path" }
        check(entry.size == size)
        file.parentFile?.mkdirs()
        getInputStream(entry).use { input ->
            file.outputStream().buffered().use { output ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var written = 0L
                while (true) {
                    currentCoroutineContext().ensureActive()
                    val count = input.read(buffer)
                    if (count < 0) break
                    written += count
                    check(written <= size)
                    output.write(buffer, 0, count)
                }
                check(written == size)
            }
        }
    }

    private suspend fun InputStream.sha256(): String = coroutineScope {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            currentCoroutineContext().ensureActive()
            val count = read(buffer)
            if (count < 0) break
            digest.update(buffer, 0, count)
        }
        return@coroutineScope digest.digest().joinToString("") { "%02x".format(it) }
    }

    internal class Config(val json: JSONObject) {
        private val detector = json.getJSONObject("detector")
        val longSide = detector.getInt("long_side")
        val maskThreshold = detector.getDouble("mask_threshold")
        val boxThreshold = detector.getDouble("box_threshold")
        val unclipRatio = detector.getDouble("unclip_ratio")
        val accept = json.getJSONObject("router").getDouble("accept")
        val margin = json.getJSONObject("router").getDouble("margin")
        val maxMillis = json.getLong("default_max_ms")
        val maxRegions = json.getInt("default_max_regions")
        val maxProfiles = json.getInt("max_profiles_per_photo")
        val maxLineMillis = json.getLong("max_line_ms")
        val minimumConfidence = json.getInt("minimum_word_confidence")
        val fallback = Profile(true, json.getJSONArray("fallback_languages").strings())
        val routes = json.getJSONObject("routes").let { routes ->
            routes.keys().asSequence().associateWith { key ->
                val route = routes.getJSONObject(key)
                Profile(
                    route.getString("source") == "import",
                    route.getJSONArray("codes").strings()
                )
            }
        }
    }

    internal data class Profile(val imported: Boolean, val codes: List<String>)

    companion object {
        private const val VERSION = "0.1.0"
        private const val SHA256 =
            "ab4fdb4f330d65230c7375dff0de9b2495d2fad4b626a81934334af162e3ea4e"
        const val ARCHIVE_SIZE = 227_830_216L
        const val URL =
            "https://huggingface.co/T8RIN/imagetoolbox-models/resolve/main/ImageToolbox-OCR-Kit.zip"
        private const val DETECTOR = "models/detector/pp-ocrv5-mobile-det.onnx"
        private const val ROUTER = "models/router/script-line.onnx"
        private const val LABELS = "models/router/labels.json"
        private const val CONFIG = "profiles.json"

        private fun JSONArray.strings() = List(length(), ::getString)
    }
}