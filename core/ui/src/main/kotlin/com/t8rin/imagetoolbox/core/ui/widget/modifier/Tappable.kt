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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalFocusManager

fun Modifier.clearFocusOnTap(
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this

    return this.then(ClearFocusOnTapElement)
}

private data object ClearFocusOnTapElement : ModifierNodeElement<ClearFocusOnTapNode>() {

    override fun create(): ClearFocusOnTapNode {
        return ClearFocusOnTapNode()
    }

    override fun update(node: ClearFocusOnTapNode) {
        node.resetPointerInput()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "clearFocusOnTap"
        properties["enabled"] = true
    }
}

private class ClearFocusOnTapNode : DelegatingNode(),
    CompositionLocalConsumerModifierNode {

    private val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTapGestures(
                onTap = {
                    currentValueOf(LocalFocusManager).clearFocus()
                }
            )
        }
    )

    fun resetPointerInput() {
        pointerInputNode.resetPointerInputHandler()
    }
}

fun Modifier.tappable(
    key1: Any? = Unit,
    onTap: PointerInputScope.(Offset) -> Unit
): Modifier = pointerInput(key1) {
    detectTapGestures { onTap(it) }
}

@Composable
fun Disableable(
    enabled: Boolean,
    onDisabledClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        content()
        if (!enabled) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .matchParentSize()
                    .tappable(onDisabledClick) {
                        onDisabledClick()
                    }
            ) {}
        }
    }
}