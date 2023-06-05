package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import coil.transform.Transformation
import com.commit451.coiltransformations.gpu.GPUFilterTransformation
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlin.reflect.full.primaryConstructor

@Parcelize
sealed class FilterTransformation<T>(
    private val context: @RawValue Context,
    @StringRes val title: Int,
    val paramsInfo: List<FilterParam> = listOf(),
    open val value: @RawValue T,
) : GPUFilterTransformation(context), Transformation, Parcelable {

    constructor(
        context: @RawValue Context,
        @StringRes title: Int,
        valueRange: ClosedFloatingPointRange<Float>,
        value: @RawValue T,
    ) : this(
        context = context,
        title = title,
        paramsInfo = listOf(FilterParam(null, valueRange, 2)),
        value = value
    )

    fun <T : Any> copy(value: T): FilterTransformation<*> =
        this::class.primaryConstructor!!.call(context, value)

    fun newInstance(): FilterTransformation<*> {
        return this::class.primaryConstructor!!.run { callBy(mapOf(parameters[0] to context)) }
    }

}

@Parcelize
data class FilterParam(
    val title: Int? = null,
    val valueRange: @RawValue ClosedFloatingPointRange<Float>,
    val roundTo: Int = 2
) : Parcelable

infix fun Int.paramTo(range: ClosedFloatingPointRange<Float>) = FilterParam(this, range, 2)