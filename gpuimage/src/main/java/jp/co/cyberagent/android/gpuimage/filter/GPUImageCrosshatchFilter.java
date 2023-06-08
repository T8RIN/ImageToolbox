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
 * crossHatchSpacing: The fractional width of the image to use as the spacing for the crosshatch. The default is 0.03.
 * lineWidth: A relative width for the crosshatch lines. The default is 0.003.
 */
public class GPUImageCrosshatchFilter extends GPUImageFilter {
    public static final String CROSSHATCH_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float crossHatchSpacing;\n" +
            "uniform highp float lineWidth;\n" +
            "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
            "void main()\n" +
            "{\n" +
            "highp float luminance = dot(texture2D(inputImageTexture, textureCoordinate).rgb, W);\n" +
            "lowp vec4 colorToDisplay = vec4(1.0, 1.0, 1.0, 1.0);\n" +
            "if (luminance < 1.00)\n" +
            "{\n" +
            "if (mod(textureCoordinate.x + textureCoordinate.y, crossHatchSpacing) <= lineWidth)\n" +
            "{\n" +
            "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "}\n" +
            "}\n" +
            "if (luminance < 0.75)\n" +
            "{\n" +
            "if (mod(textureCoordinate.x - textureCoordinate.y, crossHatchSpacing) <= lineWidth)\n" +
            "{\n" +
            "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "}\n" +
            "}\n" +
            "if (luminance < 0.50)\n" +
            "{\n" +
            "if (mod(textureCoordinate.x + textureCoordinate.y - (crossHatchSpacing / 2.0), crossHatchSpacing) <= lineWidth)\n" +
            "{\n" +
            "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "}\n" +
            "}\n" +
            "if (luminance < 0.3)\n" +
            "{\n" +
            "if (mod(textureCoordinate.x - textureCoordinate.y - (crossHatchSpacing / 2.0), crossHatchSpacing) <= lineWidth)\n" +
            "{\n" +
            "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "}\n" +
            "}\n" +
            "gl_FragColor = colorToDisplay;\n" +
            "}\n";

    private float crossHatchSpacing;
    private int crossHatchSpacingLocation;
    private float lineWidth;
    private int lineWidthLocation;

    /**
     * Using default values of crossHatchSpacing: 0.03f and lineWidth: 0.003f.
     */
    public GPUImageCrosshatchFilter() {
        this(0.03f, 0.003f);
    }

    public GPUImageCrosshatchFilter(float crossHatchSpacing, float lineWidth) {
        super(NO_FILTER_VERTEX_SHADER, CROSSHATCH_FRAGMENT_SHADER);
        this.crossHatchSpacing = crossHatchSpacing;
        this.lineWidth = lineWidth;
    }

    @Override
    public void onInit() {
        super.onInit();
        crossHatchSpacingLocation = GLES20.glGetUniformLocation(getProgram(), "crossHatchSpacing");
        lineWidthLocation = GLES20.glGetUniformLocation(getProgram(), "lineWidth");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setCrossHatchSpacing(crossHatchSpacing);
        setLineWidth(lineWidth);
    }

    /**
     * The fractional width of the image to use as the spacing for the crosshatch. The default is 0.03.
     *
     * @param crossHatchSpacing default 0.03
     */
    public void setCrossHatchSpacing(final float crossHatchSpacing) {
        float singlePixelSpacing;
        if (getOutputWidth() != 0) {
            singlePixelSpacing = 1.0f / (float) getOutputWidth();
        } else {
            singlePixelSpacing = 1.0f / 2048.0f;
        }

        if (crossHatchSpacing < singlePixelSpacing) {
            this.crossHatchSpacing = singlePixelSpacing;
        } else {
            this.crossHatchSpacing = crossHatchSpacing;
        }

        setFloat(crossHatchSpacingLocation, this.crossHatchSpacing);
    }

    /**
     * A relative width for the crosshatch lines. The default is 0.003.
     *
     * @param lineWidth default 0.003
     */
    public void setLineWidth(final float lineWidth) {
        this.lineWidth = lineWidth;
        setFloat(lineWidthLocation, this.lineWidth);
    }
}
