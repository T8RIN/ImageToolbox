package jp.co.cyberagent.android.gpuimage.filter;

/**
 * Applies sobel edge detection on the image.
 */
public class GPUImageThresholdEdgeDetectionFilter extends GPUImageFilterGroup {
    public GPUImageThresholdEdgeDetectionFilter() {
        super();
        addFilter(new GPUImageGrayscaleFilter());
        addFilter(new GPUImageSobelThresholdFilter());
    }

    public void setLineSize(final float size) {
        ((GPUImage3x3TextureSamplingFilter) getFilters().get(1)).setLineSize(size);
    }

    public void setThreshold(final float threshold) {
        ((GPUImageSobelThresholdFilter) getFilters().get(1)).setThreshold(threshold);
    }
}
