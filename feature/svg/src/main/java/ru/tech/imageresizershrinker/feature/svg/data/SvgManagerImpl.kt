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

package ru.tech.imageresizershrinker.feature.svg.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.di.DefaultDispatcher
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.saving.RandomStringGenerator
import ru.tech.imageresizershrinker.feature.svg.domain.SvgManager
import java.io.File
import javax.inject.Inject


internal class SvgManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val randomStringGenerator: RandomStringGenerator,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>
) : SvgManager {

    override suspend fun convertToSvg(
        imageUris: List<String>,
        onError: (Throwable) -> Unit,
        onProgress: suspend (originalUri: String, data: ByteArray) -> Unit
    ) = withContext(dispatcher) {
        imageUris.forEach { uri ->
            runCatching {
//                val options = HashMap<String, Array<Float>>().apply {
//                    this["ltres"] = arrayOf(1f, 0f, 10f)
//                    this["qtres"] = arrayOf(1f, 0f, 10f)
//                    this["pathomit"] = arrayOf(8f, 0f, 64f) // int
//
//                    this["colorsampling"] =
//                        arrayOf(1f, 0f, 1f) // 0 = off else on
//
//                    this["numberofcolors"] = arrayOf(16f, 2f, 64f) // int
//
//                    this["mincolorratio"] = arrayOf(0.02f, 0f, 0.1f)
//                    this["colorquantcycles"] = arrayOf(3f, 1f, 10f) // int
//
//                    this["scale"] = arrayOf(1f, 0.01f, 100f)
//                    this["roundcoords"] = arrayOf(1f, 0f, 8f)
//                    this["blurradius"] = arrayOf(0f, 0f, 5f)
//                    this["blurdelta"] = arrayOf(20f, 0f, 255f)
//                }


                val svgText = ImageTracerAndroid.imageToSVG(
                    imageGetter.getImage(data = uri),
                    ImageTracerAndroid.checkoptions(HashMap()),
                    null
                )
                val folder = File(context.cacheDir, "svg").apply { mkdirs() }
                val file = File(folder, randomStringGenerator.generate(10) + ".svg")

                val svgData = file.apply {
                    writeText(svgText)
                }.readBytes()

                onProgress(uri, svgData)
            }.onFailure(onError)
        }
    }

    private fun Uri.getFilename(): String = DocumentFile.fromSingleUri(context, this)?.name ?: ""

}