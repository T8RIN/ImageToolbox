package jp.co.cyberagent.android.gpuimage.filter;

import android.opengl.GLES20;

/**
 * Created by vashisthg 30/05/14.
 */
public class GPUImageLevelsFilter extends GPUImageFilter {

    private static final String LOGTAG = GPUImageLevelsFilter.class.getSimpleName();

    public static final String LEVELS_FRAGMET_SHADER =

            " varying highp vec2 textureCoordinate;\n" +
                    " \n" +
                    " uniform sampler2D inputImageTexture;\n" +
                    " uniform mediump vec3 levelMinimum;\n" +
                    " uniform mediump vec3 levelMiddle;\n" +
                    " uniform mediump vec3 levelMaximum;\n" +
                    " uniform mediump vec3 minOutput;\n" +
                    " uniform mediump vec3 maxOutput;\n" +
                    " \n" +
                    " void main()\n" +
                    " {\n" +
                    "     mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "     \n" +
                    "     gl_FragColor = vec4( mix(minOutput, maxOutput, pow(min(max(textureColor.rgb -levelMinimum, vec3(0.0)) / (levelMaximum - levelMinimum  ), vec3(1.0)), 1.0 /levelMiddle)) , textureColor.a);\n" +
                    " }\n";

    private int minLocation;
    private final float[] min;
    private int midLocation;
    private final float[] mid;
    private int maxLocation;
    private final float[] max;
    private int minOutputLocation;
    private final float[] minOutput;
    private int maxOutputLocation;
    private final float[] maxOutput;

    public GPUImageLevelsFilter() {
        this(new float[]{0.0f, 0.0f, 0.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 0.0f}, new float[]{1.0f, 1.0f, 1.0f});
    }

    private GPUImageLevelsFilter(final float[] min, final float[] mid, final float[] max, final float[] minOUt, final float[] maxOut) {
        super(NO_FILTER_VERTEX_SHADER, LEVELS_FRAGMET_SHADER);

        this.min = min;
        this.mid = mid;
        this.max = max;
        minOutput = minOUt;
        maxOutput = maxOut;
    }

    @Override
    public void onInit() {
        super.onInit();
        minLocation = GLES20.glGetUniformLocation(getProgram(), "levelMinimum");
        midLocation = GLES20.glGetUniformLocation(getProgram(), "levelMiddle");
        maxLocation = GLES20.glGetUniformLocation(getProgram(), "levelMaximum");
        minOutputLocation = GLES20.glGetUniformLocation(getProgram(), "minOutput");
        maxOutputLocation = GLES20.glGetUniformLocation(getProgram(), "maxOutput");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setMin(0.0f, 1.0f, 1.0f, 0.0f, 1.0f);
        updateUniforms();
    }


    public void updateUniforms() {
        setFloatVec3(minLocation, min);
        setFloatVec3(midLocation, mid);
        setFloatVec3(maxLocation, max);
        setFloatVec3(minOutputLocation, minOutput);
        setFloatVec3(maxOutputLocation, maxOutput);
    }

    public void setMin(float min, float mid, float max, float minOut, float maxOut) {
        setRedMin(min, mid, max, minOut, maxOut);
        setGreenMin(min, mid, max, minOut, maxOut);
        setBlueMin(min, mid, max, minOut, maxOut);
    }

    public void setMin(float min, float mid, float max) {
        setMin(min, mid, max, 0.0f, 1.0f);
    }

    public void setRedMin(float min, float mid, float max, float minOut, float maxOut) {
        this.min[0] = min;
        this.mid[0] = mid;
        this.max[0] = max;
        minOutput[0] = minOut;
        maxOutput[0] = maxOut;
        updateUniforms();
    }

    public void setRedMin(float min, float mid, float max) {
        setRedMin(min, mid, max, 0, 1);
    }

    public void setGreenMin(float min, float mid, float max, float minOut, float maxOut) {
        this.min[1] = min;
        this.mid[1] = mid;
        this.max[1] = max;
        minOutput[1] = minOut;
        maxOutput[1] = maxOut;
        updateUniforms();
    }

    public void setGreenMin(float min, float mid, float max) {
        setGreenMin(min, mid, max, 0, 1);
    }

    public void setBlueMin(float min, float mid, float max, float minOut, float maxOut) {
        this.min[2] = min;
        this.mid[2] = mid;
        this.max[2] = max;
        minOutput[2] = minOut;
        maxOutput[2] = maxOut;
        updateUniforms();
    }

    public void setBlueMin(float min, float mid, float max) {
        setBlueMin(min, mid, max, 0, 1);
    }
}
