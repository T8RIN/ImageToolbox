/*
 *    Copyright 2018 Yizheng Huang
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.watermark.androidwm.bean;

import android.support.annotation.FloatRange;

/**
 * It's a class for saving the position of watermark.
 * Can be used for a single image/text or a set of
 * images/texts.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 * @since 29/08/2018
 */
public class WatermarkPosition {

    private double positionX;
    private double positionY;
    private double rotation;

    /**
     * Constructors for WatermarkImage
     */
    public WatermarkPosition(@FloatRange(from = 0, to = 1) double positionX,
                             @FloatRange(from = 0, to = 1) double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public WatermarkPosition(@FloatRange(from = 0, to = 1) double positionX,
                             @FloatRange(from = 0, to = 1) double positionY,
                             double rotation) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.rotation = rotation;
    }

    /**
     * Getters and Setters for those attrs.
     */
    public double getPositionX() {
        return positionX;
    }

    public WatermarkPosition setPositionX(double positionX) {
        this.positionX = positionX;
        return this;
    }

    public double getPositionY() {
        return positionY;
    }

    public WatermarkPosition setPositionY(double positionY) {
        this.positionY = positionY;
        return this;
    }

    public double getRotation() {
        return rotation;
    }

    public WatermarkPosition setRotation(double rotation) {
        this.rotation = rotation;
        return this;
    }
}
