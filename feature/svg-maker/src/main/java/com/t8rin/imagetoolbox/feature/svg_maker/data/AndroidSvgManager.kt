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

@file:Suppress("FunctionName")

package com.t8rin.imagetoolbox.feature.svg_maker.data

import android.content.Context
import android.graphics.Bitmap
import com.t8rin.image.toolbox.svg.ImageTracerAndroid
import com.t8rin.image.toolbox.svg.ImageTracerAndroid.SvgListener
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.RandomStringGenerator
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.feature.svg_maker.domain.SvgManager
import com.t8rin.imagetoolbox.feature.svg_maker.domain.SvgParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


internal class AndroidSvgManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val randomStringGenerator: RandomStringGenerator,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, SvgManager {

    override suspend fun convertToSvg(
        imageUris: List<String>,
        params: SvgParams,
        onFailure: (Throwable) -> Unit,
        onProgress: suspend (originalUri: String, data: ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        imageUris.forEach { uri ->
            runSuspendCatching {
                val folder = File(context.cacheDir, "svg").apply { mkdirs() }
                val file = File(folder, "${randomStringGenerator.generate(10)}.svg")

                withContext(ioDispatcher) {
                    file.bufferedWriter().use { writer ->
                        ImageTracerAndroid.imageToSVG(
                            imageGetter.getImage(
                                data = uri,
                                size = if (params.isImageSampled) {
                                    IntegerSize(1000, 1000)
                                } else null
                            )!!,
                            params.toOptions(),
                            null,
                            SvgTracer {
                                writer.write(it)
                            }
                        )
                    }
                }

                onProgress(uri, file.readBytes())
            }.onFailure(onFailure)
        }
    }

    private fun SvgTracer(
        onProgress: (String) -> Unit
    ): SvgListener = object : SvgListener {
        override fun onProgress(
            part: String?
        ): SvgListener = apply { onProgress(part ?: "") }

        override fun onProgress(
            part: Double
        ): SvgListener = apply { onProgress(part.toString()) }
    }

    private fun SvgParams.toOptions(): HashMap<String, Float> = HashMap<String, Float>().apply {
        put("numberofcolors", colorsCount.toFloat())
        put("colorquantcycles", quantizationCyclesCount.toFloat())
        put("colorsampling", if (isPaletteSampled) 1f else 0f)
        put("blurradius", blurRadius.toFloat())
        put("blurdelta", blurDelta.toFloat())
        put("pathomit", pathOmit.toFloat())
        put("ltres", linesThreshold)
        put("qtres", quadraticThreshold)
        put("roundcoords", coordinatesRoundingAmount.toFloat())
        put("mincolorratio", minColorRatio)
        put("scale", svgPathsScale)
    }

}