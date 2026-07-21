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

import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.BlurredFrame
import com.t8rin.gmic.filters.FrameNormalization
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class BlurredFrameFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "30",
            "30",
            "0",
            "5",
            "0",
            "false",
            "-8355712",
            "None",
            "5",
            "-1",
            "2",
            "2",
            "1",
            "0",
            "0.5",
            "0.5",
            "0"
        )
    )
) : GMICFilterTransformation(), Filter.BlurredFrame {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = BlurredFrame(
        horizontalSize = value[0].toFloat(),
        verticalSize = value[1].toFloat(),
        crop = value[2].toFloat(),
        blur = value[3].toFloat(),
        roundness = value[4].toFloat(),
        applyColorBalance = value[5].toBoolean(),
        balanceColor = value[6].toInt(),
        normalization = FrameNormalization.valueOf(value[7]),
        outlineSize = value[8].toFloat(),
        outlineColor = value[9].toInt(),
        xShadow = value[10].toFloat(),
        yShadow = value[11].toFloat(),
        shadowSmoothness = value[12].toFloat(),
        shadowContrast = value[13].toFloat(),
        xCentering = value[14].toFloat(),
        yCentering = value[15].toFloat(),
        angle = value[16].toFloat()
    )

}
