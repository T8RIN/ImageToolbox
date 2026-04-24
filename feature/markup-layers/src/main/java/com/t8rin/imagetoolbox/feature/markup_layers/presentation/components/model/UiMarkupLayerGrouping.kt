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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.layerCornerRadiusPercent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.EditBoxState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

internal fun UiMarkupLayer.uiCornerRadiusPercent(): Int = if (isGroup) {
    0
} else {
    type.layerCornerRadiusPercent(cornerRadiusPercent)
}

internal fun UiMarkupLayer.deepDuplicate(): UiMarkupLayer = UiMarkupLayer(
    type = type,
    visibleLineCount = visibleLineCount,
    cornerRadiusPercent = cornerRadiusPercent,
    blendingMode = blendingMode,
    isLocked = isLocked,
    groupedLayers = groupedLayers.map(UiMarkupLayer::deepDuplicate),
    state = state.copy(
        isActive = false,
        isInEditMode = false
    )
)

internal fun UiMarkupLayer.renderCopy(): UiMarkupLayer = UiMarkupLayer(
    id = id,
    type = type,
    visibleLineCount = visibleLineCount,
    cornerRadiusPercent = cornerRadiusPercent,
    blendingMode = blendingMode,
    isLocked = false,
    groupedLayers = groupedLayers.map(UiMarkupLayer::renderCopy),
    state = state.copy(
        isActive = false,
        isInEditMode = false
    )
)

internal fun UiMarkupLayer.groupChildAt(
    center: Offset
): UiMarkupLayer = copy(
    state = state.copy(
        offset = state.offset - center,
        isActive = false,
        isInEditMode = false
    )
)

internal fun UiMarkupLayer.effectiveCoerceToBounds(): Boolean = state.coerceToBounds &&
        groupedLayers.all(UiMarkupLayer::effectiveCoerceToBounds)

internal fun UiMarkupLayer.withCoerceToBoundsRecursively(
    value: Boolean
): UiMarkupLayer = copy(
    groupedLayers = groupedLayers.map { child ->
        child.withCoerceToBoundsRecursively(value)
    },
    state = state.copy(
        coerceToBounds = value
    )
)

internal data class GroupPreviewData(
    val layers: List<UiMarkupLayer>,
    val contentSize: IntSize,
    val referenceSize: Int
)

internal fun UiMarkupLayer.groupContentSize(): IntSize? = localLeafLayers()
    .combinedBounds()
    ?.toIntSize()

internal fun UiMarkupLayer.canvasLeafLayers(
    canvasSize: IntegerSize = state.canvasSize,
    coerceScale: Boolean = false
): List<UiMarkupLayer> {
    val rootState = state.adjustedToCanvasSize(
        canvasSize = canvasSize,
        forceScaleAdjustment = true
    ).let { state ->
        if (coerceScale) {
            state.copy(
                scale = 1f
            )
        } else {
            state
        }
    }

    return groupedLayers.flatMap { child ->
        child.flattenLeafLayers(
            parentTransform = rootState.toLayerTransform(),
            inheritedAlpha = rootState.alpha,
            inheritedVisible = rootState.isVisible,
            rootCanvasSize = rootState.canvasSize
        )
    }
}

internal fun UiMarkupLayer.coerceGroupToBounds() {
    if (!isGroup || !state.coerceToBounds) return

    val canvasSize = state.canvasSize.takeIf { it.width > 0 && it.height > 0 } ?: return
    val bounds = canvasLeafLayers(canvasSize = canvasSize).combinedBounds() ?: return
    val constrainedOffset = state.offset + bounds.offsetCorrection(canvasSize)

    if (constrainedOffset != state.offset) {
        state.offset = constrainedOffset
    }
}

