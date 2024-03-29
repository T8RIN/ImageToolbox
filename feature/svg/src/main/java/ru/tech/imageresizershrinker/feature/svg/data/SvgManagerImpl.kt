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

package ru.tech.imageresizershrinker.feature.svg.data

import android.content.Context
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import com.t8rin.image.toolbox.svg.ImageTracerAndroid
import com.t8rin.image.toolbox.svg.ImageTracerAndroid.SvgListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.di.DefaultDispatcher
import ru.tech.imageresizershrinker.core.di.IoDispatcher
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.saving.RandomStringGenerator
import ru.tech.imageresizershrinker.feature.svg.domain.SvgManager
import ru.tech.imageresizershrinker.feature.svg.domain.SvgParams
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.inject.Inject


internal class SvgManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val randomStringGenerator: RandomStringGenerator,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>
) : SvgManager {

    override suspend fun convertToSvg(
        imageUris: List<String>,
        params: SvgParams,
        onError: (Throwable) -> Unit,
        onProgress: suspend (originalUri: String, data: ByteArray) -> Unit
    ) = withContext(defaultDispatcher) {
        imageUris.forEach { uri ->
            runCatching {
                val folder = File(context.cacheDir, "svg").apply { mkdirs() }
                val file = File(folder, "${randomStringGenerator.generate(10)}.svg")

                withContext(ioDispatcher) {
                    BufferedWriter(
                        FileWriter(file)
                    ).use { writer ->
                        ImageTracerAndroid.imageToSVG(
                            imageGetter.getImage(data = uri)!!,
                            params.toOptions(),
                            null,
                            SvgTracer {
                                writer.write(it)
                            }
                        )
                    }
                }

                onProgress(uri, file.readBytes())
            }.onFailure(onError)
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
    }

//val options = HashMap<String, Array<Float>>().apply {
//    this["ltres"] = arrayOf(1f, 0f, 10f)
//    this["qtres"] = arrayOf(1f, 0f, 10f)
//    this["pathomit"] = arrayOf(8f, 0f, 64f) // int
//
//    this["mincolorratio"] = arrayOf(0.02f, 0f, 0.1f)
//
//    this["scale"] = arrayOf(1f, 0.01f, 100f)
//    this["roundcoords"] = arrayOf(1f, 0f, 8f)
//}

}