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


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import com.watermark.androidwm.bean.WatermarkText;
import com.watermark.androidwm.listener.BuildFinishListener;
import com.watermark.androidwm.bean.AsyncTaskParams;
import com.watermark.androidwm.utils.FastDctFft;

import static com.watermark.androidwm.utils.BitmapUtils.pixel2ARGBArray;
import static com.watermark.androidwm.utils.BitmapUtils.getBitmapPixels;
import static com.watermark.androidwm.utils.BitmapUtils.textAsBitmap;
import static com.watermark.androidwm.utils.Constant.ERROR_CREATE_FAILED;
import static com.watermark.androidwm.utils.Constant.ERROR_NO_BACKGROUND;
import static com.watermark.androidwm.utils.Constant.ERROR_NO_WATERMARKS;
import static com.watermark.androidwm.utils.Constant.ERROR_PIXELS_NOT_ENOUGH;
import static com.watermark.androidwm.utils.StringUtils.copyFromIntArray;

/**
 * This is a tack that use Fast Fourier Transform for an image, to
 * build the image and text watermark into a frequency domain.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
public class FDWatermarkTask extends AsyncTask<AsyncTaskParams, Void, Bitmap> {

    private BuildFinishListener<Bitmap> listener;

    public FDWatermarkTask(BuildFinishListener<Bitmap> callback) {
        this.listener = callback;
    }

    @Override
    protected Bitmap doInBackground(AsyncTaskParams... params) {
        Bitmap backgroundBitmap = params[0].getBackgroundImg();
        WatermarkText watermarkText = params[0].getWatermarkText();
        Bitmap watermarkBitmap = params[0].getWatermarkImg();
        Context context = params[0].getContext();

        if (backgroundBitmap == null) {
            listener.onFailure(ERROR_NO_BACKGROUND);
            return null;
        }

        if (watermarkText != null) {
            watermarkBitmap = textAsBitmap(context, watermarkText);
        }

        if (watermarkBitmap == null) {
            listener.onFailure(ERROR_NO_WATERMARKS);
            return null;
        }

        int[] watermarkPixels = getBitmapPixels(watermarkBitmap);
        int[] watermarkColorArray = pixel2ARGBArray(watermarkPixels);
        Bitmap outputBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(),
                backgroundBitmap.getConfig());

        // convert the background bitmap into pixel array.
        int[] backgroundPixels = getBitmapPixels(backgroundBitmap);

        if (watermarkColorArray.length > backgroundPixels.length * 4) {
            listener.onFailure(ERROR_PIXELS_NOT_ENOUGH);
        } else {
            // divide and conquer
            // use fixed chunk size or the size of watermark image.
            if (backgroundPixels.length < watermarkColorArray.length) {
                int[] backgroundColorArray = pixel2ARGBArray(backgroundPixels);
                double[] backgroundColorArrayD = copyFromIntArray(backgroundColorArray);

                FastDctFft.transform(backgroundColorArrayD);

                //TODO: do the operations.

                FastDctFft.inverseTransform(backgroundColorArrayD);
                for (int i = 0; i < backgroundPixels.length; i++) {
                    int color = Color.argb(
                            (int) backgroundColorArrayD[4 * i],
                            (int) backgroundColorArrayD[4 * i + 1],
                            (int) backgroundColorArrayD[4 * i + 2],
                            (int) backgroundColorArrayD[4 * i + 3]
                    );

                    backgroundPixels[i] = color;
                }
            } else {
                // use fixed chunk size or the size of watermark image.
                int numOfChunks = (int) Math.ceil((double) backgroundPixels.length / watermarkColorArray.length);
                for (int i = 0; i < numOfChunks; i++) {
                    int start = i * watermarkColorArray.length;
                    int length = Math.min(backgroundPixels.length - start, watermarkColorArray.length);
                    int[] temp = new int[length];
                    System.arraycopy(backgroundPixels, start, temp, 0, length);
                    double[] colorTempD = copyFromIntArray(pixel2ARGBArray(temp));
                    FastDctFft.transform(colorTempD);

//                    for (int j = 0; j < length; j++) {
//                        colorTempD[4 * j] = colorTempD[4 * j] + watermarkColorArray[j];
//                        colorTempD[4 * j + 1] = colorTempD[4 * j + 1] + watermarkColorArray[j];
//                        colorTempD[4 * j + 2] = colorTempD[4 * j + 2] + watermarkColorArray[j];
//                        colorTempD[4 * j + 3] = colorTempD[4 * j + 3] + watermarkColorArray[j];
//                    }

                    double enhanceNum = 1;

                    // The energy in frequency scaled.
                    for (int j = 0; j < length; j++) {
                        colorTempD[4 * j] = colorTempD[4 * j] * enhanceNum;
                        colorTempD[4 * j + 1] = colorTempD[4 * j + 1] * enhanceNum;
                        colorTempD[4 * j + 2] = colorTempD[4 * j + 2] * enhanceNum;
                        colorTempD[4 * j + 3] = colorTempD[4 * j + 3] * enhanceNum;
                    }

                    //TODO: do the operations.


                    FastDctFft.inverseTransform(colorTempD);

                    for (int j = 0; j < length; j++) {
                        int color = Color.argb(
                                (int) colorTempD[4 * j],
                                (int) colorTempD[4 * j + 1],
                                (int) colorTempD[4 * j + 2],
                                (int) colorTempD[4 * j + 3]
                        );

                        backgroundPixels[start + j] = color;
                    }
                }
            }

            outputBitmap.setPixels(backgroundPixels, 0, backgroundBitmap.getWidth(), 0, 0,
                    backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
            return outputBitmap;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (listener != null) {
            if (bitmap != null) {
                listener.onSuccess(bitmap);
            } else {
                listener.onFailure(ERROR_CREATE_FAILED);
            }
        }
        super.onPostExecute(bitmap);
    }

    /**
     * Normalize array.
     *
     * @param inputArray The array to be normalized.
     * @return The result of the normalization.
     */
    public double[] normalizeArray(double[] inputArray, double dataHigh,
                                    double dataLow, double normalizedHigh,
                                    double normalizedLow) {
        for (int i = 0; i < inputArray.length; i++) {
            inputArray[i] = ((inputArray[i] - dataLow)
                    / (dataHigh - dataLow))
                    * (normalizedHigh - normalizedLow) + normalizedLow;
        }
        return inputArray;
    }

}