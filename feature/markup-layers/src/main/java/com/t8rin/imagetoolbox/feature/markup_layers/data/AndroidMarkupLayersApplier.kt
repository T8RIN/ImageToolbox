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
import android.net.Uri
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Outline
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import com.t8rin.imagetoolbox.core.utils.createZip
import com.t8rin.imagetoolbox.core.utils.putEntry
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.BackgroundSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.BackgroundType
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.FontSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.FontSnapshotType
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.LayerSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.LayerSnapshotType
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectFile
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectJsonEntry
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectVersion
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.OutlineSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.PictureSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.PositionSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.TextSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.data.utils.LayersRenderer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerPosition
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayersApplier
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProject
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProjectResult
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ProjectBackground
import com.t8rin.logger.makeLog
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
    private val renderer: LayersRenderer,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap>,
    private val jsonParser: JsonParser,
) : MarkupLayersApplier<Bitmap>, DispatchersHolder by dispatchersHolder {

    private val projectCacheRoot by lazy {
        File(context.cacheDir, "markup-projects").apply(File::mkdirs)
    }

    override suspend fun applyToImage(
        image: Bitmap,
        layers: List<MarkupLayer>
    ): Bitmap = withContext(defaultDispatcher) {
        renderer.render(
            backgroundImage = image,
            layers = layers,
        )
    }

    override suspend fun saveProject(
        uri: String,
        project: MarkupProject
    ): SaveResult = withContext(ioDispatcher) {
        val assets = linkedMapOf<String, ByteArray>()
        val projectJson = jsonParser.toJson(
            obj = project.toSnapshot(assets),
            type = MarkupProjectFile::class.java
        ) ?: return@withContext SaveResult.Error.Exception(
            Throwable("Unable to serialize markup project")
        )

        fileController.writeBytes(uri) { output ->
            output.outputStream().createZip { zip ->
                zip.putEntry(MarkupProjectJsonEntry) {
                    it.write(projectJson.toByteArray())
                }
                assets.forEach { (name, bytes) ->
                    zip.putEntry(name) {
                        it.write(bytes)
                    }
                }
            }
        }
    }

    override suspend fun openProject(uri: String): MarkupProjectResult = withContext(ioDispatcher) {
        clearProjectCache()

        val extractionDir = File(projectCacheRoot, UUID.randomUUID().toString()).apply { mkdirs() }

        runSuspendCatching {
            var projectJson: String? = null

            context.contentResolver.openInputStream(uri.toUri())?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry: ZipEntry?
                    while (zipIn.nextEntry.also { entry = it } != null) {
                        entry?.let { zipEntry ->
                            val output = File(extractionDir, zipEntry.name)
                            if (zipEntry.isDirectory) {
                                output.mkdirs()
                            } else {
                                output.parentFile?.mkdirs()
                                if (zipEntry.name == MarkupProjectJsonEntry) {
                                    projectJson = zipIn.readBytes().decodeToString()
                                } else {
                                    FileOutputStream(output).use { fos ->
                                        zipIn.copyTo(fos)
                                    }
                                }
                            }
                            zipIn.closeEntry()
                        }
                    }
                }
            } ?: return@runSuspendCatching MarkupProjectResult.Error.InvalidArchive(
                message = context.getString(R.string.markup_project_open_failed)
            )

            if (projectJson.isNullOrBlank()) {
                clearProjectCache()
                return@runSuspendCatching MarkupProjectResult.Error.MissingProjectFile(
                    message = context.getString(R.string.markup_project_missing_data)
                )
            }

            val projectJsonValue = projectJson
                ?: return@runSuspendCatching MarkupProjectResult.Error.MissingProjectFile(
                    message = context.getString(R.string.markup_project_missing_data)
                )

            jsonParser.fromJson<MarkupProjectFile>(
                json = projectJsonValue,
                type = MarkupProjectFile::class.java
            )?.toDomain(extractionDir) ?: run {
                clearProjectCache()
                MarkupProjectResult.Error.InvalidProjectFile(
                    message = context.getString(R.string.markup_project_corrupted)
                )
            }
        }.getOrElse {
            extractionDir.deleteRecursively()
            if (it is ZipException) {
                return@withContext MarkupProjectResult.Error.InvalidArchive(
                    message = context.getString(R.string.markup_project_open_failed)
                )
            }
            MarkupProjectResult.Error.Exception(
                throwable = it,
                message = it.localizedMessage ?: context.getString(R.string.something_went_wrong)
            )
        }
    }

    override fun clearProjectCache() {
        projectCacheRoot.listFiles().orEmpty().forEach(File::deleteRecursively)
    }

    private suspend fun MarkupProject.toSnapshot(
        assets: MutableMap<String, ByteArray>
    ): MarkupProjectFile = MarkupProjectFile(
        version = MarkupProjectVersion,
        imageFormat = imageFormat.title,
        saveExif = saveExif,
        background = background.toSnapshot(assets),
        layers = layers.mapIndexed { index, layer ->
            layer.toSnapshot(index, assets, "layer")
        },
        lastLayers = lastLayers.mapIndexed { index, layer ->
            layer.toSnapshot(index, assets, "last")
        },
        undoneLayers = undoneLayers.mapIndexed { index, layer ->
            layer.toSnapshot(index, assets, "undone")
        }
    )

    private suspend fun ProjectBackground.toSnapshot(
        assets: MutableMap<String, ByteArray>
    ): BackgroundSnapshot = when (this) {
        is ProjectBackground.Color -> BackgroundSnapshot(
            type = BackgroundType.Color,
            width = width,
            height = height,
            color = color
        )

        is ProjectBackground.Image -> {
            val source = uri
            val entryName = assetEntryName(
                prefix = "background",
                extension = sourceExtension(source)
            )
            assets[entryName] = readSourceBytes(source)
            BackgroundSnapshot(
                type = BackgroundType.Image,
                assetPath = entryName
            )
        }

        ProjectBackground.None -> BackgroundSnapshot(type = BackgroundType.None)
    }

    private suspend fun MarkupLayer.toSnapshot(
        index: Int,
        assets: MutableMap<String, ByteArray>,
        prefix: String
    ): LayerSnapshot = LayerSnapshot(
        type = when (type) {
            is LayerType.Text -> LayerSnapshotType.Text
            is LayerType.Picture.Image -> LayerSnapshotType.Image
            is LayerType.Picture.Sticker -> LayerSnapshotType.Sticker
        },
        position = PositionSnapshot(
            scale = position.scale,
            rotation = position.rotation,
            offsetX = position.offsetX,
            offsetY = position.offsetY,
            alpha = position.alpha,
            canvasWidth = position.currentCanvasSize.width,
            canvasHeight = position.currentCanvasSize.height,
            coerceToBounds = position.coerceToBounds,
            isVisible = position.isVisible
        ),
        text = (type as? LayerType.Text)?.let {
            TextSnapshot(
                color = it.color,
                size = it.size,
                font = it.font?.toSnapshot(),
                backgroundColor = it.backgroundColor,
                text = it.text,
                decorations = it.decorations.map(Enum<*>::name),
                outline = it.outline?.let { outline ->
                    OutlineSnapshot(
                        color = outline.color,
                        width = outline.width
                    )
                },
                alignment = it.alignment.name
            )
        },
        picture = when (val pictureType = type) {
            is LayerType.Picture.Image -> {
                val source = pictureType.imageData.toPersistableSource()
                val entryName = assetEntryName(
                    prefix = "$prefix-$index",
                    extension = sourceExtension(source)
                )
                assets[entryName] = readSourceBytes(source)
                PictureSnapshot(assetPath = entryName)
            }

            is LayerType.Picture.Sticker -> PictureSnapshot(
                value = pictureType.imageData.toString()
            )

            else -> null
        }
    )

    private fun MarkupProjectFile.toDomain(
        extractionDir: File
    ): MarkupProjectResult {
        if (version != MarkupProjectVersion) {
            return MarkupProjectResult.Error.UnsupportedVersion(
                version = version,
                message = context.getString(R.string.unsupported_markup_project_version, version)
            )
        }

        return MarkupProjectResult.Success(
            project = MarkupProject(
                imageFormat = imageFormat?.let(ImageFormat::get) ?: ImageFormat.Default,
                saveExif = saveExif,
                background = background.toDomain(extractionDir),
                layers = layers.map { it.toDomain(extractionDir) },
                lastLayers = lastLayers.map { it.toDomain(extractionDir) },
                undoneLayers = undoneLayers.map { it.toDomain(extractionDir) }
            )
        )
    }

    private fun BackgroundSnapshot.toDomain(
        extractionDir: File
    ): ProjectBackground = when (type) {
        BackgroundType.Image -> ProjectBackground.Image(
            uri = File(extractionDir, assetPath.orEmpty()).toUri().toString()
        )

        BackgroundType.Color -> ProjectBackground.Color(
            width = width ?: 1,
            height = height ?: 1,
            color = color ?: 0
        )

        BackgroundType.None -> ProjectBackground.None
    }

    private fun LayerSnapshot.toDomain(
        extractionDir: File
    ): MarkupLayer = MarkupLayer(
        type = when (type) {
            LayerSnapshotType.Text -> {
                val value = text ?: error("Missing text layer data")
                LayerType.Text(
                    color = value.color,
                    size = value.size,
                    font = value.font?.toDomain(),
                    backgroundColor = value.backgroundColor,
                    text = value.text,
                    decorations = value.decorations.mapNotNull {
                        runCatching {
                            LayerType.Text.Decoration.valueOf(it)
                        }.onFailure(Throwable::makeLog).getOrNull()
                    },
                    outline = value.outline?.let {
                        Outline(
                            color = it.color,
                            width = it.width
                        )
                    },
                    alignment = runCatching {
                        LayerType.Text.Alignment.valueOf(value.alignment)
                    }.getOrDefault(LayerType.Text.Alignment.Start)
                )
            }

            LayerSnapshotType.Image -> LayerType.Picture.Image(
                imageData = File(extractionDir, picture?.assetPath.orEmpty()).toUri().toString()
            )

            LayerSnapshotType.Sticker -> LayerType.Picture.Sticker(
                imageData = picture?.value.orEmpty()
            )
        },
        position = LayerPosition(
            scale = position.scale,
            rotation = position.rotation,
            offsetX = position.offsetX,
            offsetY = position.offsetY,
            alpha = position.alpha,
            currentCanvasSize = IntegerSize(
                width = position.canvasWidth,
                height = position.canvasHeight
            ),
            coerceToBounds = position.coerceToBounds,
            isVisible = position.isVisible
        )
    )

    private fun FontType.toSnapshot(): FontSnapshot =
        when (this) {
            is FontType.File -> FontSnapshot(
                type = FontSnapshotType.File,
                path = path
            )

            is FontType.Resource -> FontSnapshot(
                type = FontSnapshotType.Resource,
                resourceId = resId
            )
        }

    private fun FontSnapshot.toDomain(): FontType? =
        when (type) {
            FontSnapshotType.File -> path?.let {
                FontType.File(it)
            }

            FontSnapshotType.Resource -> resourceId?.let {
                FontType.Resource(it)
            }
        }

    private fun Any.toPersistableSource(): String = when (this) {
        is Uri -> toString()
        is File -> toUri().toString()
        else -> toString()
    }

    private fun assetEntryName(
        prefix: String,
        extension: String
    ): String = "assets/$prefix.$extension"

    private fun sourceExtension(
        source: String
    ): String = imageGetter.getExtension(source)
        ?.takeIf(String::isNotBlank)
        ?: source.substringAfterLast('.', "")
            .takeIf(String::isNotBlank)
        ?: "png"

    private suspend fun readSourceBytes(
        source: String
    ): ByteArray = when {
        source.startsWith("content://") || source.startsWith("file://") -> {
            fileController.readBytes(source)
        }

        else -> File(source).takeIf(File::exists)?.readBytes()
            ?: fileController.readBytes(source)
    }
}
