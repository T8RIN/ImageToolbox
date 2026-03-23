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

package com.t8rin.collages.utils

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.net.toUri
import com.t8rin.collages.Collage
import com.t8rin.collages.CollageType
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.utils.CollageLayoutFactory.COLLAGE_MAP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun PreviewCollageGeneration(
    startFrom: Int = 0
) {
    fun Bitmap.replaceColor(
        fromColor: Color,
        targetColor: Color,
        tolerance: Float
    ): Bitmap {
        fun Color.distanceFrom(color: Color): Float {
            return sqrt(
                (red - color.red).pow(2) + (green - color.green).pow(2) + (blue - color.blue).pow(
                    2
                )
            )
        }

        val width = width
        val height = height
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        for (x in pixels.indices) {
            pixels[x] = if (Color(pixels[x]).distanceFrom(fromColor) <= tolerance) {
                targetColor.toArgb()
            } else pixels[x]
        }
        val result = createBitmap(width, height)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    var allFrames: List<CollageLayout> by remember {
        mutableStateOf(emptyList())
    }
    val context = LocalContext.current

    LaunchedEffect(context) {
        allFrames =
            COLLAGE_MAP.values.map { it.invoke() }.filter { it.photoItemList.size >= startFrom }
    }

    var previewImageUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    LaunchedEffect(previewImageUri) {
        if (previewImageUri == null) {
            val file = File(context.cacheDir, "tmp.png")

            file.outputStream().use {
                createBitmap(200, 200).applyCanvas {
                    drawColor(Color.Black.toArgb())
                }.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            previewImageUri = file.toUri()
        }
    }

    val scope = rememberCoroutineScope {
        Dispatchers.IO
    }

    val dir = remember {
        File(context.cacheDir, "frames").apply {
            deleteRecursively()
            mkdirs()
        }
    }

    if (previewImageUri != null) {
        Row(
            modifier = Modifier
                .keepScreenOn()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
                .systemBarsPadding()
        ) {
            val data = remember(allFrames) {
                allFrames.groupBy { it.photoItemList.size }.toList().sortedBy { it.first }
            }
            data.forEachIndexed { index, (count, templates) ->
                Text(
                    count.toString(),
                    color = Color.White,
                    modifier = Modifier.background(Color.Black)
                )
                templates.forEach { template ->
                    val (_, title, _, photoItemList) = template
                    val density = LocalDensity.current
                    val spacing = with(density) {
                        1.5.dp.toPx()
                    }

                    var trigger by remember {
                        mutableStateOf(false)
                    }

                    LaunchedEffect(Unit) {
                        delay(500 + 10L * index)
                        trigger = true
                    }

                    Collage(
                        images = photoItemList.mapNotNull { previewImageUri },
                        modifier = Modifier.size(64.dp),
                        spacing = spacing,
                        cornerRadius = 0f,
                        onCollageCreated = { image ->
                            scope.launch {
                                val file = File(dir, "$title.png")

                                file.createNewFile()

                                file.outputStream().use {
                                    image.scale(525, 525, false).replaceColor(
                                        fromColor = Color.Black,
                                        targetColor = Color.Transparent,
                                        tolerance = 0.1f
                                    ).compress(Bitmap.CompressFormat.PNG, 100, it)
                                }
                                println("DONE: $title")
                            }
                        },
                        outputScaleRatio = 10f,
                        collageCreationTrigger = trigger,
                        collageType = CollageType(
                            layout = template,
                            index = null
                        ),
                        userInteractionEnabled = false
                    )
                }
            }
        }
    }
}