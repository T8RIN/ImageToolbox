/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage.filter;

import android.opengl.GLES20;

/**
 * Adjusts the shadows and highlights of an image
 * shadows: Increase to lighten shadows, from 0.0 to 1.0, with 0.0 as the default.
 * highlights: Decrease to darken highlights, from 0.0 to 1.0, with 1.0 as the default.
 */
public class GPUImageHighlightShadowFilter extends GPUImageFilter {
    public static final String HIGHLIGHT_SHADOW_FRAGMENT_SHADER = "" +
            " uniform sampler2D inputImageTexture;\n" +
            " varying highp vec2 textureCoordinate;\n" +
            "  \n" +
            " uniform lowp float shadows;\n" +
            " uniform lowp float highlights;\n" +
            " \n" +
            " const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            " 	lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
            " 	mediump float luminance = dot(source.rgb, luminanceWeighting);\n" +
            " \n" +
            " 	mediump float shadow = clamp((pow(luminance, 1.0/(shadows+1.0)) + (-0.76)*pow(luminance, 2.0/(shadows+1.0))) - luminance, 0.0, 1.0);\n" +
            " 	mediump float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-highlights)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-highlights)))) - luminance, -1.0, 0.0);\n" +
            " 	lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
            " \n" +
            " 	gl_FragColor = vec4(result.rgb, source.a);\n" +
            " }";

    private int shadowsLocation;
    private float shadows;
    private int highlightsLocation;
    private float highlights;

    public GPUImageHighlightShadowFilter() {
        this(0.0f, 1.0f);
    }

    public GPUImageHighlightShadowFilter(final float shadows, final float highlights) {
        super(NO_FILTER_VERTEX_SHADER, HIGHLIGHT_SHADOW_FRAGMENT_SHADER);
        this.highlights = highlights;
        this.shadows = shadows;
    }

    @Override
    public void onInit() {
        super.onInit();
        highlightsLocation = GLES20.glGetUniformLocation(getProgram(), "highlights");
        shadowsLocation = GLES20.glGetUniformLocation(getProgram(), "shadows");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setHighlights(highlights);
        setShadows(shadows);
    }

    public void setHighlights(final float highlights) {
        this.highlights = highlights;
        setFloat(highlightsLocation, this.highlights);
    }

    public void setShadows(final float shadows) {
        this.shadows = shadows;
        setFloat(shadowsLocation, this.shadows);
    }
}
