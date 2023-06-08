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
 * This equation is a simplification of the general blending equation. It assumes the destination color is opaque, and therefore drops the destination color's alpha term.
 * <p>
 * D = C1 * C1a + C2 * C2a * (1 - C1a)
 * where D is the resultant color, C1 is the color of the first element, C1a is the alpha of the first element, C2 is the second element color, C2a is the alpha of the second element. The destination alpha is calculated with:
 * <p>
 * Da = C1a + C2a * (1 - C1a)
 * The resultant color is premultiplied with the alpha. To restore the color to the unmultiplied values, just divide by Da, the resultant alpha.
 * <p>
 * http://stackoverflow.com/questions/1724946/blend-mode-on-a-transparent-and-semi-transparent-background
 * <p>
 * For some reason Photoshop behaves
 * D = C1 + C2 * C2a * (1 - C1a)
 */
public class GPUImageNormalBlendFilter extends GPUImageTwoInputFilter {
    public static final String NORMAL_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 c2 = texture2D(inputImageTexture, textureCoordinate);\n" +
            "\t lowp vec4 c1 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     \n" +
            "     lowp vec4 outputColor;\n" +
            "     \n" +
            "     outputColor.r = c1.r + c2.r * c2.a * (1.0 - c1.a);\n" +
            "\n" +
            "     outputColor.g = c1.g + c2.g * c2.a * (1.0 - c1.a);\n" +
            "     \n" +
            "     outputColor.b = c1.b + c2.b * c2.a * (1.0 - c1.a);\n" +
            "     \n" +
            "     outputColor.a = c1.a + c2.a * (1.0 - c1.a);\n" +
            "     \n" +
            "     gl_FragColor = outputColor;\n" +
            " }";

    public GPUImageNormalBlendFilter() {
        super(NORMAL_BLEND_FRAGMENT_SHADER);
    }
}
