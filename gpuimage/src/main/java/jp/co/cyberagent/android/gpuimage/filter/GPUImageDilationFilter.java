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

/**
 * For each pixel, this sets it to the maximum value of the red channel in a rectangular neighborhood extending
 * out dilationRadius pixels from the center.
 * This extends out bright features, and is most commonly used with black-and-white thresholded images.
 */
public class GPUImageDilationFilter extends GPUImageTwoPassTextureSamplingFilter {
    public static final String VERTEX_SHADER_1 =
            "attribute vec4 position;\n" +
                    "attribute vec2 inputTextureCoordinate;\n" +
                    "\n" +
                    "uniform float texelWidthOffset; \n" +
                    "uniform float texelHeightOffset; \n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                    "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "gl_Position = position;\n" +
                    "\n" +
                    "vec2 offset = vec2(texelWidthOffset, texelHeightOffset);\n" +
                    "\n" +
                    "centerTextureCoordinate = inputTextureCoordinate;\n" +
                    "oneStepNegativeTextureCoordinate = inputTextureCoordinate - offset;\n" +
                    "oneStepPositiveTextureCoordinate = inputTextureCoordinate + offset;\n" +
                    "}\n";

    public static final String VERTEX_SHADER_2 =
            "attribute vec4 position;\n" +
                    "attribute vec2 inputTextureCoordinate;\n" +
                    "\n" +
                    "uniform float texelWidthOffset;\n" +
                    "uniform float texelHeightOffset;\n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                    "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                    "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "gl_Position = position;\n" +
                    "\n" +
                    "vec2 offset = vec2(texelWidthOffset, texelHeightOffset);\n" +
                    "\n" +
                    "centerTextureCoordinate = inputTextureCoordinate;\n" +
                    "oneStepNegativeTextureCoordinate = inputTextureCoordinate - offset;\n" +
                    "oneStepPositiveTextureCoordinate = inputTextureCoordinate + offset;\n" +
                    "twoStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 2.0);\n" +
                    "twoStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 2.0);\n" +
                    "}\n";

    public static final String VERTEX_SHADER_3 =
            "attribute vec4 position;\n" +
                    "attribute vec2 inputTextureCoordinate;\n" +
                    "\n" +
                    "uniform float texelWidthOffset;\n" +
                    "uniform float texelHeightOffset;\n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                    "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                    "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                    "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "gl_Position = position;\n" +
                    "\n" +
                    "vec2 offset = vec2(texelWidthOffset, texelHeightOffset);\n" +
                    "\n" +
                    "centerTextureCoordinate = inputTextureCoordinate;\n" +
                    "oneStepNegativeTextureCoordinate = inputTextureCoordinate - offset;\n" +
                    "oneStepPositiveTextureCoordinate = inputTextureCoordinate + offset;\n" +
                    "twoStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 2.0);\n" +
                    "twoStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 2.0);\n" +
                    "threeStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 3.0);\n" +
                    "threeStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 3.0);\n" +
                    "}\n";

    public static final String VERTEX_SHADER_4 =

            "attribute vec4 position;\n" +
                    "attribute vec2 inputTextureCoordinate;\n" +
                    "\n" +
                    "uniform float texelWidthOffset;\n" +
                    "uniform float texelHeightOffset;\n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                    "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                    "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                    "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
                    "varying vec2 fourStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 fourStepsNegativeTextureCoordinate;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "gl_Position = position;\n" +
                    "\n" +
                    "vec2 offset = vec2(texelWidthOffset, texelHeightOffset);\n" +
                    "\n" +
                    "centerTextureCoordinate = inputTextureCoordinate;\n" +
                    "oneStepNegativeTextureCoordinate = inputTextureCoordinate - offset;\n" +
                    "oneStepPositiveTextureCoordinate = inputTextureCoordinate + offset;\n" +
                    "twoStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 2.0);\n" +
                    "twoStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 2.0);\n" +
                    "threeStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 3.0);\n" +
                    "threeStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 3.0);\n" +
                    "fourStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 4.0);\n" +
                    "fourStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 4.0);\n" +
                    "}\n";


