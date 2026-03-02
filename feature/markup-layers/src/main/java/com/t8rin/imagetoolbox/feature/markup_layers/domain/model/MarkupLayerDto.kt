package com.t8rin.imagetoolbox.feature.markup_layers.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Outline
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.presentation.model.asFontType
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiFont
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.EditBoxState
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects (DTOs) for serializing the Markup Layer state to JSON.
 * These classes mirror the in-memory state but use only primitive, serializable types.
 */

@Serializable
data class MarkupProjectDto(
    val version: Int = 1,
    val layers: List<MarkupLayerDto>
)

@Serializable
data class MarkupLayerDto(
    val type: LayerTypeDto,
    val position: LayerPositionDto,
    val isActive: Boolean,
    val isVisible: Boolean,
    val coerceToBounds: Boolean
)

@Serializable
data class LayerPositionDto(
    val scale: Float,
    val rotation: Float,
    val offsetX: Float,
    val offsetY: Float,
    val alpha: Float,
    val canvasWidth: Int,
    val canvasHeight: Int
)

@Serializable
sealed interface LayerTypeDto {

    @Serializable
    data class Text(
        val color: Int,
        val size: Float,
        val fontPath: String?, // Path or identifier for the font
        val backgroundColor: Int,
        val text: String,
        val decorations: List<String>, // "Bold", "Italic", etc.
        val alignment: String, // "Start", "Center", "End"
        val outlineColor: Int?,
        val outlineWidth: Float?
    ) : LayerTypeDto

    @Serializable
    data class Image(
        val assetName: String // e.g., "assets/layer_2_image.png" inside the zip
    ) : LayerTypeDto

    @Serializable
    data class Sticker(
        val emojiString: String // Or however stickers are defined
    ) : LayerTypeDto
}

// --- MAPPING FUNCTIONS ---

@Composable
fun UiMarkupLayer.toDto(assetName: String? = null): MarkupLayerDto = MarkupLayerDto(
    type = type.toDto(assetName),
    position = LayerPositionDto(
        scale = state.scale,
        rotation = state.rotation,
        offsetX = state.offset.x,
        offsetY = state.offset.y,
        alpha = state.alpha,
        canvasWidth = state.canvasSize.width,
        canvasHeight = state.canvasSize.height
    ),
    isActive = state.isActive,
    isVisible = state.isVisible,
    coerceToBounds = state.coerceToBounds
)

@Composable
fun LayerType.toDto(assetName: String? = null): LayerTypeDto = when (this) {
    is LayerType.Text -> LayerTypeDto.Text(
        color = color,
        size = size,
        fontPath = font?.let {
            val domainFont = it.toUiFont().asDomain()
            if (domainFont !is DomainFontFamily.Custom) domainFont.asString() else null
        },
        backgroundColor = backgroundColor,
        text = text,
        decorations = decorations.map { it.name },
        alignment = alignment.name,
        outlineColor = outline?.color,
        outlineWidth = outline?.width
    )
    is LayerType.Picture.Image -> LayerTypeDto.Image(
        assetName = assetName ?: ""
    )
    is LayerType.Picture.Sticker -> LayerTypeDto.Sticker(
        emojiString = imageData.toString()
    )
}

fun MarkupLayerDto.toUiLayer(imageData: Any? = null): UiMarkupLayer = UiMarkupLayer(
    type = type.toDomain(imageData),
    state = EditBoxState(
        scale = position.scale,
        rotation = position.rotation,
        offset = Offset(position.offsetX, position.offsetY),
        alpha = position.alpha,
        isActive = isActive,
        isVisible = isVisible,
        canvasSize = IntegerSize(position.canvasWidth, position.canvasHeight),
        coerceToBounds = coerceToBounds
    )
)

fun LayerTypeDto.toDomain(imageData: Any? = null): LayerType = when (this) {
    is LayerTypeDto.Text -> LayerType.Text(
        color = color,
        size = size,
        font = fontPath?.let { DomainFontFamily.fromString(it).asFontType() },
        backgroundColor = backgroundColor,
        text = text,
        decorations = decorations.mapNotNull {
            runCatching { LayerType.Text.Decoration.valueOf(it) }.getOrNull()
        },
        alignment = runCatching { LayerType.Text.Alignment.valueOf(alignment) }
            .getOrDefault(LayerType.Text.Alignment.Center),
        outline = if (outlineColor != null && outlineWidth != null) {
            Outline(color = outlineColor, width = outlineWidth)
        } else null
    )
    is LayerTypeDto.Image -> LayerType.Picture.Image(
        imageData = imageData ?: assetName
    )
    is LayerTypeDto.Sticker -> LayerType.Picture.Sticker(
        imageData = emojiString
    )
}