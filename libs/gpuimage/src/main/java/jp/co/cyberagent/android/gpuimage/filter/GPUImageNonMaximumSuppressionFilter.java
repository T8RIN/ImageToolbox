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

public class GPUImageNonMaximumSuppressionFilter extends GPUImage3x3TextureSamplingFilter {
    public static final String NMS_FRAGMENT_SHADER = "" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "varying highp vec2 leftTextureCoordinate;\n" +
            "varying highp vec2 rightTextureCoordinate;\n" +
            "\n" +
            "varying highp vec2 topTextureCoordinate;\n" +
            "varying highp vec2 topLeftTextureCoordinate;\n" +
            "varying highp vec2 topRightTextureCoordinate;\n" +
            "\n" +
            "varying highp vec2 bottomTextureCoordinate;\n" +
            "varying highp vec2 bottomLeftTextureCoordinate;\n" +
            "varying highp vec2 bottomRightTextureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "lowp float bottomColor = texture2D(inputImageTexture, bottomTextureCoordinate).r;\n" +
            "lowp float bottomLeftColor = texture2D(inputImageTexture, bottomLeftTextureCoordinate).r;\n" +
            "lowp float bottomRightColor = texture2D(inputImageTexture, bottomRightTextureCoordinate).r;\n" +
            "lowp vec4 centerColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "lowp float leftColor = texture2D(inputImageTexture, leftTextureCoordinate).r;\n" +
            "lowp float rightColor = texture2D(inputImageTexture, rightTextureCoordinate).r;\n" +
            "lowp float topColor = texture2D(inputImageTexture, topTextureCoordinate).r;\n" +
            "lowp float topRightColor = texture2D(inputImageTexture, topRightTextureCoordinate).r;\n" +
            "lowp float topLeftColor = texture2D(inputImageTexture, topLeftTextureCoordinate).r;\n" +
            "\n" +
            "// Use a tiebreaker for pixels to the left and immediately above this one\n" +
            "lowp float multiplier = 1.0 - step(centerColor.r, topColor);\n" +
            "multiplier = multiplier * 1.0 - step(centerColor.r, topLeftColor);\n" +
            "multiplier = multiplier * 1.0 - step(centerColor.r, leftColor);\n" +
            "multiplier = multiplier * 1.0 - step(centerColor.r, bottomLeftColor);\n" +
            "\n" +
            "lowp float maxValue = max(centerColor.r, bottomColor);\n" +
            "maxValue = max(maxValue, bottomRightColor);\n" +
            "maxValue = max(maxValue, rightColor);\n" +
            "maxValue = max(maxValue, topRightColor);\n" +
            "\n" +
            "gl_FragColor = vec4((centerColor.rgb * step(maxValue, centerColor.r) * multiplier), 1.0);\n" +
            "}\n";

    public GPUImageNonMaximumSuppressionFilter() {
        super(NMS_FRAGMENT_SHADER);
    }
}
