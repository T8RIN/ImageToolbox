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

package com.t8rin.image

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.Gravity
import android.widget.Magnifier
import androidx.annotation.RequiresApi
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.node.requireView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val MagnifierSize = 100.dp
private val MagnifierCornerRadius = 50.dp
private val MagnifierElevation = 2.dp

fun Modifier.imageMagnifier(
    sourceCenter: () -> Offset,
    dotColor: Color,
    dotSize: Dp,
    zoom: Float = Float.NaN
): Modifier = if (dotColor.isSpecified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    then(MagnifierElement(sourceCenter, dotColor, dotSize, zoom))
} else {
    magnifier(
        sourceCenter = { sourceCenter() },
        magnifierCenter = { sourceCenter() - Offset(0f, MagnifierSize.toPx()) },
        size = DpSize(MagnifierSize, MagnifierSize),
        zoom = zoom,
        cornerRadius = MagnifierCornerRadius,
        elevation = MagnifierElevation
    )
}

@SuppressLint("ModifierNodeInspectableProperties")
@RequiresApi(Build.VERSION_CODES.Q)
private data class MagnifierElement(
    val sourceCenter: () -> Offset,
    val dotColor: Color,
    val dotSize: Dp,
    val zoom: Float
) : ModifierNodeElement<MagnifierNode>() {
    override fun create() = MagnifierNode(this)

    override fun update(node: MagnifierNode) = node.update(this)
}

@RequiresApi(Build.VERSION_CODES.Q)
private class MagnifierNode(
    private var element: MagnifierElement
) : Modifier.Node(), GlobalPositionAwareModifierNode, ObserverModifierNode, DrawModifierNode {
    private var positionInRoot = Offset.Unspecified
    private var magnifier: Magnifier? = null
    private var isShowing = false
    private var updateJob: Job? = null

    fun update(element: MagnifierElement) {
        if (this.element.dotColor != element.dotColor || this.element.dotSize != element.dotSize) {
            dismissMagnifier()
            magnifier = null
        }
        this.element = element
        onObservedReadsChanged()
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        positionInRoot = coordinates.positionInRoot()
        onObservedReadsChanged()
    }

    override fun onObservedReadsChanged() = observeReads {
        val sourceCenter = element.sourceCenter()
        if (sourceCenter.isSpecified && positionInRoot.isSpecified) {
            val source = sourceCenter + positionInRoot
            val magnifier = magnifier ?: createMagnifier().also { magnifier = it }
            if (!element.zoom.isNaN() && magnifier.zoom != element.zoom) {
                magnifier.zoom = element.zoom
            }
            magnifier.show(source.x, source.y)
            isShowing = true
        } else {
            dismissMagnifier()
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (isShowing && updateJob?.isActive != true) {
            updateJob = coroutineScope.launch {
                // Refresh after the frame containing the updated canvas has been drawn.
                withFrameNanos {}
                if (isShowing) magnifier?.update()
            }
        }
    }

    override fun onDensityChange() {
        dismissMagnifier()
        magnifier = null
    }

    override fun onDetach() {
        dismissMagnifier()
        magnifier = null
        positionInRoot = Offset.Unspecified
    }

    private fun dismissMagnifier() {
        if (isShowing) magnifier?.dismiss()
        isShowing = false
    }

    private fun createMagnifier(): Magnifier = with(requireDensity()) {
        val dot = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(element.dotColor.toArgb())
        }
        val dotSize = element.dotSize.roundToPx()
        val overlay = LayerDrawable(arrayOf(dot)).apply {
            setLayerSize(0, dotSize, dotSize)
            setLayerGravity(0, Gravity.CENTER)
        }
        val size = MagnifierSize.roundToPx()
        Magnifier.Builder(requireView())
            .setSize(size, size)
            .setCornerRadius(MagnifierCornerRadius.toPx())
            .setElevation(MagnifierElevation.toPx())
            .setDefaultSourceToMagnifierOffset(0, -size)
            .setOverlay(overlay)
            .build()
    }
}