internal fun UiMarkupLayer.applyGroupGlobalChanges(
    zoomChange: Float = 1f,
    offsetChange: Offset = Offset.Zero,
    rotationChange: Float = 0f
) {
    if (!isGroup) return

    val currentScale = state.scale
    val targetScale = coerceGroupInteractiveScale(
        currentScale = currentScale,
        targetScale = currentScale * zoomChange
    )
    val proposedState = state.copy(
        scale = targetScale,
        rotation = state.rotation + rotationChange,
        offset = state.offset + offsetChange
    )

    val targetOffset = if (proposedState.coerceToBounds) {
        proposedState.withOffsetCorrection(
            layer = this,
            canvasSize = proposedState.canvasSize
        )
    } else {
        proposedState.offset
    }

    state.scale = targetScale
    state.rotation = proposedState.rotation
    state.offset = targetOffset
}

internal fun UiMarkupLayer.setGroupScalePrecisely(
    targetScale: Float
) {
    val resolvedScale = coerceGroupInteractiveScale(
        currentScale = state.scale,
        targetScale = targetScale
    )
    val currentScale = state.scale.coerceAtLeast(MIN_GROUP_SCALE_EPSILON)
    applyGroupGlobalChanges(
        zoomChange = resolvedScale / currentScale
    )
}

private fun EditBoxState.withOffsetCorrection(
    layer: UiMarkupLayer,
    canvasSize: IntegerSize
): Offset {
    if (canvasSize.width <= 0 || canvasSize.height <= 0) return offset

    val bounds = layer.copy(state = this)
        .canvasLeafLayers(canvasSize = canvasSize)
        .combinedBounds() ?: return offset

    return offset + bounds.offsetCorrection(canvasSize)
}

private fun LayerBounds.offsetCorrection(
    canvasSize: IntegerSize
): Offset {
    val halfCanvasWidth = canvasSize.width / 2f
    val halfCanvasHeight = canvasSize.height / 2f
    val dx = axisCoerceDelta(
        minEdge = -halfCanvasWidth,
        maxEdge = halfCanvasWidth,
        start = left,
        end = right
    )
    val dy = axisCoerceDelta(
        minEdge = -halfCanvasHeight,
        maxEdge = halfCanvasHeight,
        start = top,
        end = bottom
    )

    return Offset(dx, dy)
}

internal fun UiMarkupLayer.flattenToDomain(): List<MarkupLayer> {
    if (!isGroup) return listOf(asDomain())

    return canvasLeafLayers(
        canvasSize = state.canvasSize
    ).map(UiMarkupLayer::asDomain)
}

internal fun UiMarkupLayer.toPreviewGroupData(): GroupPreviewData {
    val previewLayers = previewLeafLayers()
    val bounds = previewLayers.combinedBounds() ?: LayerBounds(-0.5f, -0.5f, 0.5f, 0.5f)
    val previewSize = bounds.toIntSize()
    val previewCanvasSize = previewSize.toIntegerSize()
    val centeredLayers = previewLayers.map { layer ->
        layer.renderCopy().copy(
            state = layer.state.copy(
                offset = layer.state.offset - bounds.center,
                canvasSize = previewCanvasSize,
                isActive = false,
                isInEditMode = false
            )
        )
    }

    return GroupPreviewData(
        layers = centeredLayers,
        contentSize = previewSize,
        referenceSize = minOf(state.canvasSize.width, state.canvasSize.height).coerceAtLeast(1)
    )
}

internal fun UiMarkupLayer.composeToParentSpace(
    parent: UiMarkupLayer
): UiMarkupLayer {
    val rootCanvasSize = parent.state.canvasSize
    val detachedLayer = detachedSubtree()
    val composedTransform = parent.state.toLayerTransform().compose(
        detachedLayer.state.toLayerTransform()
    )
    val decomposition = composedTransform.matrix.decompose()

    return detachedLayer.copy(
        state = detachedLayer.state.copy(
            scale = decomposition.scale,
            rotation = decomposition.rotation,
            isFlippedHorizontally = decomposition.isFlippedHorizontally,
            isFlippedVertically = decomposition.isFlippedVertically,
            offset = composedTransform.offset,
            alpha = (parent.state.alpha * detachedLayer.state.alpha).coerceIn(0f, 1f),
            isActive = false,
            canvasSize = rootCanvasSize,
            isVisible = parent.state.isVisible && detachedLayer.state.isVisible,
            coerceToBounds = detachedLayer.state.coerceToBounds,
            isInEditMode = false
        )
    )
}

