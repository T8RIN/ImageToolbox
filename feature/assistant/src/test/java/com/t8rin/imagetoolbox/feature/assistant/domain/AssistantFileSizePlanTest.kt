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

package com.t8rin.imagetoolbox.feature.assistant.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantFileSizePlanTest {
    @Test
    fun convertsTheRequestedLimitWithoutModelArithmetic() {
        val amounts =
            AssistantFileSizePlan.amounts(AssistantRequest("Compress to 700 KB", language = "en"))
        val plan =
            requireNotNull(AssistantFileSizePlan.parse("""{"amount":700,"unit":"KB"}""", amounts))
        assertEquals(716800L, plan.bytes)
        assertTrue(137419L < plan.bytes)
        assertEquals("700 KB", plan.label)
    }

    @Test
    fun readsLocaleNumbersAndFractionalLimits() {
        assertEquals(
            listOf("1000"),
            AssistantFileSizePlan.amounts(AssistantRequest("Limit: 1,000 KB", language = "en"))
                .map { it.toPlainString() })
        val amounts =
            AssistantFileSizePlan.amounts(AssistantRequest("Лимит: 1,5 МБ", language = "ru"))
        assertEquals(listOf("1.5"), amounts.map { it.toPlainString() })
        assertEquals(
            1572864L,
            AssistantFileSizePlan.parse("""{"amount":1.5,"unit":"MB"}""", amounts)?.bytes
        )
    }

    @Test
    fun doesNotTreatPixelDimensionsAsFileBytesOrInventAQuantity() {
        val amounts = listOf(700.toBigDecimal())
        assertNull(AssistantFileSizePlan.parse("""{"amount":700,"unit":"pixels"}""", amounts))
        assertNull(AssistantFileSizePlan.parse("""{"amount":700,"unit":"unknown"}""", amounts))
        assertTrue(runCatching {
            AssistantFileSizePlan.parse(
                """{"amount":500,"unit":"KB"}""",
                amounts
            )
        }.isFailure)
    }

    @Test
    fun rejectsOverflowInsteadOfWrappingTheLimit() {
        val amount = "1000000000000000000000000000000".toBigDecimal()
        assertNull(
            AssistantFileSizePlan.parse(
                """{"amount":$amount,"unit":"GB"}""",
                listOf(amount)
            )
        )
    }
}
