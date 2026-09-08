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

package com.t8rin.imagetoolbox.texture_generation.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.withDefaultsFor
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PatternTextureGeneratorTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val types = TextureFilterType.entries.filter { it.pattern != null }

    @Test
    fun allPatternsRenderAtDefaultAndExtremeSettings() {
        for (type in types) {
            val pattern = requireNotNull(type.pattern)
            val params = TextureParams.Default.withDefaultsFor(type)
            val settings = listOf(
                params.patternParams.values,
                pattern.parameters.associate { it.name to it.range.start },
                pattern.parameters.associate { it.name to it.range.endInclusive })
            for (values in settings) {
                val bitmap = generatePatternTexture(
                    65, 49, params.copy(
                        patternParams = params.patternParams.copy(values = values)
                    ), context
                )
                val pixels = IntArray(65 * 49)
                bitmap.getPixels(pixels, 0, 65, 0, 0, 65, 49)
                assertTrue("$type $values alpha", pixels.all { Color.alpha(it) == 255 })
                if (values == params.patternParams.values) {
                    assertTrue("$type must render its pattern", pixels.toSet().size > 1)
                }
                bitmap.recycle()
            }
            val preview = generatePatternTexture(256, 256, params, context)
            File(context.getExternalFilesDir(null), "$type.png").outputStream().use {
                preview.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            preview.recycle()
        }
    }
}