internal fun UiMarkupLayer.visualBounds(): LayerBounds {
    val size = state.contentSize.takeIf(IntSize::isSpecified) ?: IntSize(1, 1)
    val halfExtents = size.rotatedHalfExtents(
        degrees = state.rotation,
        cornerRadiusPercent = uiCornerRadiusPercent()
    )
    val scaledHalfWidth = halfExtents.x * state.scale
    val scaledHalfHeight = halfExtents.y * state.scale

    return LayerBounds(
        left = state.offset.x - scaledHalfWidth,
        top = state.offset.y - scaledHalfHeight,
        right = state.offset.x + scaledHalfWidth,
        bottom = state.offset.y + scaledHalfHeight
    )
}

internal data class UiMarkupLayerSnapshot(
    val id: Long,
    val type: com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType,
    val visibleLineCount: Int?,
    val cornerRadiusPercent: Int,
    val blendingMode: com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode,
    val isLocked: Boolean,
    val groupedLayers: List<UiMarkupLayerSnapshot>,
    val state: EditBoxStateSnapshot
)

internal data class EditBoxStateSnapshot(
    val scale: Float,
    val rotation: Float,
    val isFlippedHorizontally: Boolean,
    val isFlippedVertically: Boolean,
    val offsetX: Float,
    val offsetY: Float,
    val alpha: Float,
    val isActive: Boolean,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val contentWidth: Int,
    val contentHeight: Int,
    val isVisible: Boolean,
    val coerceToBounds: Boolean
)

internal fun UiMarkupLayer.toSnapshot(): UiMarkupLayerSnapshot = UiMarkupLayerSnapshot(
    id = id,
    type = type,
    visibleLineCount = visibleLineCount,
    cornerRadiusPercent = cornerRadiusPercent,
    blendingMode = blendingMode,
    isLocked = isLocked,
    groupedLayers = groupedLayers.map(UiMarkupLayer::toSnapshot),
    state = state.toSnapshot()
)

internal fun UiMarkupLayerSnapshot.toUi(): UiMarkupLayer {
    noteUiLayerId(id)
    return UiMarkupLayer(
        id = id,
        type = type,
        visibleLineCount = visibleLineCount,
        cornerRadiusPercent = cornerRadiusPercent,
        blendingMode = blendingMode,
        isLocked = isLocked,
        groupedLayers = groupedLayers.map(UiMarkupLayerSnapshot::toUi),
        state = state.toUi()
    )
}

private fun EditBoxState.toSnapshot(): EditBoxStateSnapshot = EditBoxStateSnapshot(
    scale = scale,
    rotation = rotation,
    isFlippedHorizontally = isFlippedHorizontally,
    isFlippedVertically = isFlippedVertically,
    offsetX = offset.x,
    offsetY = offset.y,
    alpha = alpha,
    isActive = isActive,
    canvasWidth = canvasSize.width,
    canvasHeight = canvasSize.height,
    contentWidth = contentSize.width,
    contentHeight = contentSize.height,
    isVisible = isVisible,
    coerceToBounds = coerceToBounds
)

private fun EditBoxStateSnapshot.toUi(): EditBoxState = EditBoxState(
    scale = scale,
    rotation = rotation,
    isFlippedHorizontally = isFlippedHorizontally,
    isFlippedVertically = isFlippedVertically,
    offset = Offset(offsetX, offsetY),
    alpha = alpha,
    isActive = isActive,
    canvasSize = IntegerSize(canvasWidth, canvasHeight),
    contentSize = IntSize(contentWidth, contentHeight),
    isVisible = isVisible,
    coerceToBounds = coerceToBounds
)

private data class LayerTransform(
    val matrix: TransformMatrix,
    val offset: Offset
) {
    fun compose(
        child: LayerTransform
    ): LayerTransform = LayerTransform(
        matrix = matrix * child.matrix,
        offset = offset + matrix.transform(child.offset)
    )
}

