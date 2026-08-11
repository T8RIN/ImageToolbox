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

import android.graphics.Color
import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.RawGmicFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class Blocks3DFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "32",
            "0",
            "4",
            "1.5",
            "30",
            "60",
            "45",
            "50",
            "50",
            "0",
            "-50",
            "-100",
            "0.5",
            "0.7",
            "true",
            "true",
            "-2147483648"
        )
    )
) : GMICFilterTransformation(), Filter.Blocks3D {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter {
        val outlineColor = value[16].toInt()

        return RawGmicFilter(
            command = listOf(
                value[0],
                value[1],
                value[2],
                value[3],
                value[4],
                value[5],
                value[6],
                value[7],
                value[8],
                value[9],
                value[10],
                value[11],
                value[12],
                value[13],
                value[14].toGmicBoolean(),
                value[15].toGmicBoolean(),
                Color.red(outlineColor).toString(),
                Color.green(outlineColor).toString(),
                Color.blue(outlineColor).toString(),
                Color.alpha(outlineColor).toString()
            ).joinToString(
                separator = ",",
                prefix = "fx_blocks3d "
            )
        )
    }

}

private fun String.toGmicBoolean(): String = if (toBoolean()) "1" else "0"
