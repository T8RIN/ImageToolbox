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

package com.t8rin.imagetoolbox.core.ui.utils.animation

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

@Stable
@Immutable
class CombinedMutableInteractionSource(
    private val sources: List<MutableInteractionSource>
) : MutableInteractionSource {

    constructor(vararg sources: MutableInteractionSource) : this(sources.toList())

    override val interactions: Flow<Interaction> =
        merge(*sources.map { it.interactions }.toTypedArray())

    override suspend fun emit(interaction: Interaction) {
        sources.forEach { it.emit(interaction) }
    }

    override fun tryEmit(interaction: Interaction): Boolean {
        return sources.all { it.tryEmit(interaction) }
    }

}