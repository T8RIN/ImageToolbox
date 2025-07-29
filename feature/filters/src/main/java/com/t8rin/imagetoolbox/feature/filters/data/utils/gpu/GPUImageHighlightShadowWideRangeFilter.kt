/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

internal class GPUImageHighlightShadowWideRangeFilter @JvmOverloads constructor(
    private var shadows: Float = 1.0f,
    private var highlights: Float = 1.0f
) :
    GPUImageFilter(NO_FILTER_VERTEX_SHADER, HIGHLIGHT_SHADOW_FRAGMENT_SHADER) {
    private var shadowsLocation = 0
    private var highlightsLocation = 0

    override fun onInit() {
        super.onInit()
        highlightsLocation = GLES20.glGetUniformLocation(program, "highlights")
        shadowsLocation = GLES20.glGetUniformLocation(program, "shadows")
    }

    override fun onInitialized() {
        super.onInitialized()
        setHighlights(highlights)
        setShadows(shadows)
    }

    fun setHighlights(highlights: Float) {
        this.highlights = highlights
        setFloat(highlightsLocation, this.highlights)
    }

    fun setShadows(shadows: Float) {
        this.shadows = shadows
        setFloat(shadowsLocation, this.shadows)
    }

    companion object {
        const val HIGHLIGHT_SHADOW_FRAGMENT_SHADER: String = "" +
                " uniform sampler2D inputImageTexture;\n" +
                " varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform lowp float shadows;\n" +
                " uniform lowp float highlights;\n" +
                " \n" +
                " const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "   lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
                "   mediump float luminance = dot(source.rgb, luminanceWeighting);\n" +
                " \n" +
                "   mediump float shadow = clamp((pow(luminance, 1.0/shadows) + (-0.76)*pow(luminance, 2.0/shadows)) - luminance, 0.0, 1.0);\n" +
                "   mediump float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-highlights)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-highlights)))) - luminance, -1.0, 0.0);\n" +
                "   lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
                " \n" +
                "   mediump float contrastedLuminance = ((luminance - 0.5) * 1.5) + 0.5;\n" +
                "   mediump float whiteInterp = contrastedLuminance*contrastedLuminance*contrastedLuminance;\n" +
                "   mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;\n" +
                "   result = mix(result, vec3(1.0), whiteInterp*whiteTarget);\n" +
                " \n" +
                "   mediump float invContrastedLuminance = 1.0 - contrastedLuminance;\n" +
                "   mediump float blackInterp = invContrastedLuminance*invContrastedLuminance*invContrastedLuminance;\n" +
                "   mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);\n" +
                "   result = mix(result, vec3(0.0), blackInterp*blackTarget);\n" +
                " \n" +
                "   gl_FragColor = vec4(result, source.a);\n" +
                " }"
    }
}