    public static final String FRAGMENT_SHADER_1 =
            "precision lowp float;\n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                    "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                    "\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "float centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate).r;\n" +
                    "float oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate).r;\n" +
                    "float oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate).r;\n" +
                    "\n" +
                    "lowp float maxValue = max(centerIntensity, oneStepPositiveIntensity);\n" +
                    "maxValue = max(maxValue, oneStepNegativeIntensity);\n" +
                    "\n" +
                    "gl_FragColor = vec4(vec3(maxValue), 1.0);\n" +
                    "}\n";

    public static final String FRAGMENT_SHADER_2 =
            "precision lowp float;\n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                    "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                    "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                    "\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "float centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate).r;\n" +
                    "float oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate).r;\n" +
                    "float oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate).r;\n" +
                    "float twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate).r;\n" +
                    "float twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate).r;\n" +
                    "\n" +
                    "lowp float maxValue = max(centerIntensity, oneStepPositiveIntensity);\n" +
                    "maxValue = max(maxValue, oneStepNegativeIntensity);\n" +
                    "maxValue = max(maxValue, twoStepsPositiveIntensity);\n" +
                    "maxValue = max(maxValue, twoStepsNegativeIntensity);\n" +
                    "\n" +
                    "gl_FragColor = vec4(vec3(maxValue), 1.0);\n" +
                    "}\n";

    public static final String FRAGMENT_SHADER_3 =
            "precision lowp float;\n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                    "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                    "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                    "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
                    "\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "float centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate).r;\n" +
                    "float oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate).r;\n" +
                    "float oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate).r;\n" +
                    "float twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate).r;\n" +
                    "float twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate).r;\n" +
                    "float threeStepsPositiveIntensity = texture2D(inputImageTexture, threeStepsPositiveTextureCoordinate).r;\n" +
                    "float threeStepsNegativeIntensity = texture2D(inputImageTexture, threeStepsNegativeTextureCoordinate).r;\n" +
                    "\n" +
                    "lowp float maxValue = max(centerIntensity, oneStepPositiveIntensity);\n" +
                    "maxValue = max(maxValue, oneStepNegativeIntensity);\n" +
                    "maxValue = max(maxValue, twoStepsPositiveIntensity);\n" +
                    "maxValue = max(maxValue, twoStepsNegativeIntensity);\n" +
                    "maxValue = max(maxValue, threeStepsPositiveIntensity);\n" +
                    "maxValue = max(maxValue, threeStepsNegativeIntensity);\n" +
                    "\n" +
                    "gl_FragColor = vec4(vec3(maxValue), 1.0);\n" +
                    "}\n";

    public static final String FRAGMENT_SHADER_4 =
            "precision lowp float;\n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                    "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                    "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                    "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
                    "varying vec2 fourStepsPositiveTextureCoordinate;\n" +
                    "varying vec2 fourStepsNegativeTextureCoordinate;\n" +
                    "\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "float centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate).r;\n" +
                    "float oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate).r;\n" +
                    "float oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate).r;\n" +
                    "float twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate).r;\n" +
                    "float twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate).r;\n" +
                    "float threeStepsPositiveIntensity = texture2D(inputImageTexture, threeStepsPositiveTextureCoordinate).r;\n" +
                    "float threeStepsNegativeIntensity = texture2D(inputImageTexture, threeStepsNegativeTextureCoordinate).r;\n" +
                    "float fourStepsPositiveIntensity = texture2D(inputImageTexture, fourStepsPositiveTextureCoordinate).r;\n" +
                    "float fourStepsNegativeIntensity = texture2D(inputImageTexture, fourStepsNegativeTextureCoordinate).r;\n" +
                    "\n" +
                    "lowp float maxValue = max(centerIntensity, oneStepPositiveIntensity);\n" +
                    "maxValue = max(maxValue, oneStepNegativeIntensity);\n" +
                    "maxValue = max(maxValue, twoStepsPositiveIntensity);\n" +
                    "maxValue = max(maxValue, twoStepsNegativeIntensity);\n" +
                    "maxValue = max(maxValue, threeStepsPositiveIntensity);\n" +
                    "maxValue = max(maxValue, threeStepsNegativeIntensity);\n" +
                    "maxValue = max(maxValue, fourStepsPositiveIntensity);\n" +
                    "maxValue = max(maxValue, fourStepsNegativeIntensity);\n" +
                    "\n" +
                    "gl_FragColor = vec4(vec3(maxValue), 1.0);\n" +
                    "}\n";


    public GPUImageDilationFilter() {
        this(1);
    }

    /**
     * Acceptable values for dilationRadius, which sets the distance in pixels to sample out from the center,
     * are 1, 2, 3, and 4.
     *
     * @param radius 1, 2, 3 or 4
     */
    public GPUImageDilationFilter(int radius) {
        this(getVertexShader(radius), getFragmentShader(radius));
    }

    private GPUImageDilationFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader, vertexShader, fragmentShader);
    }

    private static String getVertexShader(int radius) {
        switch (radius) {
            case 0:
            case 1:
                return VERTEX_SHADER_1;
            case 2:
                return VERTEX_SHADER_2;
            case 3:
                return VERTEX_SHADER_3;
            default:
                return VERTEX_SHADER_4;
        }
    }

    private static String getFragmentShader(int radius) {
        switch (radius) {
            case 0:
            case 1:
                return FRAGMENT_SHADER_1;
            case 2:
                return FRAGMENT_SHADER_2;
            case 3:
                return FRAGMENT_SHADER_3;
            default:
                return FRAGMENT_SHADER_4;
        }
    }
}
