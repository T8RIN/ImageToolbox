/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.content.Context
import android.graphics.PorterDuff
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Size.Companion.Unspecified
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.graphics.vector.DefaultGroupName
import androidx.compose.ui.graphics.vector.DefaultPathName
import androidx.compose.ui.graphics.vector.DefaultPivotX
import androidx.compose.ui.graphics.vector.DefaultPivotY
import androidx.compose.ui.graphics.vector.DefaultRotation
import androidx.compose.ui.graphics.vector.DefaultScaleX
import androidx.compose.ui.graphics.vector.DefaultScaleY
import androidx.compose.ui.graphics.vector.DefaultStrokeLineCap
import androidx.compose.ui.graphics.vector.DefaultStrokeLineJoin
import androidx.compose.ui.graphics.vector.DefaultStrokeLineMiter
import androidx.compose.ui.graphics.vector.DefaultStrokeLineWidth
import androidx.compose.ui.graphics.vector.DefaultTranslationX
import androidx.compose.ui.graphics.vector.DefaultTranslationY
import androidx.compose.ui.graphics.vector.DefaultTrimPathEnd
import androidx.compose.ui.graphics.vector.DefaultTrimPathOffset
import androidx.compose.ui.graphics.vector.DefaultTrimPathStart
import androidx.compose.ui.graphics.vector.EmptyPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RootGroupName
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.graphics.vector.toPath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastForEach
import kotlin.math.ceil

/**
 * [Painter] implementation that abstracts the drawing of a Vector graphic.
 * This can be represented by either a [ImageVector] or a programmatic
 * composition of a vector
 */
internal class VectorPainter internal constructor(root: GroupComponent = GroupComponent()) :
    Painter() {

    internal var size by mutableStateOf(Size.Zero)

    internal var autoMirror by mutableStateOf(false)

    /**
     * configures the intrinsic tint that may be defined on a VectorPainter
     */
    internal var intrinsicColorFilter: ColorFilter?
        get() = vector.intrinsicColorFilter
        set(value) {
            vector.intrinsicColorFilter = value
        }

    internal var viewportSize: Size
        get() = vector.viewportSize
        set(value) {
            vector.viewportSize = value
        }

    internal var name: String
        get() = vector.name
        set(value) {
            vector.name = value
        }

    internal val vector = VectorComponent(root).apply {
        invalidateCallback = {
            if (drawCount == invalidateCount) {
                invalidateCount++
            }
        }
    }

    private var invalidateCount by mutableIntStateOf(0)

    private var currentAlpha: Float = 1.0f
    private var currentColorFilter: ColorFilter? = null

    override val intrinsicSize: Size
        get() = size

    private var drawCount = -1

    override fun DrawScope.onDraw() {
        with(vector) {
            val filter = currentColorFilter ?: intrinsicColorFilter
            if (autoMirror && layoutDirection == LayoutDirection.Rtl) {
                mirror {
                    draw(currentAlpha, filter)
                }
            } else {
                draw(currentAlpha, filter)
            }
        }
        // This assignment is necessary to obtain invalidation callbacks as the state is
        // being read here which adds this callback to the snapshot observation
        drawCount = invalidateCount
    }

    override fun applyAlpha(alpha: Float): Boolean {
        currentAlpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        currentColorFilter = colorFilter
        return true
    }
}

private inline fun DrawScope.mirror(block: DrawScope.() -> Unit) {
    scale(-1f, 1f, block = block)
}

private fun Density.obtainSizePx(
    defaultWidth: Dp,
    defaultHeight: Dp
) =
    Size(defaultWidth.toPx(), defaultHeight.toPx())

/**
 * Helper method to calculate the viewport size. If the viewport width/height are not specified
 * this falls back on the default size provided
 */
private fun obtainViewportSize(
    defaultSize: Size,
    viewportWidth: Float,
    viewportHeight: Float
) = Size(
    if (viewportWidth.isNaN()) defaultSize.width else viewportWidth,
    if (viewportHeight.isNaN()) defaultSize.height else viewportHeight
)

/**
 * Helper method to conditionally create a ColorFilter to tint contents if [tintColor] is
 * specified, that is [Color.isSpecified] returns true
 */
private fun createColorFilter(
    tintColor: Color,
    tintBlendMode: BlendMode
): ColorFilter? =
    if (tintColor.isSpecified) {
        ColorFilter.tint(tintColor, tintBlendMode)
    } else {
        null
    }

