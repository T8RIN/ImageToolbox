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
 * Applies a ColorMatrix to the image.
 */
public class GPUImageColorMatrixFilter extends GPUImageFilter {
    public static final String COLOR_MATRIX_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform lowp mat4 colorMatrix;\n" +
            "uniform lowp float intensity;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp vec4 outputColor = textureColor * colorMatrix;\n" +
            "    \n" +
            "    gl_FragColor = (intensity * outputColor) + ((1.0 - intensity) * textureColor);\n" +
            "}";

    private float intensity;
    private float[] colorMatrix;
    private int colorMatrixLocation;
    private int intensityLocation;

    public GPUImageColorMatrixFilter() {
        this(1.0f, new float[]{
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
    }

    public GPUImageColorMatrixFilter(final float intensity, final float[] colorMatrix) {
        super(NO_FILTER_VERTEX_SHADER, COLOR_MATRIX_FRAGMENT_SHADER);
        this.intensity = intensity;
        this.colorMatrix = colorMatrix;
    }

    @Override
    public void onInit() {
        super.onInit();
        colorMatrixLocation = GLES20.glGetUniformLocation(getProgram(), "colorMatrix");
        intensityLocation = GLES20.glGetUniformLocation(getProgram(), "intensity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setIntensity(intensity);
        setColorMatrix(colorMatrix);
    }

    public void setIntensity(final float intensity) {
        this.intensity = intensity;
        setFloat(intensityLocation, intensity);
    }

    public void setColorMatrix(final float[] colorMatrix) {
        this.colorMatrix = colorMatrix;
        setUniformMatrix4f(colorMatrixLocation, colorMatrix);
    }
}
