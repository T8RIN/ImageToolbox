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

package com.t8rin.imagetoolbox.feature.markup_layers.data.project

import android.net.Uri
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Outline
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import com.t8rin.imagetoolbox.core.settings.presentation.model.UiFontFamily
import com.t8rin.imagetoolbox.core.settings.presentation.model.asFontType
import com.t8rin.imagetoolbox.core.settings.presentation.model.asUi
import com.t8rin.imagetoolbox.core.ui.utils.helper.entries
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.extension
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerPosition
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProject
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProjectResult
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ProjectBackground
import com.t8rin.logger.makeLog
import java.io.File
import javax.inject.Inject

internal class MarkupMapper @Inject constructor(
    private val settingsManager: SettingsManager
) {

    fun map(project: MarkupProject, registry: AssetRegistry) = project.toSnapshot(registry)

    suspend fun map(
        project: MarkupProjectFile,
        extractionDir: File
    ) = project.toDomain(extractionDir)

    private fun MarkupProject.toSnapshot(
        assetRegistry: AssetRegistry
    ): MarkupProjectFile = MarkupProjectFile(
        version = MarkupProjectVersion,
        background = background.toSnapshot(assetRegistry),
        layers = layers.toSnapshotList(assetRegistry, prefix = "layer"),
        lastLayers = lastLayers.toSnapshotList(assetRegistry, prefix = "last"),
        undoneLayers = undoneLayers.toSnapshotList(assetRegistry, prefix = "undone")
    )

    private suspend fun MarkupProjectFile.toDomain(
        extractionDir: File
    ): MarkupProjectResult {
        if (version != MarkupProjectVersion) {
            return MarkupProjectResult.Error.UnsupportedVersion(
                version = version,
                message = getString(R.string.unsupported_markup_project_version, version)
            )
        }

        return MarkupProjectResult.Success(
            project = MarkupProject(
                background = background.toDomain(extractionDir),
                layers = layers.toDomainLayers(extractionDir),
                lastLayers = lastLayers.toDomainLayers(extractionDir),
                undoneLayers = undoneLayers.toDomainLayers(extractionDir)
            )
        )
    }

    private fun List<MarkupLayer>.toSnapshotList(
        assetRegistry: AssetRegistry,
        prefix: String
    ): List<LayerSnapshot> = mapIndexed { index, layer ->
        layer.toSnapshot(
            index = index,
            assetRegistry = assetRegistry,
            prefix = prefix
        )
    }

    private fun ProjectBackground.toSnapshot(
        assetRegistry: AssetRegistry
    ): BackgroundSnapshot = when (this) {
        is ProjectBackground.Color -> BackgroundSnapshot(
            type = BackgroundType.Color,
            width = width,
            height = height,
            color = color
        )

        is ProjectBackground.Image -> {
            val source = uri
            val entryName = assetRegistry.register(
                source = source,
                proposedEntryName = assetEntryName(
                    prefix = "background",
                    extension = sourceExtension(source)
                )
            )
            BackgroundSnapshot(
                type = BackgroundType.Image,
                assetPath = entryName
            )
        }

        ProjectBackground.None -> BackgroundSnapshot(type = BackgroundType.None)
    }

    private fun MarkupLayer.toSnapshot(
        index: Int,
        assetRegistry: AssetRegistry,
        prefix: String
    ): LayerSnapshot {
        val layerType = type
        return LayerSnapshot(
            type = layerType.toSnapshotType(),
            position = position.toSnapshot(),
            visibleLineCount = visibleLineCount,
            cornerRadiusPercent = cornerRadiusPercent.coerceIn(0, 50),
            blendingMode = blendingMode.value,
            text = (layerType as? LayerType.Text)?.toSnapshot(
                assetRegistry = assetRegistry,
                fontPrefix = "$prefix-$index-font"
            ),
            picture = layerType.toPictureSnapshot(
                assetRegistry = assetRegistry,
                prefix = "$prefix-$index"
            )
        )
    }

    private fun LayerPosition.toSnapshot(): PositionSnapshot = PositionSnapshot(
        scale = scale,
        rotation = rotation,
        isFlippedHorizontally = isFlippedHorizontally,
        isFlippedVertically = isFlippedVertically,
        offsetX = offsetX,
        offsetY = offsetY,
        alpha = alpha,
        canvasWidth = currentCanvasSize.width,
        canvasHeight = currentCanvasSize.height,
        coerceToBounds = coerceToBounds,
        isVisible = isVisible
    )

    private fun LayerType.toSnapshotType(): LayerSnapshotType = when (this) {
        is LayerType.Text -> LayerSnapshotType.Text
        is LayerType.Picture.Image -> LayerSnapshotType.Image
        is LayerType.Picture.Sticker -> LayerSnapshotType.Sticker
    }

    private fun LayerType.Text.toSnapshot(
        assetRegistry: AssetRegistry,
        fontPrefix: String
    ): TextSnapshot = TextSnapshot(
        color = color,
        size = size,
        font = font?.toSnapshot(
            assetRegistry = assetRegistry,
            prefix = fontPrefix
        ),
        backgroundColor = backgroundColor,
        text = text,
        decorations = decorations.map(Enum<*>::name),
        outline = outline?.toSnapshot(),
        alignment = alignment.name
    )

    private fun LayerType.toPictureSnapshot(
        assetRegistry: AssetRegistry,
        prefix: String
    ): PictureSnapshot? = when (this) {
        is LayerType.Picture.Image -> {
            val source = imageData.toPersistableSource()
            val entryName = assetRegistry.register(
                source = source,
                proposedEntryName = assetEntryName(
                    prefix = prefix,
                    extension = sourceExtension(source)
                )
            )
            PictureSnapshot(assetPath = entryName)
        }

        is LayerType.Picture.Sticker -> PictureSnapshot(
            value = imageData.toString()
        )

        is LayerType.Text -> null
    }

    private fun Outline.toSnapshot(): OutlineSnapshot = OutlineSnapshot(
        color = color,
        width = width
    )

    private suspend fun List<LayerSnapshot>.toDomainLayers(
        extractionDir: File
    ): List<MarkupLayer> = map { it.toDomain(extractionDir) }

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

    private suspend fun LayerSnapshot.toDomain(
        extractionDir: File
    ): MarkupLayer = MarkupLayer(
        type = toDomainType(extractionDir),
        position = position.toDomain(),
        contentSize = IntegerSize.Zero,
        visibleLineCount = visibleLineCount,
        cornerRadiusPercent = cornerRadiusPercent.coerceIn(0, 50),
        blendingMode = BlendingMode.entries.find { it.value == blendingMode }
            ?: BlendingMode.SrcOver
    )

    private suspend fun LayerSnapshot.toDomainType(
        extractionDir: File
    ): LayerType = when (type) {
        LayerSnapshotType.Text -> {
            val value = text ?: error("Missing text layer data")
            value.toDomain(extractionDir)
        }

        LayerSnapshotType.Image -> LayerType.Picture.Image(
            imageData = File(extractionDir, picture?.assetPath.orEmpty()).toUri().toString()
        )

        LayerSnapshotType.Sticker -> LayerType.Picture.Sticker(
            imageData = picture?.value.orEmpty()
        )
    }

    private suspend fun TextSnapshot.toDomain(
        extractionDir: File
    ): LayerType.Text = LayerType.Text(
        color = color,
        size = size,
        font = font?.toDomain(extractionDir),
        backgroundColor = backgroundColor,
        text = text,
        decorations = decorations.toDomainDecorations(),
        outline = outline?.toDomain(),
        alignment = alignment.toDomainAlignment()
    )

    private fun List<String>.toDomainDecorations(): List<LayerType.Text.Decoration> {
        return mapNotNull { value ->
            runCatching {
                LayerType.Text.Decoration.valueOf(value)
            }.onFailure(Throwable::makeLog).getOrNull()
        }
    }

    private fun String.toDomainAlignment(): LayerType.Text.Alignment {
        return runCatching {
            LayerType.Text.Alignment.valueOf(this)
        }.getOrDefault(LayerType.Text.Alignment.Start)
    }

    private fun PositionSnapshot.toDomain(): LayerPosition = LayerPosition(
        scale = scale,
        rotation = rotation,
        isFlippedHorizontally = isFlippedHorizontally,
        isFlippedVertically = isFlippedVertically,
        offsetX = offsetX,
        offsetY = offsetY,
        alpha = alpha,
        currentCanvasSize = IntegerSize(
            width = canvasWidth,
            height = canvasHeight
        ),
        coerceToBounds = coerceToBounds,
        isVisible = isVisible
    )

    private fun OutlineSnapshot.toDomain(): Outline = Outline(
        color = color,
        width = width
    )

    private fun FontType.toSnapshot(
        assetRegistry: AssetRegistry,
        prefix: String
    ): FontSnapshot =
        when (this) {
            is FontType.File -> {
                val source = path
                val fileName = File(path).name.takeIf(String::isNotBlank)
                    ?: "$prefix.ttf"
                val entryName = assetRegistry.register(
                    source = source,
                    proposedEntryName = "assets/fonts/$prefix/$fileName"
                )
                FontSnapshot(
                    type = FontSnapshotType.File,
                    path = path,
                    assetPath = entryName,
                    filename = fileName
                )
            }

            is FontType.Resource -> {
                val source = "android.resource://${appContext.packageName}/$resId"
                val entryName = assetRegistry.register(
                    source = source,
                    proposedEntryName = "assets/fonts/$prefix/resource-$resId.font"
                )
                FontSnapshot(
                    type = FontSnapshotType.Resource,
                    resourceId = resId,
                    resourceName = runCatching {
                        appContext.resources.getResourceEntryName(resId)
                    }.getOrNull(),
                    familyKey = asUi()
                        .takeIf { (it.type as? FontType.Resource)?.resId == resId }
                        ?.asDomain()
                        ?.asString(),
                    assetPath = entryName,
                    filename = "resource-$resId.font"
                )
            }
        }

    private suspend fun FontSnapshot.toDomain(
        extractionDir: File
    ): FontType? =
        when (type) {
            FontSnapshotType.File -> path
                ?.takeIf { File(it).exists() }
                ?.let { FontType.File(it) }
                ?: restoreFontFromAsset(extractionDir)

            FontSnapshotType.Resource -> familyKey
                ?.let { DomainFontFamily.fromString(familyKey)?.asFontType() }
                ?: resourceName
                    ?.let(::resolveResourceByKnownFonts)
                    ?.let { FontType.Resource(it) }
                ?: resourceId
                    ?.takeIf(::isValidFontResource)
                    ?.let { FontType.Resource(it) }
                ?: restoreFontFromAsset(extractionDir)
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
    ): String = source.toUri().extension()
        ?.takeIf(String::isNotBlank)
        ?: source.substringAfterLast('.', "")
            .takeIf(String::isNotBlank)
        ?: "png"

    private suspend fun FontSnapshot.restoreFontFromAsset(
        extractionDir: File
    ): FontType.File? {
        val fontAsset = assetPath
            ?.let { File(extractionDir, it) }
            ?.takeIf(File::exists)
            ?: return null

        val preferredFilename = filename
            ?.let { File(it).name }
            ?.takeIf(String::isNotBlank)

        val existingFont = settingsManager.settingsState.value.customFonts
            .firstOrNull { custom ->
                val file = File(custom.filePath)
                file.exists() && preferredFilename != null && file.name.equals(
                    preferredFilename,
                    ignoreCase = true
                )
            }
            ?.filePath
            ?.let(FontType::File)

        if (existingFont != null) return existingFont

        return settingsManager.importCustomFont(fontAsset.toUri().toString())
            ?.let { imported ->
                FontType.File(imported.filePath)
            } ?: FontType.File(fontAsset.absolutePath)
    }

    private fun resolveResourceByKnownFonts(
        resourceName: String
    ): Int? = UiFontFamily.defaultEntries
        .mapNotNull { (it.type as? FontType.Resource)?.resId }
        .firstOrNull { resId ->
            runCatching {
                appContext.resources.getResourceEntryName(resId) == resourceName
            }.getOrDefault(false)
        }

    private fun isValidFontResource(
        resourceId: Int
    ): Boolean = runCatching {
        appContext.resources.getResourceTypeName(resourceId) == "font"
    }.getOrDefault(false)

}
