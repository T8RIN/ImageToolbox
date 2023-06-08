package jp.co.cyberagent.android.gpuimage.filter;

import android.opengl.GLES20;

public class GPUImageSolarizeFilter extends GPUImageFilter {
    public static final String SOLATIZE_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float threshold;\n" +
            "\n" +
            "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    highp float luminance = dot(textureColor.rgb, W);\n" +
            "    highp float thresholdResult = step(luminance, threshold);\n" +
            "    highp vec3 finalColor = abs(thresholdResult - textureColor.rgb);\n" +
            "    \n" +
            "    gl_FragColor = vec4(finalColor, textureColor.w);\n" +
            "}";

    private int uniformThresholdLocation;
    private float threshold;

    public GPUImageSolarizeFilter() {
        this(0.5f);
    }

    public GPUImageSolarizeFilter(float threshold) {
        super(NO_FILTER_VERTEX_SHADER, SOLATIZE_FRAGMENT_SHADER);
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
