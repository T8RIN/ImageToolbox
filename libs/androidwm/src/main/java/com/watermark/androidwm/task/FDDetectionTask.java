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
import android.os.AsyncTask;

import com.watermark.androidwm.listener.DetectFinishListener;
import com.watermark.androidwm.utils.FastDctFft;

import static com.watermark.androidwm.utils.BitmapUtils.pixel2ARGBArray;
import static com.watermark.androidwm.utils.BitmapUtils.getBitmapPixels;
import static com.watermark.androidwm.utils.Constant.CHUNK_SIZE;
import static com.watermark.androidwm.utils.Constant.ERROR_BITMAP_NULL;
import static com.watermark.androidwm.utils.Constant.ERROR_DETECT_FAILED;
import static com.watermark.androidwm.utils.Constant.MAX_IMAGE_SIZE;
import static com.watermark.androidwm.utils.Constant.WARNING_BIG_IMAGE;
import static com.watermark.androidwm.utils.StringUtils.copyFromIntArray;

/**
 * This is a task for watermark image detection.
 * In FD mode, all the task will return a bitmap;
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
@SuppressWarnings("PMD")
public class FDDetectionTask extends AsyncTask<Bitmap, Void, DetectionReturnValue> {

    private DetectFinishListener listener;

    public FDDetectionTask(DetectFinishListener listener) {
        this.listener = listener;
    }

    @Override
    protected DetectionReturnValue doInBackground(Bitmap... bitmaps) {
        Bitmap markedBitmap = bitmaps[0];
        DetectionReturnValue resultValue = new DetectionReturnValue();

        if (markedBitmap == null) {
            listener.onFailure(ERROR_BITMAP_NULL);
            return null;
        }

        if (markedBitmap.getWidth() > MAX_IMAGE_SIZE || markedBitmap.getHeight() > MAX_IMAGE_SIZE) {
            listener.onFailure(WARNING_BIG_IMAGE);
            return null;
        }

        int[] pixels = getBitmapPixels(markedBitmap);

        // divide and conquer
        if (pixels.length < CHUNK_SIZE) {
            int[] watermarkRGB = pixel2ARGBArray(pixels);
            double[] watermarkArray = copyFromIntArray(watermarkRGB);
            FastDctFft.transform(watermarkArray);

            //TODO: do some operations with colorTempArray.


        } else {
            int numOfChunks = (int) Math.ceil((double) pixels.length / CHUNK_SIZE);
            for (int i = 0; i < numOfChunks; i++) {
                int start = i * CHUNK_SIZE;
                int length = Math.min(pixels.length - start, CHUNK_SIZE);
                int[] temp = new int[length];
                System.arraycopy(pixels, start, temp, 0, length);
                double[] colorTempArray = copyFromIntArray(pixel2ARGBArray(temp));
                FastDctFft.transform(colorTempArray);

                //TODO: do some operations with colorTempArray.

            }
        }

/*        TODO: new detection operations will replace this block.
        String resultString;

        if (binaryString.contains(LSB_TEXT_PREFIX_FLAG) && binaryString.contains(LSB_TEXT_SUFFIX_FLAG)) {
            resultString = getBetweenStrings(binaryString, true, listener);
            resultString = binaryToString(resultString);
            resultValue.setWatermarkString(resultString);
        } else if (binaryString.contains(LSB_IMG_PREFIX_FLAG) && binaryString.contains(LSB_IMG_SUFFIX_FLAG)) {
            binaryString = getBetweenStrings(binaryString, false, listener);
            resultString = binaryToString(binaryString);
            resultValue.setWatermarkBitmap(BitmapUtils.stringToBitmap(resultString));
        }*/

        return resultValue;
    }

    @Override
    protected void onPostExecute(DetectionReturnValue detectionReturnValue) {
        if (detectionReturnValue == null) {
            listener.onFailure(ERROR_DETECT_FAILED);
            return;
        }

        if (detectionReturnValue.getWatermarkString() != null &&
                !"".equals(detectionReturnValue.getWatermarkString()) ||
                detectionReturnValue.getWatermarkBitmap() != null) {
            listener.onSuccess(detectionReturnValue);
        } else {
            listener.onFailure(ERROR_DETECT_FAILED);
        }
        super.onPostExecute(detectionReturnValue);
    }
}