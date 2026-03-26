/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smarttoolfactory.extendedcolors.md3.hct;

import com.smarttoolfactory.extendedcolors.md3.utils.ColorUtils;


/**
 * A color system built using CAM16 hue and chroma, and L* from L*a*b*.
 *
 * <p>Using L* creates a link between the color system, contrast, and thus accessibility. Contrast
 * ratio depends on relative luminance, or Y in the XYZ color space. L*, or perceptual luminance can
 * be calculated from Y.
 *
 * <p>Unlike Y, L* is linear to human perception, allowing trivial creation of accurate color tones.
 *
 * <p>Unlike contrast ratio, measuring contrast in L* is linear, and simple to calculate. A
 * difference of 40 in HCT tone guarantees a contrast ratio >= 3.0, and a difference of 50
 * guarantees a contrast ratio >= 4.5.
 */


/**
 * HCT, hue, chroma, and tone. A color system that provides a perceptually accurate color
 * measurement system that can also accurately render what colors will appear as in different
 * lighting environments.
 */
public final class Hct {
    private double hue;
    private double chroma;
    private double tone;
    private int argb;

    private Hct(int argb) {
        setInternalState(argb);
    }

    /**
     * Create an HCT color from hue, chroma, and tone.
     *
     * @param hue    0 <= hue < 360; invalid values are corrected.
     * @param chroma 0 <= chroma < ?; Informally, colorfulness. The color returned may be lower than
     *               the requested chroma. Chroma has a different maximum for any given hue and tone.
     * @param tone   0 <= tone <= 100; invalid values are corrected.
     * @return HCT representation of a color in default viewing conditions.
     */
    public static Hct from(double hue, double chroma, double tone) {
        int argb = CamSolver.solveToInt(hue, chroma, tone);
        return new Hct(argb);
    }

    /**
     * Create an HCT color from a color.
     *
     * @param argb ARGB representation of a color.
     * @return HCT representation of a color in default viewing conditions
     */
    public static Hct fromInt(int argb) {
        return new Hct(argb);
    }

    public double getHue() {
        return hue;
    }

    /**
     * Set the hue of this color. Chroma may decrease because chroma has a different maximum for any
     * given hue and tone.
     *
     * @param newHue 0 <= newHue < 360; invalid values are corrected.
     */
    public void setHue(double newHue) {
        setInternalState(CamSolver.solveToInt(newHue, chroma, tone));
    }

    public double getChroma() {
        return chroma;
    }

    /**
     * Set the chroma of this color. Chroma may decrease because chroma has a different maximum for
     * any given hue and tone.
     *
     * @param newChroma 0 <= newChroma < ?
     */
    public void setChroma(double newChroma) {
        setInternalState(CamSolver.solveToInt(hue, newChroma, tone));
    }

    public double getTone() {
        return tone;
    }

    /**
     * Set the tone of this color. Chroma may decrease because chroma has a different maximum for any
     * given hue and tone.
     *
     * @param newTone 0 <= newTone <= 100; invalid valids are corrected.
     */
    public void setTone(double newTone) {
        setInternalState(CamSolver.solveToInt(hue, chroma, newTone));
    }

    public int toInt() {
        return argb;
    }

    private void setInternalState(int argb) {
        this.argb = argb;
        Cam16 cam = Cam16.fromInt(argb);
        hue = cam.getHue();
        chroma = cam.getChroma();
        this.tone = ColorUtils.lstarFromArgb(argb);
    }
}