/**
 * Helper method to configure the properties of a VectorPainter that maybe re-used
 */
internal fun VectorPainter.configureVectorPainter(
    defaultSize: Size,
    viewportSize: Size,
    name: String = RootGroupName,
    intrinsicColorFilter: ColorFilter?,
    autoMirror: Boolean = false,
): VectorPainter = apply {
    this.size = defaultSize
    this.autoMirror = autoMirror
    this.intrinsicColorFilter = intrinsicColorFilter
    this.viewportSize = viewportSize
    this.name = name
}

/**
 * Helper method to create a VectorPainter instance from an ImageVector
 */
internal fun createVectorPainterFromImageVector(
    density: Density,
    imageVector: ImageVector,
    root: GroupComponent
): VectorPainter {
    val defaultSize = density.obtainSizePx(imageVector.defaultWidth, imageVector.defaultHeight)
    val viewport = obtainViewportSize(
        defaultSize,
        imageVector.viewportWidth,
        imageVector.viewportHeight
    )
    return VectorPainter(root).configureVectorPainter(
        defaultSize = defaultSize,
        viewportSize = viewport,
        name = imageVector.name,
        intrinsicColorFilter = createColorFilter(imageVector.tintColor, imageVector.tintBlendMode),
        autoMirror = imageVector.autoMirror
    )
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

internal sealed class VNode {
    /**
     * Callback invoked whenever the node in the vector tree is modified in a way that would
     * change the output of the Vector
     */
    internal open var invalidateListener: ((VNode) -> Unit)? = null

    fun invalidate() {
        invalidateListener?.invoke(this)
    }

    abstract fun DrawScope.draw()
}

internal class VectorComponent(val root: GroupComponent) : VNode() {

    init {
        root.invalidateListener = {
            doInvalidate()
        }
    }

    var name: String = DefaultGroupName

    private fun doInvalidate() {
        isDirty = true
        invalidateCallback.invoke()
    }

    private var isDirty = true

    private val cacheDrawScope = DrawCache()

    private val cacheBitmapConfig: ImageBitmapConfig
        get() = cacheDrawScope.mCachedImage?.config ?: ImageBitmapConfig.Argb8888

    internal var invalidateCallback = {}

    internal var intrinsicColorFilter: ColorFilter? by mutableStateOf(null)

    // Conditional filter used if the vector is all one color. In this case we allocate a
    // alpha8 channel bitmap and tint the result to the desired color
    private var tintFilter: ColorFilter? = null

    internal var viewportSize by mutableStateOf(Size.Zero)

    private var previousDrawSize = Unspecified

    private var rootScaleX = 1f
    private var rootScaleY = 1f

    /**
     * Cached lambda used to avoid allocating the lambda on each draw invocation
     */
    private val drawVectorBlock: DrawScope.() -> Unit = {
        with(root) {
            scale(rootScaleX, rootScaleY, pivot = Offset.Zero) {
                draw()
            }
        }
    }

    fun DrawScope.draw(
        alpha: Float,
        colorFilter: ColorFilter?
    ) {
        // If the content of the vector has changed, or we are drawing a different size
        // update the cached image to ensure we are scaling the vector appropriately
        val isOneColor = root.isTintable && root.tintColor.isSpecified
        val targetImageConfig = if (isOneColor && intrinsicColorFilter.tintableWithAlphaMask() &&
            colorFilter.tintableWithAlphaMask()
        ) {
            ImageBitmapConfig.Alpha8
        } else {
            ImageBitmapConfig.Argb8888
        }

        if (isDirty || previousDrawSize != size || targetImageConfig != cacheBitmapConfig) {
            tintFilter = if (targetImageConfig == ImageBitmapConfig.Alpha8) {
                ColorFilter.tint(root.tintColor)
            } else {
                null
            }
            rootScaleX = size.width / viewportSize.width
            rootScaleY = size.height / viewportSize.height
            cacheDrawScope.drawCachedImage(
                targetImageConfig,
                IntSize(ceil(size.width).toInt(), ceil(size.height).toInt()),
                this@draw,
                layoutDirection,
                drawVectorBlock
            )
            isDirty = false
            previousDrawSize = size
        }
        val targetFilter = colorFilter
            ?: if (intrinsicColorFilter != null) {
                intrinsicColorFilter
            } else {
                tintFilter
            }
        cacheDrawScope.drawInto(this, alpha, targetFilter)
    }

    override fun DrawScope.draw() {
        draw(1.0f, null)
    }

    override fun toString(): String {
        return buildString {
            append("Params: ")
            append("\tname: ").append(name).append("\n")
            append("\tviewportWidth: ").append(viewportSize.width).append("\n")
            append("\tviewportHeight: ").append(viewportSize.height).append("\n")
        }
    }
}

internal class PathComponent : VNode() {
    var name = DefaultPathName
        set(value) {
            field = value
            invalidate()
        }

    var fill: Brush? = null
        set(value) {
            field = value
            invalidate()
        }

    var fillAlpha = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    var pathData = EmptyPath
        set(value) {
            field = value
            isPathDirty = true
            invalidate()
        }

    var pathFillType = DefaultFillType
        set(value) {
            field = value
            renderPath.fillType = value
            invalidate()
        }

    var strokeAlpha = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    var strokeLineWidth = DefaultStrokeLineWidth
        set(value) {
            field = value
            isStrokeDirty = true
            invalidate()
        }

    var stroke: Brush? = null
        set(value) {
            field = value
            invalidate()
        }

    var strokeLineCap = DefaultStrokeLineCap
        set(value) {
            field = value
            isStrokeDirty = true
            invalidate()
        }

    var strokeLineJoin = DefaultStrokeLineJoin
        set(value) {
            field = value
            isStrokeDirty = true
            invalidate()
        }

    var strokeLineMiter = DefaultStrokeLineMiter
        set(value) {
            field = value
            isStrokeDirty = true
            invalidate()
        }

    var trimPathStart = DefaultTrimPathStart
        set(value) {
            field = value
            isTrimPathDirty = true
            invalidate()
        }

    var trimPathEnd = DefaultTrimPathEnd
        set(value) {
            field = value
            isTrimPathDirty = true
            invalidate()
        }

    var trimPathOffset = DefaultTrimPathOffset
        set(value) {
            field = value
            isTrimPathDirty = true
            invalidate()
        }

    private var isPathDirty = true
    private var isStrokeDirty = true
    private var isTrimPathDirty = false

    private var strokeStyle: Stroke? = null

    private val path = Path()
    private var renderPath = path

    private val pathMeasure: PathMeasure by lazy(LazyThreadSafetyMode.NONE) { PathMeasure() }

    private fun updatePath() {
        // The call below resets the path
        pathData.toPath(path)
        updateRenderPath()
    }

    private fun updateRenderPath() {
        if (trimPathStart == DefaultTrimPathStart && trimPathEnd == DefaultTrimPathEnd) {
            renderPath = path
        } else {
            if (renderPath == path) {
                renderPath = Path()
            } else {
                // Rewind unsets the fill type so reset it here
                val fillType = renderPath.fillType
                renderPath.rewind()
                renderPath.fillType = fillType
            }

            pathMeasure.setPath(path, false)
            val length = pathMeasure.length
            val start = ((trimPathStart + trimPathOffset) % 1f) * length
            val end = ((trimPathEnd + trimPathOffset) % 1f) * length
            if (start > end) {
                pathMeasure.getSegment(start, length, renderPath, true)
                pathMeasure.getSegment(0f, end, renderPath, true)
            } else {
                pathMeasure.getSegment(start, end, renderPath, true)
            }
        }
    }

    override fun DrawScope.draw() {
        if (isPathDirty) {
            updatePath()
        } else if (isTrimPathDirty) {
            updateRenderPath()
        }
        isPathDirty = false
        isTrimPathDirty = false

        fill?.let { drawPath(renderPath, brush = it, alpha = fillAlpha) }
        stroke?.let {
            var targetStroke = strokeStyle
            if (isStrokeDirty || targetStroke == null) {
                targetStroke =
                    Stroke(strokeLineWidth, strokeLineMiter, strokeLineCap, strokeLineJoin)
                strokeStyle = targetStroke
                isStrokeDirty = false
            }
            drawPath(renderPath, brush = it, alpha = strokeAlpha, style = targetStroke)
        }
    }

    override fun toString() = path.toString()
}

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
 * helper method to verify if the rgb channels are equal excluding comparison of the alpha
 * channel
 */
internal fun Color.rgbEqual(other: Color) =
    this.red == other.red &&
            this.green == other.green &&
            this.blue == other.blue

/**
 * Helper method to determine if a particular ColorFilter will generate the same output
 * if the bitmap has an Alpha8 or ARGB8888 configuration
 */
internal fun ColorFilter?.tintableWithAlphaMask() = if (this is BlendModeColorFilter) {
    this.blendMode == BlendMode.SrcIn || this.blendMode == BlendMode.SrcOver
} else {
    this == null
}

/**
 * Creates a drawing environment that directs its drawing commands to an [ImageBitmap]
 * which can be drawn directly in another [DrawScope] instance. This is useful to cache
 * complicated drawing commands across frames especially if the content has not changed.
 * Additionally some drawing operations such as rendering paths are done purely in
 * software so it is beneficial to cache the result and render the contents
 * directly through a texture as done by [DrawScope.drawImage]
 */
internal class DrawCache {

    @PublishedApi
    internal var mCachedImage: ImageBitmap? = null
    private var cachedCanvas: Canvas? = null
    private var scopeDensity: Density? = null
    private var layoutDirection: LayoutDirection = LayoutDirection.Ltr
    private var size: IntSize = IntSize.Zero
    private var config: ImageBitmapConfig = ImageBitmapConfig.Argb8888

    private val cacheScope = CanvasDrawScope()

    /**
     * Draw the contents of the lambda with receiver scope into an [ImageBitmap] with the provided
     * size. If the same size is provided across calls, the same [ImageBitmap] instance is
     * re-used and the contents are cleared out before drawing content in it again
     */
    fun drawCachedImage(
        config: ImageBitmapConfig,
        size: IntSize,
        density: Density,
        layoutDirection: LayoutDirection,
        block: DrawScope.() -> Unit
    ) {
        this.scopeDensity = density
        this.layoutDirection = layoutDirection
        var targetImage = mCachedImage
        var targetCanvas = cachedCanvas
        if (targetImage == null ||
            targetCanvas == null ||
            size.width > targetImage.width ||
            size.height > targetImage.height ||
            this.config != config
        ) {
            targetImage = ImageBitmap(size.width, size.height, config = config)
            targetCanvas = Canvas(targetImage)

            mCachedImage = targetImage
            cachedCanvas = targetCanvas
            this.config = config
        }
        this.size = size
        cacheScope.draw(density, layoutDirection, targetCanvas, size.toSize()) {
            clear()
            block()
        }
        targetImage.prepareToDraw()
    }

    /**
     * Draw the cached content into the provided [DrawScope] instance
     */
    fun drawInto(
        target: DrawScope,
        alpha: Float = 1.0f,
        colorFilter: ColorFilter? = null
    ) {
        val targetImage = mCachedImage
        require(targetImage != null) {
            "drawCachedImage must be invoked first before attempting to draw the result " +
                    "into another destination"
        }
        target.drawImage(targetImage, srcSize = size, alpha = alpha, colorFilter = colorFilter)
    }

    /**
     * Helper method to clear contents of the draw environment from the given bounds of the
     * DrawScope
     */
    private fun DrawScope.clear() {
        drawRect(color = Color.Black, blendMode = BlendMode.Clear)
    }
}

fun ImageVector.toImageBitmap(
    context: Context,
    width: Int,
    height: Int,
    tint: Color = Color.Unspecified,
    backgroundColor: Color = Color.Transparent,
    iconPadding: Int = 0
): ImageBitmap {
    val imageBitmap = ImageBitmap(width, height)
    val density = object : Density {
        override val density: Float
            get() = context.resources.displayMetrics.density
        override val fontScale: Float
            get() = context.resources.configuration.fontScale
    }
    val painter = createVectorPainterFromImageVector(
        density = density,
        imageVector = this,
        root = GroupComponent().apply {
            createGroupComponent(root)
        }
    )
    val canvas = Canvas(imageBitmap).apply {
        with(nativeCanvas) {
            drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
            drawColor(backgroundColor.toArgb())
        }
    }

    CanvasDrawScope().draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        translate(iconPadding.toFloat(), iconPadding.toFloat()) {
            with(painter) {
                draw(
                    size = Size(
                        width = (width - iconPadding * 2).toFloat(),
                        height = (height - iconPadding * 2).toFloat()
                    ),
                    colorFilter = ColorFilter.tint(tint)
                )
            }
        }
    }

    return imageBitmap
}