/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.utils.helper.image_vector

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.vector.DefaultGroupName
import androidx.compose.ui.graphics.vector.DefaultPivotX
import androidx.compose.ui.graphics.vector.DefaultPivotY
import androidx.compose.ui.graphics.vector.DefaultRotation
import androidx.compose.ui.graphics.vector.DefaultScaleX
import androidx.compose.ui.graphics.vector.DefaultScaleY
import androidx.compose.ui.graphics.vector.DefaultTranslationX
import androidx.compose.ui.graphics.vector.DefaultTranslationY
import androidx.compose.ui.graphics.vector.EmptyPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.graphics.vector.toPath
import androidx.compose.ui.util.fastForEach

internal class GroupComponent : VNode() {
    private var groupMatrix: Matrix? = null

    private val children = mutableListOf<VNode>()

    /**
     * Flag to determine if the contents of this group can be rendered with a single color
     * This is true if all the paths and groups within this group can be rendered with the
     * same color
     */
    var isTintable = true
        private set

    /**
     * Tint color to render all the contents of this group. This is configured only if all the
     * contents within the group are the same color
     */
    var tintColor = Color.Unspecified
        private set

    /**
     * Helper method to inspect whether the provided brush matches the current color of paths
     * within the group in order to help determine if only an alpha channel bitmap can be allocated
     * and tinted in order to save on memory overhead.
     */
    private fun markTintForBrush(brush: Brush?) {
        if (!isTintable) {
            return
        }
        if (brush != null) {
            if (brush is SolidColor) {
                markTintForColor(brush.value)
            } else {
                // If the brush is not a solid color then we require a explicit ARGB channels in the
                // cached bitmap
                markNotTintable()
            }
        }
    }

    /**
     * Helper method to inspect whether the provided color matches the current color of paths
     * within the group in order to help determine if only an alpha channel bitmap can be allocated
     * and tinted in order to save on memory overhead.
     */
    private fun markTintForColor(color: Color) {
        if (!isTintable) {
            return
        }

        if (color.isSpecified) {
            if (tintColor.isUnspecified) {
                // Initial color has not been specified, initialize the target color to the
                // one provided
                tintColor = color
            } else if (!tintColor.rgbEqual(color)) {
                // The given color does not match the rgb channels if our previous color
                // Therefore we require explicit ARGB channels in the cached bitmap
                markNotTintable()
            }
        }
    }

    private fun markTintForVNode(node: VNode) {
        if (node is PathComponent) {
            markTintForBrush(node.fill)
            markTintForBrush(node.stroke)
        } else if (node is GroupComponent) {
            if (node.isTintable && isTintable) {
                markTintForColor(node.tintColor)
            } else {
                markNotTintable()
            }
        }
    }

    private fun markNotTintable() {
        isTintable = false
        tintColor = Color.Unspecified
    }

    var clipPathData = EmptyPath
        set(value) {
            field = value
            isClipPathDirty = true
            invalidate()
        }

    private val willClipPath: Boolean
        get() = clipPathData.isNotEmpty()

    private var isClipPathDirty = true

    private var clipPath: Path? = null

    override var invalidateListener: ((VNode) -> Unit)? = null

    private val wrappedListener: (VNode) -> Unit = { node ->
        markTintForVNode(node)
        invalidateListener?.invoke(node)
    }

    private fun updateClipPath() {
        if (willClipPath) {
            var targetClip = clipPath
            if (targetClip == null) {
                targetClip = Path()
                clipPath = targetClip
            }

            // toPath() will reset the path we send
            clipPathData.toPath(targetClip)
        }
    }

    // If the name changes we should re-draw as individual nodes could
    // be modified based off of this name parameter.
    var name = DefaultGroupName
        set(value) {
            field = value
            invalidate()
        }

    var rotation = DefaultRotation
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var pivotX = DefaultPivotX
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var pivotY = DefaultPivotY
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var scaleX = DefaultScaleX
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var scaleY = DefaultScaleY
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var translationX = DefaultTranslationX
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var translationY = DefaultTranslationY
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    private val numChildren: Int
        get() = children.size

