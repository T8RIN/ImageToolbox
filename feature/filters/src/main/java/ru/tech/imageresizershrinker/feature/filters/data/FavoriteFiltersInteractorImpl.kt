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

@file:Suppress("UNCHECKED_CAST")

package ru.tech.imageresizershrinker.feature.filters.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.model.BokehParams
import ru.tech.imageresizershrinker.core.filters.domain.model.FadeSide
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterValueWrapper
import ru.tech.imageresizershrinker.core.filters.domain.model.GlitchParams
import ru.tech.imageresizershrinker.core.filters.domain.model.LinearTiltShiftParams
import ru.tech.imageresizershrinker.core.filters.domain.model.MotionBlurParams
import ru.tech.imageresizershrinker.core.filters.domain.model.RadialTiltShiftParams
import ru.tech.imageresizershrinker.core.filters.domain.model.SideFadeParams
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.filters.domain.model.WaterParams
import ru.tech.imageresizershrinker.feature.filters.di.FilterInteractorDataStore
import javax.inject.Inject
import kotlin.reflect.full.primaryConstructor

internal class FavoriteFiltersInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @FilterInteractorDataStore private val dataStore: DataStore<Preferences>
) : FavoriteFiltersInteractor<Bitmap> {

    override fun getFavoriteFilters(): Flow<List<Filter<Bitmap, *>>> = dataStore.data.map { prefs ->
        prefs[FAVORITE_FILTERS]?.toFiltersList(false) ?: emptyList()
    }

    override suspend fun toggleFavorite(filter: Filter<Bitmap, *>) {
        val currentFilters = getFavoriteFilters().first()
        if (currentFilters.filterIsInstance(filter::class.java).isEmpty()) {
            dataStore.edit { prefs ->
                prefs[FAVORITE_FILTERS] = (currentFilters + filter).toDatastoreString()
            }
        } else {
            dataStore.edit { prefs ->
                prefs[FAVORITE_FILTERS] = currentFilters
                    .filter {
                        !it::class.java.isInstance(filter)
                    }
                    .toDatastoreString()
            }
        }
    }

    override fun getTemplateFilters(): Flow<List<TemplateFilter<Bitmap>>> =
        dataStore.data.map { prefs ->
            prefs[TEMPLATE_FILTERS]?.toTemplateFiltersList() ?: emptyList()
        }

    override suspend fun addTemplateFilterFromString(
        string: String,
        onError: suspend () -> Unit
    ) {
        if (context.applicationInfo.packageName in string && "Filter" in string) {
            string.toTemplateFiltersList().firstOrNull()?.let { addTemplateFilter(it) }
        } else onError()
    }

    override suspend fun addTemplateFilterFromUri(
        uri: String,
        onError: suspend () -> Unit
    ) {
        context.contentResolver.openInputStream(uri.toUri())?.use {
            addTemplateFilterFromString(
                string = it.readBytes().decodeToString(),
                onError = onError
            )
        }
    }

    override suspend fun removeTemplateFilter(templateFilter: TemplateFilter<Bitmap>) {
        val currentFilters = getTemplateFilters().first()
        dataStore.edit { prefs ->
            prefs[TEMPLATE_FILTERS] = (currentFilters - templateFilter).toDatastoreString()
        }
    }

    override suspend fun convertTemplateFilterToString(
        templateFilter: TemplateFilter<Bitmap>
    ): String = listOf(templateFilter).toDatastoreString()

    override suspend fun addTemplateFilter(templateFilter: TemplateFilter<Bitmap>) {
        val currentFilters = getTemplateFilters().first()
        dataStore.edit { prefs ->
            prefs[TEMPLATE_FILTERS] = (currentFilters + templateFilter).toDatastoreString()
        }
    }

    private fun List<Filter<Bitmap, *>>.toDatastoreString(
        includeValue: Boolean = false
    ): String = joinToString(separator = ",") { filter ->
        filter::class.qualifiedName!! + if (includeValue && filter.value != null) {
            ":" + filter.value!!.toPair()?.let { it.first + ":" + it.second }
        } else ""
    }.trim()

    private fun Any.toPair(): Pair<String, String>? {
        return when (this) {
            is Int -> Int::class.simpleName!! to toString()
            is Float -> Float::class.simpleName!! to toString()
            is Unit -> Unit::class.simpleName!! to "Unit"
            is FloatArray -> FloatArray::class.simpleName!! to joinToString(separator = "$") { it.toString() }
            is FilterValueWrapper<*> -> {
                when (wrapped) {
                    is Color -> "${FilterValueWrapper::class.simpleName!!}{${Color::class.simpleName}}" to (wrapped as Color).toArgb()
                        .toString()

                    else -> null
                }
            }

            is Pair<*, *> -> {
                val firstPart = first!!.toPart()
                val secondPart = second!!.toPart()

                "${Pair::class.simpleName}{${first!!::class.simpleName}$${second!!::class.simpleName}}" to ("$firstPart$$secondPart")
            }

            is Triple<*, *, *> -> {
                val firstPart = first!!.toPart()
                val secondPart = second!!.toPart()
                val thirdPart = third!!.toPart()

                "${Triple::class.simpleName}{${first!!::class.simpleName}$${second!!::class.simpleName}$${third!!::class.simpleName}}" to ("$firstPart$$secondPart$$thirdPart")
            }

            is BokehParams -> {
                BokehParams::class.simpleName!! to "$radius$$amount$$scale"
            }

            is GlitchParams -> {
                GlitchParams::class.simpleName!! to "$channelsShiftX$$channelsShiftY$$corruptionSize$$corruptionCount$$corruptionShiftX$$corruptionShiftY"
            }

            is LinearTiltShiftParams -> {
                LinearTiltShiftParams::class.simpleName!! to "$blurRadius$$sigma$$anchorX$$anchorY$$holeRadius$$angle"
            }

            is RadialTiltShiftParams -> {
                RadialTiltShiftParams::class.simpleName!! to "$blurRadius$$sigma$$anchorX$$anchorY$$holeRadius"
            }

            is MotionBlurParams -> {
                MotionBlurParams::class.simpleName!! to "$radius$$sigma$$centerX$$centerY$$strength$$angle"
            }

            is SideFadeParams.Relative -> {
                SideFadeParams::class.simpleName!! to "${side.name}$$scale"
            }

            is WaterParams -> {
                WaterParams::class.simpleName!! to "$fractionSize$$frequencyX$$frequencyY$$amplitudeX$$amplitudeY"
            }

            else -> null
        }
    }

    private fun Pair<String, String>.fromPair(): Any? {
        val name = first.trim()
        val value = second.trim()

        return when {
            name == Int::class.simpleName -> second.toInt()
            name == Float::class.simpleName -> second.toFloat()
            name == Boolean::class.simpleName -> second.toBoolean()
            name == Unit::class.simpleName -> Unit
            name == FloatArray::class.simpleName -> value.split("$")
                .map { it.toFloat() }
                .toFloatArray()

            "${FilterValueWrapper::class.simpleName}{" in name -> {
                when (name.getTypeFromBraces()) {
                    Color::class.simpleName -> Color(value.toInt())
                    else -> null
                }
            }

            "${Pair::class.simpleName}{" in name -> {
                val (firstType, secondType) = name.getTypeFromBraces().split("$")
                val (firstPart, secondPart) = value.split("$")
                firstPart.fromPart(firstType) to secondPart.fromPart(secondType)
            }

            "${Triple::class.simpleName}{" in name -> {
                val (firstType, secondType, thirdType) = name.getTypeFromBraces().split("$")
                val (firstPart, secondPart, thirdPart) = value.split("$")
                Triple(
                    firstPart.fromPart(firstType),
                    secondPart.fromPart(secondType),
                    thirdPart.fromPart(thirdType)
                )
            }

            name == BokehParams::class.simpleName -> {
                val (radius, amount, scale) = value.split("$")
                BokehParams(radius.toInt(), amount.toInt(), scale.toFloat())
            }

            name == GlitchParams::class.simpleName -> {
                val (channelsShiftX,
                    channelsShiftY,
                    corruptionSize,
                    corruptionCount,
                    corruptionShiftX,
                    corruptionShiftY) = value.split("$")
                GlitchParams(
                    channelsShiftX = channelsShiftX.toFloat(),
                    channelsShiftY = channelsShiftY.toFloat(),
                    corruptionSize = corruptionSize.toFloat(),
                    corruptionCount = corruptionCount.toInt(),
                    corruptionShiftX = corruptionShiftX.toFloat(),
                    corruptionShiftY = corruptionShiftY.toFloat()
                )
            }

            name == LinearTiltShiftParams::class.simpleName -> {
                val (blurRadius, sigma, anchorX, anchorY, holeRadius, angle) = value.split("$")
                LinearTiltShiftParams(
                    blurRadius = blurRadius.toFloat(),
                    sigma = sigma.toFloat(),
                    anchorX = anchorX.toFloat(),
                    anchorY = anchorY.toFloat(),
                    holeRadius = holeRadius.toFloat(),
                    angle = angle.toFloat()
                )
            }

            name == RadialTiltShiftParams::class.simpleName -> {
                val (blurRadius, sigma, anchorX, anchorY, holeRadius) = value.split("$")
                RadialTiltShiftParams(
                    blurRadius = blurRadius.toFloat(),
                    sigma = sigma.toFloat(),
                    anchorX = anchorX.toFloat(),
                    anchorY = anchorY.toFloat(),
                    holeRadius = holeRadius.toFloat()
                )
            }

            name == MotionBlurParams::class.simpleName -> {
                val (radius, sigma, centerX, centerY, strength, angle) = value.split("$")
                MotionBlurParams(
                    radius = radius.toInt(),
                    sigma = sigma.toFloat(),
                    centerX = centerX.toFloat(),
                    centerY = centerY.toFloat(),
                    strength = strength.toFloat(),
                    angle = angle.toFloat()
                )
            }

            name == SideFadeParams::class.simpleName -> {
                val (sideName, scale) = value.split("$")
                SideFadeParams.Relative(
                    side = FadeSide.valueOf(sideName),
                    scale = scale.toFloat()
                )
            }

            name == WaterParams::class.simpleName -> {
                val (fractionSize, frequencyX, frequencyY, amplitudeX, amplitudeY) = value.split("$")
                WaterParams(
                    fractionSize = fractionSize.toFloat(),
                    frequencyX = frequencyX.toFloat(),
                    frequencyY = frequencyY.toFloat(),
                    amplitudeX = amplitudeX.toFloat(),
                    amplitudeY = amplitudeY.toFloat()
                )
            }

            else -> null
        }
    }

    private fun String.getTypeFromBraces(): String = removeSuffix("}").split("{")[1]

    private fun Any.toPart(): String {
        return when (this) {
            is Int -> toString()
            is Float -> toString()
            is Color -> toArgb().toString()
            is Boolean -> toString()
            else -> ""
        }
    }

    private fun String.fromPart(type: String): Any {
        return when (type) {
            Int::class.simpleName!! -> toInt()
            Float::class.simpleName!! -> toFloat()
            Color::class.simpleName!! -> Color(toInt())
            Boolean::class.simpleName!! -> toBoolean()
            else -> ""
        }
    }

    private fun String.toFiltersList(
        includeValue: Boolean
    ): List<Filter<Bitmap, *>> = split(",").mapNotNull {
        val (name, value) = if (includeValue) {
            runCatching {
                val splitData = it.split(":")
                val className = splitData[1]
                val valueString = splitData[2]

                splitData[0].trim() to (className to valueString).fromPair()
            }.getOrElse { _ -> it.trim() to Unit }
        } else it.trim() to Unit
        val filterClass = Class.forName(name) as Class<Filter<Bitmap, *>>
        filterClass.kotlin.primaryConstructor?.run {
            try {
                callBy(if (includeValue && value != null) mapOf(parameters[0] to value) else emptyMap())
            } catch (e: Throwable) {
                callBy(emptyMap())
            }
        }
    }

    private fun String.toTemplateFiltersList(): List<TemplateFilter<Bitmap>> = split("/").map {
        val splitData = it.split("+")
        val name = splitData[0]
        val filters = splitData[1].toFiltersList(true)

        TemplateFilter(
            name = name,
            filters = filters
        )
    }

    private fun List<TemplateFilter<Bitmap>>.toDatastoreString(): String =
        joinToString(separator = "/") {
            it.name + "+" + it.filters.toDatastoreString(true)
        }


    private operator fun <E> List<E>.component6(): E = get(5)
}

private val FAVORITE_FILTERS = stringPreferencesKey("FAVORITE_FILTERS")
private val TEMPLATE_FILTERS = stringPreferencesKey("TEMPLATE_FILTERS")