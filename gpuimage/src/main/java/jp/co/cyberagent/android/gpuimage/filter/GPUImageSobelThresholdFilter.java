package jp.co.cyberagent.android.gpuimage.filter;

import android.opengl.GLES20;

public class GPUImageSobelThresholdFilter extends
        GPUImage3x3TextureSamplingFilter {
    public static final String SOBEL_THRESHOLD_EDGE_DETECTION = "" +
            "precision mediump float;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 leftTextureCoordinate;\n" +
            "varying vec2 rightTextureCoordinate;\n" +
            "\n" +
            "varying vec2 topTextureCoordinate;\n" +
            "varying vec2 topLeftTextureCoordinate;\n" +
            "varying vec2 topRightTextureCoordinate;\n" +
            "\n" +
            "varying vec2 bottomTextureCoordinate;\n" +
            "varying vec2 bottomLeftTextureCoordinate;\n" +
            "varying vec2 bottomRightTextureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform lowp float threshold;\n" +
            "\n" +
            "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    float bottomLeftIntensity = texture2D(inputImageTexture, bottomLeftTextureCoordinate).r;\n" +
            "    float topRightIntensity = texture2D(inputImageTexture, topRightTextureCoordinate).r;\n" +
            "    float topLeftIntensity = texture2D(inputImageTexture, topLeftTextureCoordinate).r;\n" +
            "    float bottomRightIntensity = texture2D(inputImageTexture, bottomRightTextureCoordinate).r;\n" +
            "    float leftIntensity = texture2D(inputImageTexture, leftTextureCoordinate).r;\n" +
            "    float rightIntensity = texture2D(inputImageTexture, rightTextureCoordinate).r;\n" +
            "    float bottomIntensity = texture2D(inputImageTexture, bottomTextureCoordinate).r;\n" +
            "    float topIntensity = texture2D(inputImageTexture, topTextureCoordinate).r;\n" +
            "    float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
            "    float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
            "\n" +
            "    float mag = 1.0 - length(vec2(h, v));\n" +
            "    mag = step(threshold, mag);\n" +
            "\n" +
            "    gl_FragColor = vec4(vec3(mag), 1.0);\n" +
            "}\n";

    private int uniformThresholdLocation;
    private float threshold;

    public GPUImageSobelThresholdFilter() {
        this(0.9f);
    }

    public GPUImageSobelThresholdFilter(float threshold) {
        super(SOBEL_THRESHOLD_EDGE_DETECTION);
        this.threshold = threshold;
    }

    @Override
    public void onInit() {
        super.onInit();
        uniformThresholdLocation = GLES20.glGetUniformLocation(getProgram(), "threshold");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setThreshold(threshold);
    }

    public void setThreshold(final float threshold) {
        this.threshold = threshold;
        setFloat(uniformThresholdLocation, threshold);
    }
}
