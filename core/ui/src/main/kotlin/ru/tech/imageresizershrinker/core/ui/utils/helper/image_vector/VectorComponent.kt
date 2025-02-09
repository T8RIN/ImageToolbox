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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.DefaultGroupName
import androidx.compose.ui.unit.IntSize
import kotlin.math.ceil

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

    private var previousDrawSize = Size.Unspecified

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

    /**
     * Helper method to determine if a particular ColorFilter will generate the same output
     * if the bitmap has an Alpha8 or ARGB8888 configuration
     */
    private fun ColorFilter?.tintableWithAlphaMask() = if (this is BlendModeColorFilter) {
        this.blendMode == BlendMode.SrcIn || this.blendMode == BlendMode.SrcOver
    } else {
        this == null
    }
}