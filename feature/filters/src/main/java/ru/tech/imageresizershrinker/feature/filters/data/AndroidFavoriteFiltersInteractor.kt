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
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.model.BlurEdgeMode
import ru.tech.imageresizershrinker.core.filters.domain.model.ClaheParams
import ru.tech.imageresizershrinker.core.filters.domain.model.EnhancedZoomBlurParams
import ru.tech.imageresizershrinker.core.filters.domain.model.FadeSide
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterValueWrapper
import ru.tech.imageresizershrinker.core.filters.domain.model.GlitchParams
import ru.tech.imageresizershrinker.core.filters.domain.model.LinearGaussianParams
import ru.tech.imageresizershrinker.core.filters.domain.model.LinearTiltShiftParams
import ru.tech.imageresizershrinker.core.filters.domain.model.PopArtBlendingMode
import ru.tech.imageresizershrinker.core.filters.domain.model.RadialTiltShiftParams
import ru.tech.imageresizershrinker.core.filters.domain.model.SideFadeParams
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.filters.domain.model.TransferFunc
import ru.tech.imageresizershrinker.core.filters.domain.model.WaterParams
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.toImageModel
import java.io.File
import javax.inject.Inject
import kotlin.reflect.full.primaryConstructor

internal class AndroidFavoriteFiltersInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>
) : FavoriteFiltersInteractor {

    override fun getFavoriteFilters(): Flow<List<Filter<*>>> = dataStore.data.map { prefs ->
        prefs[FAVORITE_FILTERS]?.toFiltersList(false) ?: emptyList()
    }

    override suspend fun toggleFavorite(filter: Filter<*>) {
        val currentFilters = getFavoriteFilters().first()
        if (currentFilters.filterIsInstance(filter::class.java).isEmpty()) {
            dataStore.edit { prefs ->
                prefs[FAVORITE_FILTERS] = (listOf(filter) + currentFilters).toDatastoreString()
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

    override fun getTemplateFilters(): Flow<List<TemplateFilter>> =
        dataStore.data.map { prefs ->
            prefs[TEMPLATE_FILTERS]?.takeIf { it.isNotEmpty() }?.toTemplateFiltersList()
                ?: emptyList()
        }

    override suspend fun addTemplateFilterFromString(
        string: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit,
    ) {
        runCatching {
            if (isValidTemplateFilter(string)) {
                runCatching {
                    string.removePrefix(LINK_HEADER).toTemplateFiltersList().firstOrNull()
                }.getOrNull()?.let {
                    addTemplateFilter(it)
                    onSuccess(it.name, it.filters.size)
                } ?: onFailure()
            } else onFailure()
        }.onFailure { onFailure() }
    }

    override fun isValidTemplateFilter(
        string: String,
    ): Boolean =
        (context.applicationInfo.packageName in string || PACKAGE in string) && "Filter" in string && LINK_HEADER in string

    override suspend fun reorderFavoriteFilters(newOrder: List<Filter<*>>) {
        dataStore.edit { prefs ->
            prefs[FAVORITE_FILTERS] = newOrder.toDatastoreString()
        }
    }

    override fun getFilterPreviewModel(): Flow<ImageModel> = dataStore.data.map { prefs ->
        prefs[PREVIEW_MODEL]?.let {
            when (it) {
                "0" -> R.drawable.filter_preview_source
                "1" -> R.drawable.filter_preview_source_3
                else -> it
            }.toImageModel()
        } ?: R.drawable.filter_preview_source.toImageModel()
    }

    override suspend fun setFilterPreviewModel(uri: String) {
        if (uri.any { !it.isDigit() }) {
            imageGetter.getImage(
                data = uri,
                size = 300
            )?.let { image ->
                fileController.writeBytes(
                    File(context.filesDir, "filterPreview.png").apply {
                        createNewFile()
                    }.toUri().toString()
                ) { writeable ->
                    writeable.writeBytes(
                        imageCompressor.compress(
                            image = image,
                            imageFormat = ImageFormat.Png.Lossless,
                            quality = Quality.Base(100)
                        )
                    )
                }
            }
        }
        dataStore.edit {
            it[PREVIEW_MODEL] = uri
        }
    }

    override suspend fun addTemplateFilterFromUri(
        uri: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit,
    ) {
        addTemplateFilterFromString(
            string = fileController.readBytes(uri).decodeToString(),
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    override suspend fun removeTemplateFilter(templateFilter: TemplateFilter) {
        val currentFilters = getTemplateFilters().first()
        dataStore.edit { prefs ->
            prefs[TEMPLATE_FILTERS] = currentFilters.filter {
                convertTemplateFilterToString(it) != convertTemplateFilterToString(templateFilter)
            }.toDatastoreString()
        }
    }

    override suspend fun convertTemplateFilterToString(
        templateFilter: TemplateFilter,
    ): String = "$LINK_HEADER${listOf(templateFilter).toDatastoreString()}"

    override suspend fun addTemplateFilter(templateFilter: TemplateFilter) {
        val currentFilters = getTemplateFilters().first()
        dataStore.edit { prefs ->
            prefs[TEMPLATE_FILTERS] = currentFilters.let { filters ->
                val index = filters.indexOfFirst {
                    convertTemplateFilterToString(it) == convertTemplateFilterToString(
                        templateFilter
                    )
                }
                if (index != -1) filters else filters + templateFilter
            }.toDatastoreString()
        }
    }

    private fun List<Filter<*>>.toDatastoreString(
        includeValue: Boolean = false,
    ): String = joinToString(separator = FILTERS_SEPARATOR) { filter ->
        filter::class.qualifiedName!!.replace(
            context.applicationInfo.packageName,
            PACKAGE
        ) + if (includeValue && filter.value != null) {
            VALUE_SEPARATOR + filter.value!!.toPair()
                ?.let { it.first + VALUE_SEPARATOR + it.second }
        } else ""
    }.trim()

    private fun Any.toPair(): Pair<String, String>? {
        return when (this) {
            is Int -> Int::class.simpleName!! to toString()
            is Float -> Float::class.simpleName!! to toString()
            is Unit -> Unit::class.simpleName!! to "Unit"
            is FloatArray -> FloatArray::class.simpleName!! to joinToString(separator = PROPERTIES_SEPARATOR) { it.toString() }
            is FilterValueWrapper<*> -> {
                when (wrapped) {
                    is ColorModel -> "${FilterValueWrapper::class.simpleName!!}{${ColorModel::class.simpleName}}" to (wrapped as ColorModel).colorInt
                        .toString()

                    else -> null
                }
            }

            is Pair<*, *> -> {
                val firstPart = first!!.toPart()
                val secondPart = second!!.toPart()
                "${Pair::class.simpleName}{${first!!::class.simpleName}$PROPERTIES_SEPARATOR${second!!::class.simpleName}}" to listOf(
                    firstPart,
                    secondPart
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is Triple<*, *, *> -> {
                val firstPart = first!!.toPart()
                val secondPart = second!!.toPart()
                val thirdPart = third!!.toPart()

                "${Triple::class.simpleName}{${first!!::class.simpleName}$PROPERTIES_SEPARATOR${second!!::class.simpleName}$PROPERTIES_SEPARATOR${third!!::class.simpleName}}" to listOf(
                    firstPart,
                    secondPart,
                    thirdPart
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is GlitchParams -> {
                GlitchParams::class.simpleName!! to listOf(
                    channelsShiftX,
                    channelsShiftY,
                    corruptionSize,
                    corruptionCount,
                    corruptionShiftX,
                    corruptionShiftY
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is LinearTiltShiftParams -> {
                LinearTiltShiftParams::class.simpleName!! to listOf(
                    blurRadius,
                    sigma,
                    anchorX,
                    anchorY,
                    holeRadius,
                    angle
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is RadialTiltShiftParams -> {
                RadialTiltShiftParams::class.simpleName!! to listOf(
                    blurRadius,
                    sigma,
                    anchorX,
                    anchorY,
                    holeRadius
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is EnhancedZoomBlurParams -> {
                EnhancedZoomBlurParams::class.simpleName!! to listOf(
                    radius,
                    sigma,
                    centerX,
                    centerY,
                    strength,
                    angle
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is SideFadeParams.Relative -> {
                SideFadeParams::class.simpleName!! to listOf(
                    side.name, scale
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is WaterParams -> {
                WaterParams::class.simpleName!! to listOf(
                    fractionSize,
                    frequencyX,
                    frequencyY,
                    amplitudeX,
                    amplitudeY
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is ClaheParams -> {
                ClaheParams::class.simpleName!! to listOf(
                    threshold,
                    gridSizeHorizontal,
                    gridSizeVertical,
                    binsCount
                ).joinToString(PROPERTIES_SEPARATOR)
            }

            is LinearGaussianParams -> {
                LinearGaussianParams::class.simpleName!! to listOf(
                    kernelSize,
                    sigma,
                    edgeMode.name,
                    transferFunction.name
                ).joinToString(PROPERTIES_SEPARATOR)
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
            name == FloatArray::class.simpleName -> value.split(PROPERTIES_SEPARATOR)
                .map { it.toFloat() }
                .toFloatArray()

            "${FilterValueWrapper::class.simpleName}{" in name -> {
                when (name.getTypeFromBraces()) {
                    ColorModel::class.simpleName -> FilterValueWrapper(ColorModel(value.toInt()))
                    else -> null
                }
            }

            "${Pair::class.simpleName}{" in name -> {
                val (firstType, secondType) = name.getTypeFromBraces().split(PROPERTIES_SEPARATOR)
                val (firstPart, secondPart) = value.split(PROPERTIES_SEPARATOR)
                firstPart.fromPart(firstType) to secondPart.fromPart(secondType)
            }

            "${Triple::class.simpleName}{" in name -> {
                val (firstType, secondType, thirdType) = name.getTypeFromBraces()
                    .split(PROPERTIES_SEPARATOR)
                val (firstPart, secondPart, thirdPart) = value.split(PROPERTIES_SEPARATOR)
                Triple(
                    firstPart.fromPart(firstType),
                    secondPart.fromPart(secondType),
                    thirdPart.fromPart(thirdType)
                )
            }

            name == GlitchParams::class.simpleName -> {
                val (
                    channelsShiftX,
                    channelsShiftY,
                    corruptionSize,
                    corruptionCount,
                    corruptionShiftX,
                    corruptionShiftY,
                ) = value.split(PROPERTIES_SEPARATOR)
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
                val (blurRadius, sigma, anchorX, anchorY, holeRadius, angle) = value.split(
                    PROPERTIES_SEPARATOR
                )
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
                val (blurRadius, sigma, anchorX, anchorY, holeRadius) = value.split(
                    PROPERTIES_SEPARATOR
                )
                RadialTiltShiftParams(
                    blurRadius = blurRadius.toFloat(),
                    sigma = sigma.toFloat(),
                    anchorX = anchorX.toFloat(),
                    anchorY = anchorY.toFloat(),
                    holeRadius = holeRadius.toFloat()
                )
            }

            name == EnhancedZoomBlurParams::class.simpleName -> {
                val (radius, sigma, centerX, centerY, strength, angle) = value.split(
                    PROPERTIES_SEPARATOR
                )
                EnhancedZoomBlurParams(
                    radius = radius.toInt(),
                    sigma = sigma.toFloat(),
                    centerX = centerX.toFloat(),
                    centerY = centerY.toFloat(),
                    strength = strength.toFloat(),
                    angle = angle.toFloat()
                )
            }

            name == SideFadeParams::class.simpleName -> {
                val (sideName, scale) = value.split(PROPERTIES_SEPARATOR)
                SideFadeParams.Relative(
                    side = FadeSide.valueOf(sideName),
                    scale = scale.toFloat()
                )
            }

            name == WaterParams::class.simpleName -> {
                val (fractionSize, frequencyX, frequencyY, amplitudeX, amplitudeY) = value.split(
                    PROPERTIES_SEPARATOR
                )
                WaterParams(
                    fractionSize = fractionSize.toFloat(),
                    frequencyX = frequencyX.toFloat(),
                    frequencyY = frequencyY.toFloat(),
                    amplitudeX = amplitudeX.toFloat(),
                    amplitudeY = amplitudeY.toFloat()
                )
            }

            name == ClaheParams::class.simpleName -> {
                val (threshold, gridSizeHorizontal, gridSizeVertical, binsCount) = value.split(
                    PROPERTIES_SEPARATOR
                )
                ClaheParams(
                    threshold = threshold.toFloat(),
                    gridSizeHorizontal = gridSizeHorizontal.toInt(),
                    gridSizeVertical = gridSizeVertical.toInt(),
                    binsCount = binsCount.toInt()
                )
            }

            name == LinearGaussianParams::class.simpleName -> {
                val (kernelSize, sigma, edgeModeName, transferFunctionName) = value.split(
                    PROPERTIES_SEPARATOR
                )
                LinearGaussianParams(
                    kernelSize = kernelSize.toInt(),
                    sigma = sigma.toFloat(),
                    edgeMode = BlurEdgeMode.valueOf(edgeModeName),
                    transferFunction = TransferFunc.valueOf(transferFunctionName)
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
            is ColorModel -> colorInt.toString()
            is Boolean -> toString()
            is BlurEdgeMode -> name
            is TransferFunc -> name
            is FadeSide -> name
            is PopArtBlendingMode -> name
            else -> ""
        }
    }

    private fun String.fromPart(type: String): Any {
        return when (type) {
            Int::class.simpleName!! -> toInt()
            Float::class.simpleName!! -> toFloat()
            ColorModel::class.simpleName!! -> ColorModel(toInt())
            Boolean::class.simpleName!! -> toBoolean()
            BlurEdgeMode::class.simpleName!! -> BlurEdgeMode.valueOf(this)
            TransferFunc::class.simpleName!! -> TransferFunc.valueOf(this)
            FadeSide::class.simpleName!! -> FadeSide.valueOf(this)
            PopArtBlendingMode::class.simpleName!! -> PopArtBlendingMode.valueOf(this)
            else -> ""
        }
    }

    private fun String.toFiltersList(
        includeValue: Boolean,
    ): List<Filter<*>> = split(FILTERS_SEPARATOR).mapNotNull { line ->
        if (line.trim().isEmpty()) return@mapNotNull null

        val (name, value) = if (includeValue) {
            runCatching {
                val splitData = line.split(VALUE_SEPARATOR)
                val className = splitData[1]
                val valueString = splitData[2]

                splitData[0].trim() to (className to valueString).fromPair()
            }.getOrElse { line.trim() to Unit }
        } else line.trim() to Unit
        runCatching {
            val filterClass = Class.forName(
                name.replace(
                    PACKAGE,
                    context.applicationInfo.packageName
                )
            ) as Class<Filter<*>>
            filterClass.kotlin.primaryConstructor?.run {
                try {
                    if (includeValue && value != null) {
                        callBy(mapOf(parameters[0] to value))
                    } else callBy(emptyMap())
                } catch (_: Throwable) {
                    callBy(emptyMap())
                }
            }
        }.getOrNull()
    }

    private fun String.toTemplateFiltersList(): List<TemplateFilter> =
        split(TEMPLATES_SEPARATOR).map {
            val splitData = it.split(TEMPLATE_CONTENT_SEPARATOR)
            val name = splitData[0]
            val filters = splitData[1].toFiltersList(true)

            TemplateFilter(
                name = name,
                filters = filters
            )
        }

    private fun List<TemplateFilter>.toDatastoreString(): String =
        joinToString(separator = TEMPLATES_SEPARATOR) {
            it.name + TEMPLATE_CONTENT_SEPARATOR + it.filters.toDatastoreString(true)
        }


    private operator fun <E> List<E>.component6(): E = get(5)
}

private const val LINK_HEADER: String = "https://github.com/T8RIN/ImageToolbox?"

private const val PACKAGE = "^^"
private const val FILTERS_SEPARATOR = ","
private const val TEMPLATES_SEPARATOR = "\\"
private const val TEMPLATE_CONTENT_SEPARATOR = "+"
private const val VALUE_SEPARATOR = ":"
private const val PROPERTIES_SEPARATOR = "$"

private val FAVORITE_FILTERS = stringPreferencesKey("FAVORITE_FILTERS")
private val TEMPLATE_FILTERS = stringPreferencesKey("TEMPLATE_FILTERS")
private val PREVIEW_MODEL = stringPreferencesKey("PREVIEW_MODEL")