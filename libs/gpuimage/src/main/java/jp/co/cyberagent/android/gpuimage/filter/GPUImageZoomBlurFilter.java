package jp.co.cyberagent.android.gpuimage.filter;

import android.graphics.PointF;
import android.opengl.GLES20;

public class GPUImageZoomBlurFilter extends GPUImageFilter {
    public static final String ZOOM_BLUR_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform highp vec2 blurCenter;\n" +
            "uniform highp float blurSize;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    // TODO: Do a more intelligent scaling based on resolution here\n" +
            "    highp vec2 samplingOffset = 1.0/100.0 * (blurCenter - textureCoordinate) * blurSize;\n" +
            "    \n" +
            "    lowp vec4 fragmentColor = texture2D(inputImageTexture, textureCoordinate) * 0.18;\n" +
            "    fragmentColor += texture2D(inputImageTexture, textureCoordinate + samplingOffset) * 0.15;\n" +
            "    fragmentColor += texture2D(inputImageTexture, textureCoordinate + (2.0 * samplingOffset)) *  0.12;\n" +
            "    fragmentColor += texture2D(inputImageTexture, textureCoordinate + (3.0 * samplingOffset)) * 0.09;\n" +
            "    fragmentColor += texture2D(inputImageTexture, textureCoordinate + (4.0 * samplingOffset)) * 0.05;\n" +
            "    fragmentColor += texture2D(inputImageTexture, textureCoordinate - samplingOffset) * 0.15;\n" +
            "    fragmentColor += texture2D(inputImageTexture, textureCoordinate - (2.0 * samplingOffset)) *  0.12;\n" +
            "    fragmentColor += texture2D(inputImageTexture, textureCoordinate - (3.0 * samplingOffset)) * 0.09;\n" +
            "    fragmentColor += texture2D(inputImageTexture, textureCoordinate - (4.0 * samplingOffset)) * 0.05;\n" +
            "    \n" +
            "    gl_FragColor = fragmentColor;\n" +
            "}\n";

    private PointF blurCenter;
    private int blurCenterLocation;
    private float blurSize;
    private int blurSizeLocation;

    public GPUImageZoomBlurFilter() {
        this(new PointF(0.5f, 0.5f), 1.0f);
    }

    public GPUImageZoomBlurFilter(PointF blurCenter, float blurSize) {
        super(NO_FILTER_VERTEX_SHADER, ZOOM_BLUR_FRAGMENT_SHADER);
        this.blurCenter = blurCenter;
        this.blurSize = blurSize;
    }

    @Override
    public void onInit() {
        super.onInit();
        blurCenterLocation = GLES20.glGetUniformLocation(getProgram(), "blurCenter");
        blurSizeLocation = GLES20.glGetUniformLocation(getProgram(), "blurSize");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setBlurCenter(blurCenter);
        setBlurSize(blurSize);
    }

    public void setBlurCenter(final PointF blurCenter) {
        this.blurCenter = blurCenter;
        setPoint(blurCenterLocation, blurCenter);
    }

    public void setBlurSize(final float blurSize) {
        this.blurSize = blurSize;
        setFloat(blurSizeLocation, blurSize);
    }
}
