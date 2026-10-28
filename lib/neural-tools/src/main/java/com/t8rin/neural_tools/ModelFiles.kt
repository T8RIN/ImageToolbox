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

package com.t8rin.neural_tools

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

internal class ModelFiles(
    private val folder: String,
    private vararg val models: Model,
    private val directoryOverride: File? = null
) : NeuralTool() {

    data class Model(
        val name: String,
        val size: Long,
        val sha256: String
    )

    private val directory get() = directoryOverride ?: File(context.filesDir, "ai_models/$folder")
    private val verified = mutableMapOf<String, Pair<Long, Long>>()

    fun file(name: String) = File(directory, name)

    fun isDownloaded(): Boolean = models.all { isValid(it, file(it.name)) }

    private fun isValid(model: Model, file: File): Boolean {
        if (!file.isFile || file.length() != model.size) return false
        val signature = file.length() to file.lastModified()
        synchronized(verified) {
            if (verified[file.absolutePath] == signature) return true
        }
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var count = input.read(buffer)
            while (count >= 0) {
                digest.update(buffer, 0, count)
                count = input.read(buffer)
            }
        }
        val matches = digest.digest().joinToString("") { "%02x".format(it) } == model.sha256
        if (matches) synchronized(verified) { verified[file.absolutePath] = signature }
        return matches
    }

    suspend fun download(
        onProgress: suspend (Float) -> Unit
    ) = withContext(Dispatchers.IO) {
        check(directory.mkdirs() || directory.isDirectory)
        val totalSize = models.sumOf(Model::size)
        var completedSize = 0L
        onProgress(0f)
        models.forEach { model ->
            currentCoroutineContext().ensureActive()
            val destination = file(model.name)
            if (!isValid(model, destination)) {
                val temporary = File.createTempFile(model.name, ".download", directory)
                try {
                    downloader.download(
                        url = baseUrl.replace("*", "$folder/${model.name}?download=true"),
                        destinationPath = temporary.absolutePath,
                        onProgress = {
                            currentCoroutineContext().ensureActive()
                            val downloaded = it.currentPercent.coerceIn(0f, 1f) * model.size
                            onProgress((completedSize + downloaded) / totalSize)
                        }
                    )
                    currentCoroutineContext().ensureActive()
                    check(isValid(model, temporary)) { "Invalid model: ${model.name}" }
                    currentCoroutineContext().ensureActive()
                    check(temporary.renameTo(destination)) { "Cannot install model: ${model.name}" }
                } finally {
                    synchronized(verified) { verified.remove(temporary.absolutePath) }
                    temporary.delete()
                }
            }
            completedSize += model.size
            onProgress(completedSize.toFloat() / totalSize)
        }
    }

    fun delete() {
        check(!directory.exists() || directory.deleteRecursively())
        synchronized(verified) { verified.clear() }
    }
}