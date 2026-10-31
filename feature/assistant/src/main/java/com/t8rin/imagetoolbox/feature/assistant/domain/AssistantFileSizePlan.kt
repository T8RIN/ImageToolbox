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

import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale

internal data class AssistantFileSizePlan(val amount: BigDecimal, val unit: Unit) {
    enum class Unit(val bytes: Long) {
        B(1), KB(1024), MB(1024 * 1024), GB(1024L * 1024 * 1024)
    }

    val bytes: Long = amount.multiply(BigDecimal.valueOf(unit.bytes))
        .setScale(0, RoundingMode.FLOOR).longValueExact().also { require(it > 0) }
    val label: String get() = "${amount.stripTrailingZeros().toPlainString()} ${unit.name}"

    companion object {
        private val number = Regex("\\p{Nd}+(?:[., \\u00a0\\u202f]\\p{Nd}+)*(?:[eE][+-]?\\p{Nd}+)?")

        fun amounts(request: AssistantRequest): List<BigDecimal> {
            val format =
                NumberFormat.getNumberInstance(Locale.forLanguageTag(request.language.orEmpty()))
            if (format is DecimalFormat) format.isParseBigDecimal = true
            return number.findAll(request.query).mapNotNull { match ->
                val raw = match.value.filterNot(Char::isWhitespace)
                val position = ParsePosition(0)
                val parsed = format.parse(raw, position)
                val amount =
                    if (position.index == raw.length) parsed?.toString()?.toBigDecimalOrNull()
                    else raw.replace(',', '.').toBigDecimalOrNull()
                amount?.takeIf { it.signum() > 0 }
            }.distinct().take(12).toList()
        }

        fun grammar(amounts: List<BigDecimal>): String {
            require(amounts.isNotEmpty())
            val values = amounts.joinToString(",") { it.toPlainString() }
            return """start: %json {"type":"object","properties":{"amount":{"enum":[$values]},"unit":{"enum":["B","KB","MB","GB","pixels","unknown"]}},"required":["amount","unit"],"additionalProperties":false}"""
        }

        fun prompt(request: AssistantRequest, model: AssistantModel): String = assistantPrompt(
            grounding = "Extract the requested maximum FILE SIZE. Return the numeric amount and its original unit as B, KB, MB or GB. KB uses 1024 bytes here. Do not multiply. Use pixels for pixel dimensions and unknown if no file-size unit is specified. Example: Request: Limit the file to 250 KB. JSON: {\"amount\":250,\"unit\":\"KB\"}. Example: Request: Make it 800 pixels wide. JSON: {\"amount\":800,\"unit\":\"pixels\"}. The app documentation suggests a file-size compression tool; check the actual request.",
            history = emptyList(),
            question = request.query,
            hasImage = false,
            model = model
        )

        fun parse(text: String, amounts: List<BigDecimal>): AssistantFileSizePlan? {
            require(text.length <= 1000)
            val value = Json.parseToJsonElement(text).jsonObject
            require(value.keys == setOf("amount", "unit"))
            val unit =
                Unit.entries.firstOrNull { it.name == value.getValue("unit").jsonPrimitive.content }
                    ?: return null
            val amount = value.getValue("amount").jsonPrimitive.content.toBigDecimal()
            require(amounts.any { it.compareTo(amount) == 0 })
            return try {
                AssistantFileSizePlan(amount, unit)
            } catch (_: ArithmeticException) {
                null
            }
        }
    }
}
