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

import android.graphics.PointF;
import android.opengl.GLES20;

/**
 * Creates a swirl distortion on the image.
 */
public class GPUImageSwirlFilter extends GPUImageFilter {
    public static final String SWIRL_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform highp vec2 center;\n" +
            "uniform highp float radius;\n" +
            "uniform highp float angle;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "highp vec2 textureCoordinateToUse = textureCoordinate;\n" +
            "highp float dist = distance(center, textureCoordinate);\n" +
            "if (dist < radius)\n" +
            "{\n" +
            "textureCoordinateToUse -= center;\n" +
            "highp float percent = (radius - dist) / radius;\n" +
            "highp float theta = percent * percent * angle * 8.0;\n" +
            "highp float s = sin(theta);\n" +
            "highp float c = cos(theta);\n" +
            "textureCoordinateToUse = vec2(dot(textureCoordinateToUse, vec2(c, -s)), dot(textureCoordinateToUse, vec2(s, c)));\n" +
            "textureCoordinateToUse += center;\n" +
            "}\n" +
            "\n" +
            "gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse );\n" +
            "\n" +
            "}\n";

    private float angle;
    private int angleLocation;
    private float radius;
    private int radiusLocation;
    private PointF center;
    private int centerLocation;

    public GPUImageSwirlFilter() {
        this(0.5f, 1.0f, new PointF(0.5f, 0.5f));
    }

    public GPUImageSwirlFilter(float radius, float angle, PointF center) {
        super(NO_FILTER_VERTEX_SHADER, SWIRL_FRAGMENT_SHADER);
        this.radius = radius;
        this.angle = angle;
        this.center = center;
    }

    @Override
    public void onInit() {
        super.onInit();
        angleLocation = GLES20.glGetUniformLocation(getProgram(), "angle");
        radiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
        centerLocation = GLES20.glGetUniformLocation(getProgram(), "center");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setRadius(radius);
        setAngle(angle);
        setCenter(center);
    }

    /**
     * The radius of the distortion, ranging from 0.0 to 1.0, with a default of 0.5.
     *
     * @param radius from 0.0 to 1.0, default 0.5
     */
    public void setRadius(float radius) {
        this.radius = radius;
        setFloat(radiusLocation, radius);
    }

    /**
     * The amount of distortion to apply, with a minimum of 0.0 and a default of 1.0.
     *
     * @param angle minimum 0.0, default 1.0
     */
    public void setAngle(float angle) {
        this.angle = angle;
        setFloat(angleLocation, angle);
    }

    /**
     * The center about which to apply the distortion, with a default of (0.5, 0.5).
     *
     * @param center default (0.5, 0.5)
     */
    public void setCenter(PointF center) {
        this.center = center;
        setPoint(centerLocation, center);
    }
}
