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

public class GPUImageAddBlendFilter extends GPUImageTwoInputFilter {
    public static final String ADD_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "   lowp vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   lowp vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "\n" +
            "   mediump float r;\n" +
            "   if (overlay.r * base.a + base.r * overlay.a >= overlay.a * base.a) {\n" +
            "     r = overlay.a * base.a + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     r = overlay.r + base.r;\n" +
            "   }\n" +
            "\n" +
            "   mediump float g;\n" +
            "   if (overlay.g * base.a + base.g * overlay.a >= overlay.a * base.a) {\n" +
            "     g = overlay.a * base.a + overlay.g * (1.0 - base.a) + base.g * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     g = overlay.g + base.g;\n" +
            "   }\n" +
            "\n" +
            "   mediump float b;\n" +
            "   if (overlay.b * base.a + base.b * overlay.a >= overlay.a * base.a) {\n" +
            "     b = overlay.a * base.a + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     b = overlay.b + base.b;\n" +
            "   }\n" +
            "\n" +
            "   mediump float a = overlay.a + base.a - overlay.a * base.a;\n" +
            "   \n" +
            "   gl_FragColor = vec4(r, g, b, a);\n" +
            " }";

    public GPUImageAddBlendFilter() {
        super(ADD_BLEND_FRAGMENT_SHADER);
    }
}
