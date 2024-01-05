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
 * saturation: The degree of saturation or desaturation to apply to the image (0.0 - 2.0, with 1.0 as the default)
 */
public class GPUImageSaturationFilter extends GPUImageFilter {
    public static final String SATURATION_FRAGMENT_SHADER = "" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float saturation;\n" +
            " \n" +
            " // Values from \"Graphics Shaders: Theory and Practice\" by Bailey and Cunningham\n" +
            " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "    lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "    \n" +
            "    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);\n" +
            "     \n" +
            " }";

    private int saturationLocation;
    private float saturation;

    public GPUImageSaturationFilter() {
        this(1.0f);
    }

    public GPUImageSaturationFilter(final float saturation) {
        super(NO_FILTER_VERTEX_SHADER, SATURATION_FRAGMENT_SHADER);
        this.saturation = saturation;
    }

    @Override
    public void onInit() {
        super.onInit();
        saturationLocation = GLES20.glGetUniformLocation(getProgram(), "saturation");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setSaturation(saturation);
    }

    public void setSaturation(final float saturation) {
        this.saturation = saturation;
        setFloat(saturationLocation, this.saturation);
    }
}
