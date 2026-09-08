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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.texture_generation.domain.model.PatternTextureType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

internal fun generatePatternTexture(
    width: Int,
    height: Int,
    params: TextureParams,
    context: Context = appContext
): Bitmap {
    val source = createBitmap(width, height)
    return try {
        GPUImage(context).apply {
            setImage(source)
            setFilter(PatternTextureFilter(params))
        }.bitmapWithFilterApplied
    } finally {
        source.recycle()
    }
}

private class PatternTextureFilter(private val params: TextureParams) : GPUImageFilter(
    NO_FILTER_VERTEX_SHADER, patternShader(requireNotNull(params.textureFilterType.pattern))
) {
    override fun onInit() {
        super.onInit()
        val type = requireNotNull(params.textureFilterType.pattern)
        val p = params.patternParams
        fun scalar(name: String, value: Float) =
            setFloat(GLES20.glGetUniformLocation(program, name), value)
        type.resolve(p.values).forEach { (name, value) -> scalar(name, value) }
        scalar(
            "frequency",
            p.frequency.takeIf { it.isFinite() }?.coerceIn(0.1f, 64f) ?: type.defaultFrequency
        )
        scalar("offsetX", p.offsetX.takeIf { it.isFinite() }?.coerceIn(-1f, 1f) ?: 0f)
        scalar("offsetY", p.offsetY.takeIf { it.isFinite() }?.coerceIn(-1f, 1f) ?: 0f)
        scalar("rotation", p.rotation.takeIf { it.isFinite() }?.coerceIn(-180f, 180f) ?: 0f)
        repeat(4) { index ->
            val color = p.colors.getOrElse(index) { -1 }
            setFloatVec4(
                GLES20.glGetUniformLocation(program, "color${index + 1}"), floatArrayOf(
                    Color.red(color) / 255f, Color.green(color) / 255f,
                    Color.blue(color) / 255f, Color.alpha(color) / 255f
                )
            )
        }
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        setFloatVec2(
            GLES20.glGetUniformLocation(program, "aspect"), floatArrayOf(
                width.toFloat() / minOf(width, height), height.toFloat() / minOf(width, height)
            )
        )
    }
}