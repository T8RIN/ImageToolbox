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
 * Adjusts the alpha channel of the incoming image
 * opacity: The value to multiply the incoming alpha channel for each pixel by (0.0 - 1.0, with 1.0 as the default)
 */
public class GPUImageOpacityFilter extends GPUImageFilter {
    public static final String OPACITY_FRAGMENT_SHADER = "" +
            "  varying highp vec2 textureCoordinate;\n" +
            "  \n" +
            "  uniform sampler2D inputImageTexture;\n" +
            "  uniform lowp float opacity;\n" +
            "  \n" +
            "  void main()\n" +
            "  {\n" +
            "      lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "      \n" +
            "      gl_FragColor = vec4(textureColor.rgb, textureColor.a * opacity);\n" +
            "  }\n";

    private int opacityLocation;
    private float opacity;

    public GPUImageOpacityFilter() {
        this(1.0f);
    }

    public GPUImageOpacityFilter(final float opacity) {
        super(NO_FILTER_VERTEX_SHADER, OPACITY_FRAGMENT_SHADER);
        this.opacity = opacity;
    }

    @Override
    public void onInit() {
        super.onInit();
        opacityLocation = GLES20.glGetUniformLocation(getProgram(), "opacity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setOpacity(opacity);
    }

    public void setOpacity(final float opacity) {
        this.opacity = opacity;
        setFloat(opacityLocation, this.opacity);
    }
}
