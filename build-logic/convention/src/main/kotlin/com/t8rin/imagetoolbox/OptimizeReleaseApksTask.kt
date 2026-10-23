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

import com.android.apksig.ApkVerifier
import com.android.build.api.artifact.ArtifactTransformationRequest
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

@CacheableTask
abstract class OptimizeReleaseApksTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val zipalign: RegularFileProperty

    @get:Internal
    abstract val transformationRequest: Property<ArtifactTransformationRequest<OptimizeReleaseApksTask>>

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun optimize() {
        transformationRequest.get().submit(this) { artifact ->
            val input = File(artifact.outputFile)
            val output = outputDirectory.file(input.name).get().asFile
            output.parentFile.mkdirs()

            // CI signs the optimized APKs afterwards. Preserve signatures from signed IDE builds.
            if (ApkVerifier.Builder(input).build().verify().isVerified) {
                logger.lifecycle("Preserving already signed APK: ${input.name}")
                input.copyTo(output, overwrite = true)
            } else {
                execOperations.exec {
                    commandLine(zipalign.get().asFile, "-P", "16", "-f", "-z", "4", input, output)
                }
                logger.lifecycle("${input.name}: saved ${(input.length() - output.length()) / 1024} KiB")
            }
            output
        }
    }
}