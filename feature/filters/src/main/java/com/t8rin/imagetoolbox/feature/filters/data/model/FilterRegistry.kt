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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.FilterCatalog
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.acceptsOldParamsKey
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.fromPair
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.parseShaderTemplate
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.pixelationKey
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.readOldParams
import kotlin.reflect.KClass

internal class FilterRegistration<F>(
    val type: KClass<out Filter<*>>,
    val implementation: KClass<F>,
    val transformationKey: String,
    val group: String? = null,
    val uiKey: String? = null,
    val createUi: ((Any?) -> UiFilter<*>)? = null,
    val paramsKey: String? = null,
    val encodeParams: ((Any) -> String?)? = null,
    val decodeParams: ((String) -> Filter.ParameterValue)? = null,
    val readStoredParams: ((Any?) -> Filter.ParameterValue?)? = null,
    val normalizeParams: ((Any) -> Filter.ParameterValue?)? = null,
    val decodeShaderTemplate: ((Map<String, String>) -> Filter.ParameterValue)? = null,
    val createTransformation: (Any?, ErrorOwner?) -> F?
) where F : Filter<*>, F : FilterTransformation

internal object GeneratedFilterCatalog : FilterCatalog {
    private val registrations = filterRegistrations()
    private val byGroup = registrations.filter { it.group != null }.groupBy { it.group }
    private val byUiKey =
        registrations.mapNotNull { entry -> entry.uiKey?.let { it to entry } }.toMap()
    private val byTransformationKey = registrations.associateBy { it.transformationKey }
    private val byImplementation = registrations.associateBy { it.implementation }
    private val byType = registrations.associateBy { it.type }

    override fun filters(group: String): List<UiFilter<*>> =
        byGroup[group].orEmpty().mapNotNull { it.createUi?.invoke(null) }

    override fun restore(key: String, value: Any?): UiFilter<*>? =
        uiRegistration(key)?.createUi?.invoke(value)

    fun restoreStored(key: String, params: Pair<String, String>?): Filter<*>? {
        val ui = uiRegistration(key)
        val entry = ui ?: byTransformationKey[key] ?: return null
        val value = params?.let { runCatching { entry.decodeStoredParams(it) }.getOrNull() }
        return if (ui != null) entry.createUi?.invoke(value) else entry.createTransformation(
            value,
            null
        )
    }

    private fun uiRegistration(key: String): FilterRegistration<*>? =
        byUiKey[key] ?: key.pixelationKey()?.let(byUiKey::get)

    override fun toUiFilter(filter: Filter<*>): UiFilter<*>? =
        registrationFor(filter)?.createUi?.invoke(filter.value)

    override fun create(type: KClass<out Filter<*>>, value: Any?): UiFilter<*>? =
        byType[type]?.createUi?.invoke(value)

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
        val value = entry?.normalizeParams?.invoke(filter.value) ?: filter.value
        val type = entry?.transformationKey ?: filter.javaClass.name
        val key = if (value is Filter.EmptyValue) type else "$type:${value.hashCode()}"
        val extra = filter.cacheKeyExtra
        return if (extra == null) key else "$key:${extra.hashCode()}"
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
        val (name, payload) = params
        val key = name.trim()
        return when {
            key == "ProceduralParams" -> decodeShaderTemplate?.invoke(parseShaderTemplate(payload))
            key == paramsKey || type.acceptsOldParamsKey(key) -> {
                runCatching { decodeParams?.invoke(payload.trim()) }.getOrNull()
                    ?: type.readOldParams(key, payload.trim())?.let { normalizeParams?.invoke(it) }
            }

            else -> readStoredParams?.invoke(params.fromPair())
        }
    }

    private fun registrationFor(filter: Filter<*>): FilterRegistration<*>? = when (filter) {
        is UiFilter<*> -> byUiKey[filter.serializationKey]
        else -> byImplementation[filter::class]
            ?: registrations.firstOrNull { it.type.isInstance(filter) }
    }
}