    private var isMatrixDirty = true

    private fun updateMatrix() {
        val matrix: Matrix
        val target = groupMatrix
        if (target == null) {
            matrix = Matrix()
            groupMatrix = matrix
        } else {
            matrix = target
            matrix.reset()
        }
        // M = T(translationX + pivotX, translationY + pivotY) *
        //     R(rotation) * S(scaleX, scaleY) *
        //     T(-pivotX, -pivotY)
        matrix.translate(translationX + pivotX, translationY + pivotY)
        matrix.rotateZ(degrees = rotation)
        matrix.scale(scaleX, scaleY, 1f)
        matrix.translate(-pivotX, -pivotY)
    }

    fun insertAt(
        index: Int,
        instance: VNode
    ) {
        if (index < numChildren) {
            children[index] = instance
        } else {
            children.add(instance)
        }

        markTintForVNode(instance)

        instance.invalidateListener = wrappedListener
        invalidate()
    }

    fun remove(
        index: Int,
        count: Int
    ) {
        repeat(count) {
            if (index < children.size) {
                children[index].invalidateListener = null
                children.removeAt(index)
            }
        }
        invalidate()
    }

    override fun DrawScope.draw() {
        if (isMatrixDirty) {
            updateMatrix()
            isMatrixDirty = false
        }

        if (isClipPathDirty) {
            updateClipPath()
            isClipPathDirty = false
        }

        withTransform({
            groupMatrix?.let { transform(it) }
            val targetClip = clipPath
            if (willClipPath && targetClip != null) {
                clipPath(targetClip)
            }
        }) {
            children.fastForEach { node ->
                with(node) {
                    this@draw.draw()
                }
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder().append("VGroup: ").append(name)
        children.fastForEach { node ->
            sb.append("\t").append(node.toString()).append("\n")
        }
        return sb.toString()
    }
}

/**
 * statically create a a GroupComponent from the VectorGroup representation provided from
 * an [ImageVector] instance
 */
internal fun GroupComponent.createGroupComponent(currentGroup: VectorGroup): GroupComponent {
    for (index in 0 until currentGroup.size) {
        val vectorNode = currentGroup[index]
        if (vectorNode is VectorPath) {
            val pathComponent = PathComponent().apply {
                pathData = vectorNode.pathData
                pathFillType = vectorNode.pathFillType
                name = vectorNode.name
                fill = vectorNode.fill
                fillAlpha = vectorNode.fillAlpha
                stroke = vectorNode.stroke
                strokeAlpha = vectorNode.strokeAlpha
                strokeLineWidth = vectorNode.strokeLineWidth
                strokeLineCap = vectorNode.strokeLineCap
                strokeLineJoin = vectorNode.strokeLineJoin
                strokeLineMiter = vectorNode.strokeLineMiter
                trimPathStart = vectorNode.trimPathStart
                trimPathEnd = vectorNode.trimPathEnd
                trimPathOffset = vectorNode.trimPathOffset
            }
            insertAt(index, pathComponent)
        } else if (vectorNode is VectorGroup) {
            val groupComponent = GroupComponent().apply {
                name = vectorNode.name
                rotation = vectorNode.rotation
                scaleX = vectorNode.scaleX
                scaleY = vectorNode.scaleY
                translationX = vectorNode.translationX
                translationY = vectorNode.translationY
                pivotX = vectorNode.pivotX
                pivotY = vectorNode.pivotY
                clipPathData = vectorNode.clipPathData
                createGroupComponent(vectorNode)
            }
            insertAt(index, groupComponent)
        }
    }
    return this
}

/**
 * helper method to verify if the rgb channels are equal excluding comparison of the alpha
 * channel
 */
private fun Color.rgbEqual(other: Color) =
    this.red == other.red &&
            this.green == other.green &&
            this.blue == other.blue