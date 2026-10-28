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

package com.t8rin.imagetoolbox

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.tukaani.xz.LZMA2Options
import org.tukaani.xz.XZOutputStream
import java.util.zip.GZIPInputStream

@CacheableTask
abstract class CompressReleaseResourcesTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun compress() {
        val source = sourceDirectory.get().asFile
        val output = outputDirectory.get().asFile
        output.deleteRecursively()

        val options = LZMA2Options(6).apply { dictSize = 1 shl 20 }
        source.walkTopDown().filter { file ->
            file.isFile && (file.name == "help_texts.json" ||
                    file.name == "image_toolbox_universal_bold.gz")
        }.forEach { file ->
            val target = output.resolve(file.relativeTo(source).path)
                .resolveSibling("${file.nameWithoutExtension}.xz")
            target.parentFile.mkdirs()

            file.inputStream().buffered().let { input ->
                if (file.extension == "gz") GZIPInputStream(input) else input
            }.use { input ->
                XZOutputStream(
                    target.outputStream().buffered(),
                    options
                ).use { output -> input.copyTo(output) }
            }
        }
    }
}