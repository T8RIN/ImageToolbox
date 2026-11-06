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

package com.t8rin.collages

import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.utils.CollageLayoutFactory.createCollageLayouts

@ConsistentCopyVisibility
data class CollageType internal constructor(
    internal val layout: CollageLayout?
) {
    val layoutId: String?
        get() = layout?.title

    val imageCount: Int
        get() = layout?.photoItemList?.size ?: 0

    companion object {
        val Empty by lazy { CollageType(layout = null) }

        fun fromLayoutId(
            layoutId: String,
            layoutParams: List<Float>? = null
        ): CollageType? {
            val layout = createCollageLayouts(layoutId) ?: return null
            if (layoutParams != null) {
                val manager = layout.paramsManager
                if (layoutParams.size != (manager?.snapshotValues()?.size ?: 0) ||
                    layoutParams.any { !it.isFinite() }
                ) return null

                if (runCatching { manager?.restoreValues(layoutParams) }.isFailure) return null
            }
            return CollageType(layout)
        }
    }
}
