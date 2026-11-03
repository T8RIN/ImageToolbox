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


package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.ErrorOwner
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.FilterFactory
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.StoredParameterValues
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.fromPair
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.pixelationKey
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.readOldParams
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.storedParameters
import kotlin.reflect.KClass

internal class FilterRegistration<F>(
    val type: KClass<out Filter<*>>,
    val implementation: KClass<F>,
    val transformationKey: String,
    val storedKey: String? = null,
    val paramsKey: String? = null,
    val encodeParams: ((Any) -> String?)? = null,
    val cacheParams: ((Any) -> String?)? = null,
    val decodeParams: ((String) -> Filter.ParameterValue)? = null,
    val readStoredParams: ((Any?) -> Filter.ParameterValue?)? = null,
    val normalizeParams: ((Any) -> Filter.ParameterValue?)? = null,
    val createTransformation: (Any?, ErrorOwner?) -> F?
) where F : Filter<*>, F : FilterTransformation

internal object GeneratedFilterRegistry {
    private val registrations = filterRegistrations()
    private val byStoredKey =
        registrations.mapNotNull { entry -> entry.storedKey?.let { it to entry } }.toMap()
    private val byTransformationKey = registrations.associateBy { it.transformationKey }
    private val byImplementation = registrations.associateBy { it.implementation }

    fun restoreStored(
        key: String,
        params: Pair<String, String>?,
        factory: FilterFactory
    ): Filter<*>? {
        val entry = storedRegistration(key) ?: byTransformationKey[key] ?: return null
        val value = when {
            params == null -> null
            params == ("null" to "") && entry.type.storedParameters()?.type == "null" -> null
            entry.paramsKey == null -> {
                require(params == ("Unit" to "Unit")) { "Invalid empty filter parameters for $key" }
                null
            }

            else -> requireNotNull(entry.decodeStoredParams(params)) {
                "Cannot restore parameters for $key"
            }
        }
        return factory.restore(key, value) ?: entry.createTransformation(value, null)
    }

    private fun storedRegistration(key: String): FilterRegistration<*>? =
        byStoredKey[key] ?: key.pixelationKey()?.let(byStoredKey::get)

    fun transformationKey(filter: Filter<*>): String? =
        byImplementation[filter::class]?.transformationKey

    fun encodeParams(filter: Filter<*>): Pair<String, String>? {
        if (filter.value is Filter.EmptyValue) return "Unit" to "Unit"
        val entry = registrationFor(filter) ?: return null
        val key = entry.paramsKey ?: return null
        val payload = entry.encodeParams?.invoke(filter.value) ?: return null
        return key to payload
    }

    fun cacheKey(filter: FilterTransformation): String {
        val entry = byImplementation[filter::class]
        val type = entry?.transformationKey ?: filter.javaClass.name
        val key = if (filter.value is Filter.EmptyValue) type else {
            val parameters = requireNotNull(entry?.cacheParams?.invoke(filter.value)) {
                "Missing cache parameters for $type"
            }
            "$type:$parameters"
        }
        val extra = filter.cacheKeyExtra
        return if (extra == null) key else "$key:${filterCacheKey(extra)}"
    }

    fun transform(filter: Filter<*>): Transformation<Bitmap> =
        requireNotNull(
            registrationFor(filter)?.createTransformation?.invoke(
                filter.value,
                filter
            )
        ) {
            "No transformation for ${filter::class.qualifiedName}"
        }

    private fun FilterRegistration<*>.decodeStoredParams(params: Pair<String, String>): Filter.ParameterValue? {
        val key = params.first.trim()
        val payload = params.second.trim()
        return when {
            StoredParameterValues.isNamed(payload) -> {
                require(key == paramsKey) { "Unexpected filter parameter type $key" }
                decodeParams?.invoke(payload)
            }

            key == type.storedParameters()?.type -> {
                type.readOldParams(key, payload)?.let { normalizeParams?.invoke(it) }
                    ?: if (key.endsWith("Params") && key != "GmicFilterParams") {
                        decodeParams?.invoke(payload)
                    } else readStoredParams?.invoke((key to payload).fromPair())
            }

            else -> null
        }
    }

    private fun registrationFor(filter: Filter<*>): FilterRegistration<*>? =
        byImplementation[filter::class] ?: registrations.firstOrNull { it.type.isInstance(filter) }
}
