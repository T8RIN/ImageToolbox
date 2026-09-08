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

package com.t8rin.imagetoolbox.feature.filters.data.utils.gpu

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

internal abstract class BitmapGpuImageFilter(
    fragmentShader: String
) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, fragmentShader) {

    private var sourceSizeLocation = -1
    private var storageSizeLocation = -1

    override fun onInit() {
        super.onInit()
        sourceSizeLocation = GLES20.glGetUniformLocation(program, "sourceSize")
        storageSizeLocation = GLES20.glGetUniformLocation(program, "sourceStorageSize")
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        setFloatVec2(sourceSizeLocation, floatArrayOf(width.toFloat(), height.toFloat()))
        // GPUImageRenderer appends a transparent column when the bitmap width is odd.
        setFloatVec2(
            storageSizeLocation,
            floatArrayOf((width + width % 2).toFloat(), height.toFloat())
        )
    }
}

internal const val BITMAP_SAMPLING_SHADER = """
    uniform vec2 sourceSize;
    uniform vec2 sourceStorageSize;

    vec4 sampleInput(vec2 uv) {
        vec2 halfPixel = vec2(0.5) / sourceSize;
        vec2 logicalUv = clamp(uv, halfPixel, vec2(1.0) - halfPixel);
        return texture2D(inputImageTexture, logicalUv * sourceSize / sourceStorageSize);
    }
"""