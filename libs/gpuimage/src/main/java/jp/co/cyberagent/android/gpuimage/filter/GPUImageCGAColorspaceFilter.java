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

public class GPUImageCGAColorspaceFilter extends GPUImageFilter {
    public static final String CGACOLORSPACE_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "highp vec2 sampleDivisor = vec2(1.0 / 200.0, 1.0 / 320.0);\n" +
            "//highp vec4 colorDivisor = vec4(colorDepth);\n" +
            "\n" +
            "highp vec2 samplePos = textureCoordinate - mod(textureCoordinate, sampleDivisor);\n" +
            "highp vec4 color = texture2D(inputImageTexture, samplePos );\n" +
            "\n" +
            "//gl_FragColor = texture2D(inputImageTexture, samplePos );\n" +
            "mediump vec4 colorCyan = vec4(85.0 / 255.0, 1.0, 1.0, 1.0);\n" +
            "mediump vec4 colorMagenta = vec4(1.0, 85.0 / 255.0, 1.0, 1.0);\n" +
            "mediump vec4 colorWhite = vec4(1.0, 1.0, 1.0, 1.0);\n" +
            "mediump vec4 colorBlack = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "\n" +
            "mediump vec4 endColor;\n" +
            "highp float blackDistance = distance(color, colorBlack);\n" +
            "highp float whiteDistance = distance(color, colorWhite);\n" +
            "highp float magentaDistance = distance(color, colorMagenta);\n" +
            "highp float cyanDistance = distance(color, colorCyan);\n" +
            "\n" +
            "mediump vec4 finalColor;\n" +
            "\n" +
            "highp float colorDistance = min(magentaDistance, cyanDistance);\n" +
            "colorDistance = min(colorDistance, whiteDistance);\n" +
            "colorDistance = min(colorDistance, blackDistance); \n" +
            "\n" +
            "if (colorDistance == blackDistance) {\n" +
            "finalColor = colorBlack;\n" +
            "} else if (colorDistance == whiteDistance) {\n" +
            "finalColor = colorWhite;\n" +
            "} else if (colorDistance == cyanDistance) {\n" +
            "finalColor = colorCyan;\n" +
            "} else {\n" +
            "finalColor = colorMagenta;\n" +
            "}\n" +
            "\n" +
            "gl_FragColor = finalColor;\n" +
            "}\n";

    public GPUImageCGAColorspaceFilter() {
        super(NO_FILTER_VERTEX_SHADER, CGACOLORSPACE_FRAGMENT_SHADER);
    }
}