private data class TransformMatrix(
    val m00: Float,
    val m01: Float,
    val m10: Float,
    val m11: Float
) {
    operator fun times(
        other: TransformMatrix
    ): TransformMatrix = TransformMatrix(
        m00 = m00 * other.m00 + m01 * other.m10,
        m01 = m00 * other.m01 + m01 * other.m11,
        m10 = m10 * other.m00 + m11 * other.m10,
        m11 = m10 * other.m01 + m11 * other.m11
    )

    fun transform(
        offset: Offset
    ): Offset = Offset(
        x = m00 * offset.x + m01 * offset.y,
        y = m10 * offset.x + m11 * offset.y
    )

    fun decompose(): DecomposedTransform {
        val scale = sqrt(m00 * m00 + m10 * m10).coerceAtLeast(0.0001f)
        val determinant = m00 * m11 - m01 * m10

        return if (determinant < 0f) {
            DecomposedTransform(
                scale = scale,
                rotation = radiansToDegrees(
                    kotlin.math.atan2(-m10, -m00)
                ),
                isFlippedHorizontally = true,
                isFlippedVertically = false
            )
        } else {
            DecomposedTransform(
                scale = scale,
                rotation = radiansToDegrees(
                    kotlin.math.atan2(m10, m00)
                ),
                isFlippedHorizontally = false,
                isFlippedVertically = false
            )
        }
    }
}

private data class DecomposedTransform(
    val scale: Float,
    val rotation: Float,
    val isFlippedHorizontally: Boolean,
    val isFlippedVertically: Boolean
)

internal data class LayerBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val center: Offset
        get() = Offset(
            x = (left + right) / 2f,
            y = (top + bottom) / 2f
        )

    fun plus(
        other: LayerBounds
    ): LayerBounds = LayerBounds(
        left = minOf(left, other.left),
        top = minOf(top, other.top),
        right = maxOf(right, other.right),
        bottom = maxOf(bottom, other.bottom)
    )

    fun toIntSize(): IntSize = IntSize(
        width = ceil(right - left).roundToInt().coerceAtLeast(1),
        height = ceil(bottom - top).roundToInt().coerceAtLeast(1)
    )

    fun contains(point: Offset): Boolean = point.x in left..right && point.y in top..bottom

    fun axisCoerceDelta(
        minEdge: Float,
        maxEdge: Float,
        start: Float,
        end: Float
    ): Float {
        val size = end - start
        val availableSize = maxEdge - minEdge

        return if (size <= availableSize) {
            when {
                start < minEdge -> minEdge - start
                end > maxEdge -> maxEdge - end
                else -> 0f
            }
        } else {
            when {
                start > minEdge -> minEdge - start
                end < maxEdge -> maxEdge - end
                else -> 0f
            }
        }
    }
}

internal fun List<UiMarkupLayer>.combinedBounds(): LayerBounds? = map(UiMarkupLayer::visualBounds)
    .reduceOrNull(LayerBounds::plus)

private fun UiMarkupLayer.previewLeafLayers(): List<UiMarkupLayer> = canvasLeafLayers(
    coerceScale = true
)

private fun UiMarkupLayer.detachedSubtree(): UiMarkupLayer = copy(
    groupedLayers = groupedLayers.map(UiMarkupLayer::detachedSubtree),
    state = state.copy(
        isActive = false,
        isInEditMode = false
    )
)

private fun UiMarkupLayer.localLeafLayers(): List<UiMarkupLayer> = groupedLayers.flatMap { child ->
    child.flattenLeafLayers()
}

private fun EditBoxState.adjustedToCanvasSize(
    canvasSize: IntegerSize,
    forceScaleAdjustment: Boolean = false
): EditBoxState = copy().also {
    it.syncCanvasSize(
        value = canvasSize,
        forceScaleAdjustment = forceScaleAdjustment
    )
}

