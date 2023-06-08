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

public class GPUImageHueBlendFilter extends GPUImageTwoInputFilter {
    public static final String HUE_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " highp float lum(lowp vec3 c) {\n" +
            "     return dot(c, vec3(0.3, 0.59, 0.11));\n" +
            " }\n" +
            " \n" +
            " lowp vec3 clipcolor(lowp vec3 c) {\n" +
            "     highp float l = lum(c);\n" +
            "     lowp float n = min(min(c.r, c.g), c.b);\n" +
            "     lowp float x = max(max(c.r, c.g), c.b);\n" +
            "     \n" +
            "     if (n < 0.0) {\n" +
            "         c.r = l + ((c.r - l) * l) / (l - n);\n" +
            "         c.g = l + ((c.g - l) * l) / (l - n);\n" +
            "         c.b = l + ((c.b - l) * l) / (l - n);\n" +
            "     }\n" +
            "     if (x > 1.0) {\n" +
            "         c.r = l + ((c.r - l) * (1.0 - l)) / (x - l);\n" +
            "         c.g = l + ((c.g - l) * (1.0 - l)) / (x - l);\n" +
            "         c.b = l + ((c.b - l) * (1.0 - l)) / (x - l);\n" +
            "     }\n" +
            "     \n" +
            "     return c;\n" +
            " }\n" +
            " \n" +
            " lowp vec3 setlum(lowp vec3 c, highp float l) {\n" +
            "     highp float d = l - lum(c);\n" +
            "     c = c + vec3(d);\n" +
            "     return clipcolor(c);\n" +
            " }\n" +
            " \n" +
            " highp float sat(lowp vec3 c) {\n" +
            "     lowp float n = min(min(c.r, c.g), c.b);\n" +
            "     lowp float x = max(max(c.r, c.g), c.b);\n" +
            "     return x - n;\n" +
            " }\n" +
            " \n" +
            " lowp float mid(lowp float cmin, lowp float cmid, lowp float cmax, highp float s) {\n" +
            "     return ((cmid - cmin) * s) / (cmax - cmin);\n" +
            " }\n" +
            " \n" +
            " lowp vec3 setsat(lowp vec3 c, highp float s) {\n" +
            "     if (c.r > c.g) {\n" +
            "         if (c.r > c.b) {\n" +
            "             if (c.g > c.b) {\n" +
            "                 /* g is mid, b is min */\n" +
            "                 c.g = mid(c.b, c.g, c.r, s);\n" +
            "                 c.b = 0.0;\n" +
            "             } else {\n" +
            "                 /* b is mid, g is min */\n" +
            "                 c.b = mid(c.g, c.b, c.r, s);\n" +
            "                 c.g = 0.0;\n" +
            "             }\n" +
            "             c.r = s;\n" +
            "         } else {\n" +
            "             /* b is max, r is mid, g is min */\n" +
            "             c.r = mid(c.g, c.r, c.b, s);\n" +
            "             c.b = s;\n" +
            "             c.r = 0.0;\n" +
            "         }\n" +
            "     } else if (c.r > c.b) {\n" +
            "         /* g is max, r is mid, b is min */\n" +
            "         c.r = mid(c.b, c.r, c.g, s);\n" +
            "         c.g = s;\n" +
            "         c.b = 0.0;\n" +
            "     } else if (c.g > c.b) {\n" +
            "         /* g is max, b is mid, r is min */\n" +
            "         c.b = mid(c.r, c.b, c.g, s);\n" +
            "         c.g = s;\n" +
            "         c.r = 0.0;\n" +
            "     } else if (c.b > c.g) {\n" +
            "         /* b is max, g is mid, r is min */\n" +
            "         c.g = mid(c.r, c.g, c.b, s);\n" +
            "         c.b = s;\n" +
            "         c.r = 0.0;\n" +
            "     } else {\n" +
            "         c = vec3(0.0);\n" +
            "     }\n" +
            "     return c;\n" +
            " }\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "   highp vec4 baseColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   highp vec4 overlayColor = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     \n" +
            "     gl_FragColor = vec4(baseColor.rgb * (1.0 - overlayColor.a) + setlum(setsat(overlayColor.rgb, sat(baseColor.rgb)), lum(baseColor.rgb)) * overlayColor.a, baseColor.a);\n" +
            " }";

    public GPUImageHueBlendFilter() {
        super(HUE_BLEND_FRAGMENT_SHADER);
    }
}
