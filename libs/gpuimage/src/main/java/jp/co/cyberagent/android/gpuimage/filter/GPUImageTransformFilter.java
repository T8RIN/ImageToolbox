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
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GPUImageTransformFilter extends GPUImageFilter {
    public static final String TRANSFORM_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            " attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            " uniform mat4 transformMatrix;\n" +
            " uniform mat4 orthographicMatrix;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_Position = transformMatrix * vec4(position.xyz, 1.0) * orthographicMatrix;\n" +
            "     textureCoordinate = inputTextureCoordinate.xy;\n" +
            " }";
    private final float[] orthographicMatrix;
    private int transformMatrixUniform;
    private int orthographicMatrixUniform;
    private float[] transform3D;

    // This applies the transform to the raw frame data if set to YES, the default of NO takes the aspect ratio of the image input into account when rotating
    private boolean ignoreAspectRatio;

    // sets the anchor point to top left corner
    private boolean anchorTopLeft;

    public GPUImageTransformFilter() {
        super(TRANSFORM_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);

        orthographicMatrix = new float[16];
        Matrix.orthoM(orthographicMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);

        transform3D = new float[16];
        Matrix.setIdentityM(transform3D, 0);
    }

    @Override
    public void onInit() {
        super.onInit();
        transformMatrixUniform = GLES20.glGetUniformLocation(getProgram(), "transformMatrix");
        orthographicMatrixUniform = GLES20.glGetUniformLocation(getProgram(), "orthographicMatrix");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setUniformMatrix4f(transformMatrixUniform, transform3D);
        setUniformMatrix4f(orthographicMatrixUniform, orthographicMatrix);
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);

        if (!ignoreAspectRatio) {
            Matrix.orthoM(orthographicMatrix, 0, -1.0f, 1.0f, -1.0f * (float) height / (float) width, (float) height / (float) width, -1.0f, 1.0f);
            setUniformMatrix4f(orthographicMatrixUniform, orthographicMatrix);
        }
    }

    @Override
    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {

        FloatBuffer vertBuffer = cubeBuffer;

        if (!ignoreAspectRatio) {

            float[] adjustedVertices = new float[8];

            cubeBuffer.position(0);
            cubeBuffer.get(adjustedVertices);

            float normalizedHeight = (float) getOutputHeight() / (float) getOutputWidth();
            adjustedVertices[1] *= normalizedHeight;
            adjustedVertices[3] *= normalizedHeight;
            adjustedVertices[5] *= normalizedHeight;
            adjustedVertices[7] *= normalizedHeight;

            vertBuffer = ByteBuffer.allocateDirect(adjustedVertices.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            vertBuffer.put(adjustedVertices).position(0);
        }

        super.onDraw(textureId, vertBuffer, textureBuffer);
    }

    public float[] getTransform3D() {
        return transform3D;
    }

    public void setTransform3D(float[] transform3D) {
        this.transform3D = transform3D;
        setUniformMatrix4f(transformMatrixUniform, transform3D);
    }

    public void setIgnoreAspectRatio(boolean ignoreAspectRatio) {
        this.ignoreAspectRatio = ignoreAspectRatio;

        if (ignoreAspectRatio) {
            Matrix.orthoM(orthographicMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
            setUniformMatrix4f(orthographicMatrixUniform, orthographicMatrix);
        } else {
            onOutputSizeChanged(getOutputWidth(), getOutputHeight());
        }
    }

    public boolean ignoreAspectRatio() {
        return ignoreAspectRatio;
    }

    public void setAnchorTopLeft(boolean anchorTopLeft) {
        this.anchorTopLeft = anchorTopLeft;
        setIgnoreAspectRatio(ignoreAspectRatio);
    }

    public boolean anchorTopLeft() {
        return anchorTopLeft;
    }
}
