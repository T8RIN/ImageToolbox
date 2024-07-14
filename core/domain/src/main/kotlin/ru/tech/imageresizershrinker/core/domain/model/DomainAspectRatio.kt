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

package ru.tech.imageresizershrinker.core.domain.model

sealed class DomainAspectRatio(
    open val widthProportion: Float,
    open val heightProportion: Float
) {
    val value: Float get() = widthProportion / heightProportion

    data class Numeric(
        override val widthProportion: Float,
        override val heightProportion: Float
    ) : DomainAspectRatio(widthProportion = widthProportion, heightProportion = heightProportion)

    data object Free : DomainAspectRatio(widthProportion = -2f, heightProportion = 1f)
    data object Original : DomainAspectRatio(widthProportion = -1f, heightProportion = 1f)

    data class Custom(
        override val widthProportion: Float = 1f,
        override val heightProportion: Float = 1f
    ) : DomainAspectRatio(widthProportion = widthProportion, heightProportion = heightProportion)

    companion object {
        val defaultList: List<DomainAspectRatio> by lazy {
            listOf(
                Free,
                Original,
                Custom(),
                Numeric(widthProportion = 1f, heightProportion = 1f),
                Numeric(widthProportion = 10f, heightProportion = 16f),
                Numeric(widthProportion = 9f, heightProportion = 16f),
                Numeric(widthProportion = 9f, heightProportion = 18.5f),
                Numeric(widthProportion = 9f, heightProportion = 20f),
                Numeric(widthProportion = 9f, heightProportion = 21f),
                Numeric(widthProportion = 1f, heightProportion = 1.91f),
                Numeric(widthProportion = 2f, heightProportion = 3f),
                Numeric(widthProportion = 1f, heightProportion = 2f),
                Numeric(widthProportion = 5f, heightProportion = 3f),
                Numeric(widthProportion = 5f, heightProportion = 4f),
                Numeric(widthProportion = 4f, heightProportion = 3f),
                Numeric(widthProportion = 21f, heightProportion = 9f),
                Numeric(widthProportion = 20f, heightProportion = 9f),
                Numeric(widthProportion = 18.5f, heightProportion = 9f),
                Numeric(widthProportion = 16f, heightProportion = 9f),
                Numeric(widthProportion = 16f, heightProportion = 10f),
                Numeric(widthProportion = 1.91f, heightProportion = 1f),
                Numeric(widthProportion = 3f, heightProportion = 2f),
                Numeric(widthProportion = 3f, heightProportion = 4f),
                Numeric(widthProportion = 4f, heightProportion = 5f),
                Numeric(widthProportion = 3f, heightProportion = 5f),
                Numeric(widthProportion = 2f, heightProportion = 1f)
            )
        }

        fun fromString(value: String): DomainAspectRatio? = when {
            value == Free::class.simpleName -> Free
            value == Original::class.simpleName -> Original
            value.contains(Custom::class.simpleName!!) -> {
                val (w, h) = value.split("|")[1].split(":").map { it.toFloat() }
                Custom(w, h)
            }

            value.contains(Numeric::class.simpleName!!) -> {
                val (w, h) = value.split("|")[1].split(":").map { it.toFloat() }
                Numeric(w, h)
            }

            else -> null
        }
    }

    fun asString(): String = when {
        this is Custom || this is Numeric -> "${this::class.simpleName}|${widthProportion}:${heightProportion}"
        else -> this::class.simpleName!!
    }
}