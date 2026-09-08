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

package com.t8rin.imagetoolbox.feature.filters.data.utils.serialization

import com.t8rin.imagetoolbox.core.filters.domain.model.enums.ProceduralEffect
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiProceduralFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ProceduralParams
import com.t8rin.imagetoolbox.feature.filters.data.model.HueShiftFanFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.HelixWavesFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GridSineDistortion2Filter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProceduralSerializationTest {

    @Test
    fun everyProceduralEffectHasUsableUiParametersAndCanBeCopied() {
        for (effect in ProceduralEffect.entries) {
            val type = Class.forName(
                "com.t8rin.imagetoolbox.core.filters.presentation.model.Ui${effect.name}Filter"
            )
            val filter = type.getDeclaredConstructor().newInstance() as UiProceduralFilter
            assertEquals(effect, filter.effect)
            assertEquals(effect.parameters.size, filter.paramsInfo.size)
            assertTrue(filter.paramsInfo.all { it.title != null })
            assertEquals(type, filter.newInstance().javaClass)
            val params = ProceduralParams(effect.parameters.associate { it.name to it.range.start })
            assertEquals(params, filter.copy(params).value)
        }
    }

    @Test
    fun templateRetainsEffectIdentityAndParameters() {
        val filters = listOf(
            HelixWavesFilter(ProceduralParams(mapOf("frequency" to 1.7f, "mode" to 2f))),
            GridSineDistortion2Filter(ProceduralParams(mapOf("intensity" to -0.75f))),
            HelixWavesFilter(),
            HueShiftFanFilter(
                ProceduralParams(
                    values = mapOf("intensity" to 0.6f)
                )
            )
        )
        val restored = filters.toDatastoreString(true).toFiltersList(true)
        assertEquals(filters.map { it::class }, restored.map { it::class })
        assertEquals(filters.map { it.value }, restored.map { it.value })
    }

    @Test
    fun partialAndNonFiniteParametersResolveToSafeValues() {
        for (effect in ProceduralEffect.entries) {
            val invalid = ProceduralParams(effect.parameters.associate { it.name to Float.NaN })
            val resolved = effect.resolve(invalid)
            assertEquals(effect.resolve(ProceduralParams()), resolved)
            val tooLarge = effect.resolve(
                ProceduralParams(effect.parameters.associate { it.name to Float.MAX_VALUE })
            )
            effect.parameters.forEach { param ->
                assertTrue(tooLarge.getValue(param.name) in param.range)
            }
        }
    }
}