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
 * Applies an emboss effect to the image.<br>
 * <br>
 * Intensity ranges from 0.0 to 4.0, with 1.0 as the normal level
 */
public class GPUImageEmbossFilter extends GPUImage3x3ConvolutionFilter {
    private float intensity;

    public GPUImageEmbossFilter() {
        this(1.0f);
    }

    public GPUImageEmbossFilter(final float intensity) {
        super();
        this.intensity = intensity;
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setIntensity(intensity);
    }

    public void setIntensity(final float intensity) {
        this.intensity = intensity;
        setConvolutionKernel(new float[]{
                intensity * (-2.0f), -intensity, 0.0f,
                -intensity, 1.0f, intensity,
                0.0f, intensity, intensity * 2.0f,
        });
    }

    public float getIntensity() {
        return intensity;
    }
}
