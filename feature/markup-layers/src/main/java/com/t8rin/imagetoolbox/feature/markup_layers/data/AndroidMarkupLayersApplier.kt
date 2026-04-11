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

package com.t8rin.imagetoolbox.feature.markup_layers.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.createZip
import com.t8rin.imagetoolbox.core.utils.putEntry
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.AssetRegistry
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupMapper
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectFile
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectJsonEntry
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.ProjectArchive
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.ProjectFileLoadResult
import com.t8rin.imagetoolbox.feature.markup_layers.data.utils.LayersRenderer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayersApplier
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProject
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProjectResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import javax.inject.Inject

internal class AndroidMarkupLayersApplier @Inject constructor(
    @ApplicationContext private val context: Context,
    dispatchersHolder: DispatchersHolder,
    private val mapper: MarkupMapper,
    private val renderer: LayersRenderer,
    private val jsonParser: JsonParser,
) : MarkupLayersApplier<Bitmap>, DispatchersHolder by dispatchersHolder {

    private val projectCacheRoot by lazy {
        File(context.filesDir, "markup-projects").apply(File::mkdirs)
    }

    override suspend fun applyToImage(
        image: Bitmap,
        layers: List<MarkupLayer>,
        measuredContentSizes: List<IntegerSize?>
    ): Bitmap = withContext(defaultDispatcher) {
        renderer.render(
            backgroundImage = image,
            layers = layers,
            measuredContentSizes = measuredContentSizes
        )
    }

    override suspend fun saveProject(
        destination: Writeable,
        project: MarkupProject
    ) = withContext(ioDispatcher) {
        writeProjectArchive(
            destination = destination,
            archive = project.toArchive() ?: return@withContext
        )
    }

    override suspend fun openProject(uri: String): MarkupProjectResult = withContext(ioDispatcher) {
        clearProjectCache()
        try {
            val extractionDir =
                File(projectCacheRoot, UUID.randomUUID().toString()).apply(File::mkdirs)

            val loadResult = loadProjectFile(
                uri = uri,
                extractionDir = extractionDir
            )

            when (loadResult) {
                is ProjectFileLoadResult.Error -> loadResult.error
                is ProjectFileLoadResult.Success -> mapper.map(
                    project = loadResult.projectFile,
                    extractionDir = extractionDir
                )
            }
        } catch (t: Throwable) {
            clearProjectCache()
            if (t is ZipException) {
                invalidArchiveError()
            } else {
                MarkupProjectResult.Error.Exception(
                    throwable = t,
                    message = t.localizedMessage
                        ?: context.getString(R.string.something_went_wrong)
                )
            }
        }
    }

    override fun clearProjectCache() {
        projectCacheRoot.listFiles().orEmpty().forEach(File::deleteRecursively)
    }

    private fun MarkupProject.toArchive(): ProjectArchive? {
        val assetRegistry = AssetRegistry()
        val projectJson = jsonParser.toJson(
            obj = mapper.map(
                project = this,
                registry = assetRegistry
            ),
            type = MarkupProjectFile::class.java
        ) ?: return null

        return ProjectArchive(
            projectJson = projectJson,
            assets = assetRegistry.entries()
        )
    }

    private fun loadProjectFile(
        uri: String,
        extractionDir: File
    ): ProjectFileLoadResult {
        val projectJson = extractProjectArchive(
            uri = uri,
            extractionDir = extractionDir
        ) ?: return ProjectFileLoadResult.Error(invalidArchiveError())

        if (projectJson.isBlank()) {
            return ProjectFileLoadResult.Error(
                MarkupProjectResult.Error.MissingProjectFile(
                    message = context.getString(R.string.markup_project_missing_data)
                )
            )
        }

        val projectFile = jsonParser.fromJson<MarkupProjectFile>(
            json = projectJson,
            type = MarkupProjectFile::class.java
        ) ?: return ProjectFileLoadResult.Error(
            MarkupProjectResult.Error.InvalidProjectFile(
                message = context.getString(R.string.markup_project_corrupted)
            )
        )

        return ProjectFileLoadResult.Success(projectFile)
    }

    private fun invalidArchiveError(): MarkupProjectResult.Error.InvalidArchive {
        return MarkupProjectResult.Error.InvalidArchive(
            message = context.getString(R.string.markup_project_open_failed)
        )
    }

    private fun writeProjectArchive(
        destination: Writeable,
        archive: ProjectArchive
    ) {
        destination.outputStream().createZip { zip ->
            zip.putEntry(MarkupProjectJsonEntry) {
                it.write(archive.projectJson.toByteArray())
            }
            archive.assets.forEach { asset ->
                zip.putEntry(asset.entryName) { entry ->
                    openSourceStream(asset.source)?.use { input ->
                        input.copyTo(entry)
                    }
                }
            }
        }
    }

    private fun extractProjectArchive(
        uri: String,
        extractionDir: File
    ): String? {
        val extractionDirPath = extractionDir.canonicalPath + File.separator

        return context.contentResolver.openInputStream(uri.toUri())?.use { inputStream ->
            ZipInputStream(inputStream).use { zipIn ->
                var projectJson: String? = null
                var entry: ZipEntry?
                while (zipIn.nextEntry.also { entry = it } != null) {
                    entry?.let { zipEntry ->
                        if (!zipEntry.isDirectory && zipEntry.name == MarkupProjectJsonEntry) {
                            projectJson = zipIn.readBytes().decodeToString()
                            zipIn.closeEntry()
                            return@let
                        }

                        val output = File(extractionDir, zipEntry.name).canonicalFile
                        if (!output.path.startsWith(extractionDirPath)) {
                            throw ZipException("Invalid zip entry path: ${zipEntry.name}")
                        }

                        if (zipEntry.isDirectory) {
                            output.mkdirs()
                        } else {
                            output.parentFile?.mkdirs()
                            FileOutputStream(output).use { fos ->
                                zipIn.copyTo(fos)
                            }
                        }
                        zipIn.closeEntry()
                    }
                }
                projectJson
            }
        }
    }

    private fun openSourceStream(source: String) = when {
        source.startsWith("content://") || source.startsWith("file://") -> {
            context.contentResolver.openInputStream(source.toUri())
        }

        else -> File(source).takeIf(File::exists)?.inputStream()
            ?: context.contentResolver.openInputStream(source.toUri())
    }

}