private fun UiMarkupLayer.flattenLeafLayers(
    parentTransform: LayerTransform? = null,
    inheritedAlpha: Float = 1f,
    inheritedVisible: Boolean = true,
    rootCanvasSize: IntegerSize = state.canvasSize,
    includeScale: Boolean = true
): List<UiMarkupLayer> {
    val currentTransform =
        parentTransform?.compose(state.toLayerTransform(includeScale = includeScale))
            ?: state.toLayerTransform(includeScale = includeScale)
    val combinedAlpha = (inheritedAlpha * state.alpha).coerceIn(0f, 1f)
    val combinedVisible = inheritedVisible && state.isVisible

    if (isGroup) {
        return groupedLayers.flatMap { child ->
            child.flattenLeafLayers(
                parentTransform = currentTransform,
                inheritedAlpha = combinedAlpha,
                inheritedVisible = combinedVisible,
                rootCanvasSize = rootCanvasSize,
                includeScale = includeScale
            )
        }
    }

    val decomposition = currentTransform.matrix.decompose()

    return listOf(
        copy(
            groupedLayers = emptyList(),
            state = state.copy(
                scale = decomposition.scale,
                rotation = decomposition.rotation,
                isFlippedHorizontally = decomposition.isFlippedHorizontally,
                isFlippedVertically = decomposition.isFlippedVertically,
                offset = currentTransform.offset,
                alpha = combinedAlpha,
                canvasSize = rootCanvasSize,
                isVisible = combinedVisible,
                isActive = false,
                isInEditMode = false
            )
        )
    )
}

private fun EditBoxState.toLayerTransform(
    includeScale: Boolean = true
): LayerTransform {
    val angle = rotation * DEGREES_TO_RADIANS
    val cos = cos(angle)
    val sin = sin(angle)
    val appliedScale = if (includeScale) scale else 1f
    val scaleX = appliedScale * if (isFlippedHorizontally) -1f else 1f
    val scaleY = appliedScale * if (isFlippedVertically) -1f else 1f

    return LayerTransform(
        matrix = TransformMatrix(
            m00 = cos * scaleX,
            m01 = -sin * scaleY,
            m10 = sin * scaleX,
            m11 = cos * scaleY
        ),
        offset = offset
    )
}

private fun IntSize.toIntegerSize(): IntegerSize = IntegerSize(
    width = width.coerceAtLeast(0),
    height = height.coerceAtLeast(0)
)

private fun IntSize.rotatedHalfExtents(
    degrees: Float,
    cornerRadiusPercent: Int
): Offset {
    val halfWidth = width / 2f
    val halfHeight = height / 2f
    val cornerRadiusPx = (
            minOf(width, height) * (cornerRadiusPercent.coerceIn(0, 50) / 100f)
            ).coerceIn(0f, minOf(halfWidth, halfHeight))
    val innerHalfWidth = (halfWidth - cornerRadiusPx).coerceAtLeast(0f)
    val innerHalfHeight = (halfHeight - cornerRadiusPx).coerceAtLeast(0f)

    val radians = degrees * DEGREES_TO_RADIANS
    val absCos = abs(cos(radians))
    val absSin = abs(sin(radians))

    return Offset(
        x = innerHalfWidth * absCos + innerHalfHeight * absSin + cornerRadiusPx,
        y = innerHalfWidth * absSin + innerHalfHeight * absCos + cornerRadiusPx
    )
}

private fun IntSize.isSpecified(): Boolean = width > 0 && height > 0

private fun radiansToDegrees(
    radians: Float
): Float = radians * 180f / PI.toFloat()

private fun coerceGroupInteractiveScale(
    currentScale: Float,
    targetScale: Float
): Float {
    val minimumValue = GROUP_SCALE_RANGE.start
    val maximumValue = GROUP_SCALE_RANGE.endInclusive
    val safeTargetScale = targetScale.coerceAtLeast(MIN_GROUP_SCALE_EPSILON)

    return when {
        safeTargetScale >= minimumValue -> safeTargetScale.coerceAtMost(maximumValue)
        currentScale < minimumValue -> safeTargetScale.coerceAtMost(maximumValue)
        else -> minimumValue
    }
}

private const val DEGREES_TO_RADIANS = (PI / 180f).toFloat()
private const val MIN_GROUP_SCALE_EPSILON = 0.0001f
private val GROUP_SCALE_RANGE = 0.1f..10f
