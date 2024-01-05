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

public class GPUImageBulgeDistortionFilter extends GPUImageFilter {
    public static final String BULGE_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform highp float aspectRatio;\n" +
            "uniform highp vec2 center;\n" +
            "uniform highp float radius;\n" +
            "uniform highp float scale;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "highp float dist = distance(center, textureCoordinateToUse);\n" +
            "textureCoordinateToUse = textureCoordinate;\n" +
            "\n" +
            "if (dist < radius)\n" +
            "{\n" +
            "textureCoordinateToUse -= center;\n" +
            "highp float percent = 1.0 - ((radius - dist) / radius) * scale;\n" +
            "percent = percent * percent;\n" +
            "\n" +
            "textureCoordinateToUse = textureCoordinateToUse * percent;\n" +
            "textureCoordinateToUse += center;\n" +
            "}\n" +
            "\n" +
            "gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse );    \n" +
            "}\n";

    private float scale;
    private int scaleLocation;
    private float radius;
    private int radiusLocation;
    private PointF center;
    private int centerLocation;
    private float aspectRatio;
    private int aspectRatioLocation;

    public GPUImageBulgeDistortionFilter() {
        this(0.25f, 0.5f, new PointF(0.5f, 0.5f));
    }

    public GPUImageBulgeDistortionFilter(float radius, float scale, PointF center) {
        super(NO_FILTER_VERTEX_SHADER, BULGE_FRAGMENT_SHADER);
        this.radius = radius;
        this.scale = scale;
        this.center = center;
    }

    @Override
    public void onInit() {
        super.onInit();
        scaleLocation = GLES20.glGetUniformLocation(getProgram(), "scale");
        radiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
        centerLocation = GLES20.glGetUniformLocation(getProgram(), "center");
        aspectRatioLocation = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setAspectRatio(aspectRatio);
        setRadius(radius);
        setScale(scale);
        setCenter(center);
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        aspectRatio = (float) height / width;
        setAspectRatio(aspectRatio);
        super.onOutputSizeChanged(width, height);
    }

    private void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        setFloat(aspectRatioLocation, aspectRatio);
    }

    /**
     * The radius of the distortion, ranging from 0.0 to 1.0, with a default of 0.25
     *
     * @param radius from 0.0 to 1.0, default 0.25
     */
    public void setRadius(float radius) {
        this.radius = radius;
        setFloat(radiusLocation, radius);
    }

    /**
     * The amount of distortion to apply, from -1.0 to 1.0, with a default of 0.5
     *
     * @param scale from -1.0 to 1.0, default 0.5
     */
    public void setScale(float scale) {
        this.scale = scale;
        setFloat(scaleLocation, scale);
    }

    /**
     * The center about which to apply the distortion, with a default of (0.5, 0.5)
     *
     * @param center default (0.5, 0.5)
     */
    public void setCenter(PointF center) {
        this.center = center;
        setPoint(centerLocation, center);
    }
}
