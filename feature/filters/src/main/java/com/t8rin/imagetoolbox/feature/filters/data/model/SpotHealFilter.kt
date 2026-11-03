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
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SpotHeal.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.image.loadBitmap
import com.t8rin.neural_tools.inpaint.LaMaProcessor
import com.t8rin.neural_tools.withCancellableRunOptions
import com.t8rin.opencv_tools.spot_heal.SpotHealer
import com.t8rin.opencv_tools.spot_heal.model.HealType

@FilterDefinition
internal class SpotHealFilter(
    override val value: Value,
) : FilterTransformation, Filter.SpotHeal {

    private val settings by lazy {
        appContext.entryPoint<SettingsStateEntryPoint>().settingsManager.settingsState.value
    }

    override val cacheKeyExtra: Pair<Int, Boolean>?
        get() = if (value.mode == SpotHealMode.LaMa) {
            settings.lamaThreads to settings.lamaLimitResolution
        } else null

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val mask = value.mask.data.loadBitmap() ?: return input

        return when (value.mode) {
            SpotHealMode.OpenCV -> openCV(
                input = input,
                mask = mask
            )

            SpotHealMode.LaMa -> lama(
                input = input,
                mask = mask,
                isPreview = size.isDefined()
            )
        }
    }

    private fun openCV(
        input: Bitmap,
        mask: Bitmap
    ) = SpotHealer.heal(
        image = input,
        mask = mask,
        radius = 3f,
        type = HealType.TELEA
    )

    private suspend fun lama(
        input: Bitmap,
        mask: Bitmap,
        isPreview: Boolean
    ) = withCancellableRunOptions(
        onCancellation = { it?.recycle() }
    ) { options ->
        LaMaProcessor.inpaint(
            image = input,
            mask = mask,
            runOptions = options,
            isPreview = isPreview,
            threads = settings.lamaThreads,
            limitResolution = settings.lamaLimitResolution
        )
    } ?: openCV(
        input = input,
        mask = mask
    )

    companion object : Filter.Schema.HiddenValue<Value> {
        override val parameters = structuredParameters {
            Value::mask.editor { ImageModel("") }
            Value::mode.enum(label = { it.label }) { SpotHealMode.OpenCV }
        }
    }
}