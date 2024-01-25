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
package com.watermark.androidwm.task;

import android.graphics.Bitmap;

/**
 * This is a simple class that can help we get more than two kinds of
 * return values in the task.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
public class DetectionReturnValue {

    private Bitmap watermarkBitmap;
    private String watermarkString;

    public DetectionReturnValue() {

    }

    public DetectionReturnValue(Bitmap watermarkBitmap, String watermarkString) {
        this.watermarkBitmap = watermarkBitmap;
        this.watermarkString = watermarkString;
    }

    public Bitmap getWatermarkBitmap() {
        return watermarkBitmap;
    }

    protected void setWatermarkBitmap(Bitmap watermarkBitmap) {
        this.watermarkBitmap = watermarkBitmap;
    }

    public String getWatermarkString() {
        return watermarkString;
    }

    protected void setWatermarkString(String watermarkString) {
        this.watermarkString = watermarkString;
    }
}
