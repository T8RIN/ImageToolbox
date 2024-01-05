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
 * Kuwahara image abstraction, drawn from the work of Kyprianidis, et. al. in their publication
 * "Anisotropic Kuwahara Filtering on the GPU" within the GPU Pro collection. This produces an oil-painting-like
 * image, but it is extremely computationally expensive, so it can take seconds to render a frame on an iPad 2.
 * This might be best used for still images.
 */
public class GPUImageKuwaharaFilter extends GPUImageFilter {
    public static final String KUWAHARA_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform int radius;\n" +
            "\n" +
            "precision highp float;\n" +
            "\n" +
            "const vec2 src_size = vec2 (1.0 / 768.0, 1.0 / 1024.0);\n" +
            "\n" +
            "void main (void) \n" +
            "{\n" +
            "vec2 uv = textureCoordinate;\n" +
            "float n = float((radius + 1) * (radius + 1));\n" +
            "int i; int j;\n" +
            "vec3 m0 = vec3(0.0); vec3 m1 = vec3(0.0); vec3 m2 = vec3(0.0); vec3 m3 = vec3(0.0);\n" +
            "vec3 s0 = vec3(0.0); vec3 s1 = vec3(0.0); vec3 s2 = vec3(0.0); vec3 s3 = vec3(0.0);\n" +
            "vec3 c;\n" +
            "\n" +
            "for (j = -radius; j <= 0; ++j)  {\n" +
            "for (i = -radius; i <= 0; ++i)  {\n" +
            "c = texture2D(inputImageTexture, uv + vec2(i,j) * src_size).rgb;\n" +
            "m0 += c;\n" +
            "s0 += c * c;\n" +
            "}\n" +
            "}\n" +
            "\n" +
            "for (j = -radius; j <= 0; ++j)  {\n" +
            "for (i = 0; i <= radius; ++i)  {\n" +
            "c = texture2D(inputImageTexture, uv + vec2(i,j) * src_size).rgb;\n" +
            "m1 += c;\n" +
            "s1 += c * c;\n" +
            "}\n" +
            "}\n" +
            "\n" +
            "for (j = 0; j <= radius; ++j)  {\n" +
            "for (i = 0; i <= radius; ++i)  {\n" +
            "c = texture2D(inputImageTexture, uv + vec2(i,j) * src_size).rgb;\n" +
            "m2 += c;\n" +
            "s2 += c * c;\n" +
            "}\n" +
            "}\n" +
            "\n" +
            "for (j = 0; j <= radius; ++j)  {\n" +
            "for (i = -radius; i <= 0; ++i)  {\n" +
            "c = texture2D(inputImageTexture, uv + vec2(i,j) * src_size).rgb;\n" +
            "m3 += c;\n" +
            "s3 += c * c;\n" +
            "}\n" +
            "}\n" +
            "\n" +
            "\n" +
            "float min_sigma2 = 1e+2;\n" +
            "m0 /= n;\n" +
            "s0 = abs(s0 / n - m0 * m0);\n" +
            "\n" +
            "float sigma2 = s0.r + s0.g + s0.b;\n" +
            "if (sigma2 < min_sigma2) {\n" +
            "min_sigma2 = sigma2;\n" +
            "gl_FragColor = vec4(m0, 1.0);\n" +
            "}\n" +
            "\n" +
            "m1 /= n;\n" +
            "s1 = abs(s1 / n - m1 * m1);\n" +
            "\n" +
            "sigma2 = s1.r + s1.g + s1.b;\n" +
            "if (sigma2 < min_sigma2) {\n" +
            "min_sigma2 = sigma2;\n" +
            "gl_FragColor = vec4(m1, 1.0);\n" +
            "}\n" +
            "\n" +
            "m2 /= n;\n" +
            "s2 = abs(s2 / n - m2 * m2);\n" +
            "\n" +
            "sigma2 = s2.r + s2.g + s2.b;\n" +
            "if (sigma2 < min_sigma2) {\n" +
            "min_sigma2 = sigma2;\n" +
            "gl_FragColor = vec4(m2, 1.0);\n" +
            "}\n" +
            "\n" +
            "m3 /= n;\n" +
            "s3 = abs(s3 / n - m3 * m3);\n" +
            "\n" +
            "sigma2 = s3.r + s3.g + s3.b;\n" +
            "if (sigma2 < min_sigma2) {\n" +
            "min_sigma2 = sigma2;\n" +
            "gl_FragColor = vec4(m3, 1.0);\n" +
            "}\n" +
            "}\n";

    private int radius;
    private int radiusLocation;

    public GPUImageKuwaharaFilter() {
        this(3);
    }

    public GPUImageKuwaharaFilter(int radius) {
        super(NO_FILTER_VERTEX_SHADER, KUWAHARA_FRAGMENT_SHADER);
        this.radius = radius;
    }

    @Override
    public void onInit() {
        super.onInit();
        radiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setRadius(radius);
    }

    /**
     * The radius to sample from when creating the brush-stroke effect, with a default of 3.
     * The larger the radius, the slower the filter.
     *
     * @param radius default 3
     */
    public void setRadius(final int radius) {
        this.radius = radius;
        setInteger(radiusLocation, radius);
    }
}
