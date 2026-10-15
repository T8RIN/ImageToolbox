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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import android.graphics.Path as NativePath

internal fun List<UiPathPaint>.combineSpotHealPaths(
    mode: DrawMode.SpotHeal
): UiPathPaint? {
    val first = firstOrNull() ?: return null
    val mask = NativePath()
    forEach { entry ->
        val outline = NativePath()
        pathEffectPaint(
            entry.strokeWidth,
            if (entry.isErasing) DrawPathMode.Free else entry.drawPathMode,
            first.canvasSize
        ).getFillPath(
            entry.path.scaleToFitCanvas(first.canvasSize, entry.canvasSize).asAndroidPath(),
            outline
        )
        mask.op(outline, if (entry.isErasing) NativePath.Op.DIFFERENCE else NativePath.Op.UNION)
    }
    if (mask.isEmpty) return null
    return first.copy(
        path = mask.asComposePath(),
        isErasing = false,
        drawMode = mode,
        drawPathMode = DrawPathMode.Lasso,
        isPendingSpotHeal = false
    )
}

fun List<UiPathPaint>.applySpotHeal(mode: DrawMode): List<UiPathPaint> {
    val pending = filter { it.isPendingSpotHeal }
    if (pending.isEmpty()) return this
    val healMode = mode as? DrawMode.SpotHeal
        ?: pending.lastOrNull()?.drawMode as? DrawMode.SpotHeal
        ?: return this
    val combined = pending.combineSpotHealPaths(healMode)
    return filterNot { it.isPendingSpotHeal } + listOfNotNull(combined)
}