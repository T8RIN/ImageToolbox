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

/**
 * This uses a similar process as the GPUImageToonFilter, only it precedes the toon effect
 * with a Gaussian blur to smooth out noise.
 */
public class GPUImageSmoothToonFilter extends GPUImageFilterGroup {

    private final GPUImageGaussianBlurFilter blurFilter;
    private final GPUImageToonFilter toonFilter;

    /**
     * Setup and Tear down
     */
    public GPUImageSmoothToonFilter() {
        // First pass: apply a variable Gaussian blur
        blurFilter = new GPUImageGaussianBlurFilter();
        addFilter(blurFilter);

        // Second pass: run the Sobel edge detection on this blurred image, along with a posterization effect
        toonFilter = new GPUImageToonFilter();
        addFilter(toonFilter);

        getFilters().add(blurFilter);
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setBlurSize(0.5f);
        setThreshold(0.2f);
        setQuantizationLevels(10.0f);
    }

    /**
     * Accessors
     */
    public void setTexelWidth(float value) {
        toonFilter.setTexelWidth(value);
    }

    public void setTexelHeight(float value) {
        toonFilter.setTexelHeight(value);
    }

    public void setBlurSize(float value) {
        blurFilter.setBlurSize(value);
    }

    public void setThreshold(float value) {
        toonFilter.setThreshold(value);
    }

    public void setQuantizationLevels(float value) {
        toonFilter.setQuantizationLevels(value);
    }

}
