package jp.co.cyberagent.android.gpuimage.filter;

import android.opengl.GLES20;

public class GPUImageVibranceFilter extends GPUImageFilter {
    public static final String VIBRANCE_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform lowp float vibrance;\n" +
            "\n" +
            "void main() {\n" +
            "    lowp vec4 color = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp float average = (color.r + color.g + color.b) / 3.0;\n" +
            "    lowp float mx = max(color.r, max(color.g, color.b));\n" +
            "    lowp float amt = (mx - average) * (-vibrance * 3.0);\n" +
            "    color.rgb = mix(color.rgb, vec3(mx), amt);\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private int vibranceLocation;
    private float vibrance;

    @Override
    public void onInit() {
        super.onInit();
        vibranceLocation = GLES20.glGetUniformLocation(getProgram(), "vibrance");
    }

    public GPUImageVibranceFilter() {
        this(0f);
    }

    public GPUImageVibranceFilter(float vibrance) {
        super(NO_FILTER_VERTEX_SHADER, VIBRANCE_FRAGMENT_SHADER);
        this.vibrance = vibrance;
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setVibrance(vibrance);
    }

    public void setVibrance(final float vibrance) {
        this.vibrance = vibrance;
        if (isInitialized()) {
            setFloat(vibranceLocation, vibrance);
        }
    }
}

