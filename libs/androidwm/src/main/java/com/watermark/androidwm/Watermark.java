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
package com.watermark.androidwm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.watermark.androidwm.bean.AsyncTaskParams;
import com.watermark.androidwm.bean.WatermarkImage;
import com.watermark.androidwm.bean.WatermarkText;
import com.watermark.androidwm.listener.BuildFinishListener;
import com.watermark.androidwm.task.FDWatermarkTask;
import com.watermark.androidwm.task.LSBWatermarkTask;
import com.watermark.androidwm.utils.BitmapUtils;

import java.util.List;

import static com.watermark.androidwm.utils.BitmapUtils.resizeBitmap;
import static com.watermark.androidwm.utils.BitmapUtils.textAsBitmap;

/**
 * The main class for watermark processing library.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
public class Watermark {
    private WatermarkText watermarkText;
    private WatermarkImage watermarkImg;
    private Bitmap backgroundImg;
    private Context context;
    private Bitmap outputImage;
    private Bitmap canvasBitmap;
    private boolean isTileMode;
    private boolean isInvisible;
    private boolean isLSB;
    private BuildFinishListener<Bitmap> buildFinishListener;

    /**
     * Constructors for WatermarkImage
     */
    @SuppressWarnings("PMD")
    Watermark(@NonNull Context context,
              @NonNull Bitmap backgroundImg,
              @Nullable WatermarkImage watermarkImg,
              @Nullable List<WatermarkImage> wmBitmapList,
              @Nullable WatermarkText inputText,
              @Nullable List<WatermarkText> wmTextList,
              boolean isTileMode,
              boolean isInvisible,
              boolean isLSB,
              @Nullable BuildFinishListener<Bitmap> buildFinishListener) {

        this.context = context;
        this.isTileMode = isTileMode;
        this.watermarkImg = watermarkImg;
        this.backgroundImg = backgroundImg;
        this.watermarkText = inputText;
        this.isInvisible = isInvisible;
        this.buildFinishListener = buildFinishListener;
        this.isLSB = isLSB;

        canvasBitmap = backgroundImg;
        outputImage = backgroundImg;

        createWatermarkImage(watermarkImg);
        createWatermarkImages(wmBitmapList);
        createWatermarkText(watermarkText);
        createWatermarkTexts(wmTextList);
    }


    /**
     * interface for getting the watermark bitmap.
     *
     * @return {@link Bitmap} in watermark.
     */
    public Bitmap getWatermarkBitmap() {
        return watermarkImg.getImage();
    }

    /**
     * interface for getting the watermark text.
     *
     * @return {@link Bitmap} in watermark.
     */
    public String getWatermarkText() {
        return watermarkText.getText();
    }

    /**
     * Creating the composite image with {@link WatermarkImage}.
     * This method cannot be called outside.
     */
    private void createWatermarkImage(WatermarkImage watermarkImg) {
        if (watermarkImg != null && backgroundImg != null) {
            if (isInvisible) {
                Bitmap scaledWMBitmap = resizeBitmap(watermarkImg.getImage(), (float) watermarkImg.getSize(), backgroundImg);
                if (isLSB) {
                    new LSBWatermarkTask(buildFinishListener).execute(
                            new AsyncTaskParams(context, backgroundImg, scaledWMBitmap)
                    );
                } else {
                    new FDWatermarkTask(buildFinishListener).execute(
                            new AsyncTaskParams(context, backgroundImg, scaledWMBitmap)
                    );
                }
            } else {
                Paint watermarkPaint = new Paint();
                watermarkPaint.setAlpha(watermarkImg.getAlpha());
                Bitmap newBitmap = Bitmap.createBitmap(backgroundImg.getWidth(),
                        backgroundImg.getHeight(), backgroundImg.getConfig());
                Canvas watermarkCanvas = new Canvas(newBitmap);
                watermarkCanvas.drawBitmap(canvasBitmap, 0, 0, null);
                Bitmap scaledWMBitmap = resizeBitmap(watermarkImg.getImage(), (float) watermarkImg.getSize(), backgroundImg);
                scaledWMBitmap = adjustPhotoRotation(scaledWMBitmap,
                        (int) watermarkImg.getPosition().getRotation());

                if (isTileMode) {
                    watermarkPaint.setShader(new BitmapShader(scaledWMBitmap,
                            Shader.TileMode.REPEAT,
                            Shader.TileMode.REPEAT));
                    Rect bitmapShaderRect = watermarkCanvas.getClipBounds();
                    watermarkCanvas.drawRect(bitmapShaderRect, watermarkPaint);
                } else {
                    watermarkCanvas.drawBitmap(scaledWMBitmap,
                            (float) watermarkImg.getPosition().getPositionX() * backgroundImg.getWidth(),
                            (float) watermarkImg.getPosition().getPositionY() * backgroundImg.getHeight(),
                            watermarkPaint);
                }

                canvasBitmap = newBitmap;
                outputImage = newBitmap;
            }

        }

    }

    /**
     * Creating the composite image with {@link WatermarkImage}.
     * The input of the method is a set of {@link WatermarkImage}s.
     */
    private void createWatermarkImages(List<WatermarkImage> watermarkImages) {
        if (watermarkImages != null) {
            for (int i = 0; i < watermarkImages.size(); i++) {
                createWatermarkImage(watermarkImages.get(i));
            }
        }
    }

    /**
     * Creating the composite image with  {@link WatermarkText}.
     * This method cannot be called outside.
     */
    private void createWatermarkText(WatermarkText watermarkText) {

        if (watermarkText != null && backgroundImg != null) {
            if (isInvisible) {
                if (isLSB) {
                    new LSBWatermarkTask(buildFinishListener).execute(
                            new AsyncTaskParams(context, backgroundImg, watermarkText)
                    );
                } else {
                    new FDWatermarkTask(buildFinishListener).execute(
                            new AsyncTaskParams(context, backgroundImg, watermarkText)
                    );
                }
            } else {
                Paint watermarkPaint = new Paint();
                watermarkPaint.setAlpha(watermarkText.getTextAlpha());
                Bitmap newBitmap = Bitmap.createBitmap(backgroundImg.getWidth(),
                        backgroundImg.getHeight(), backgroundImg.getConfig());
                Canvas watermarkCanvas = new Canvas(newBitmap);
                watermarkCanvas.drawBitmap(canvasBitmap, 0, 0, null);
                Bitmap scaledWMBitmap = textAsBitmap(context, watermarkText);
                scaledWMBitmap = adjustPhotoRotation(scaledWMBitmap,
                        (int) watermarkText.getPosition().getRotation());

                if (isTileMode) {
                    watermarkPaint.setShader(new BitmapShader(scaledWMBitmap,
                            Shader.TileMode.REPEAT,
                            Shader.TileMode.REPEAT));
                    Rect bitmapShaderRect = watermarkCanvas.getClipBounds();
                    watermarkCanvas.drawRect(bitmapShaderRect, watermarkPaint);
                } else {
                    watermarkCanvas.drawBitmap(scaledWMBitmap,
                            (float) watermarkText.getPosition().getPositionX() * backgroundImg.getWidth(),
                            (float) watermarkText.getPosition().getPositionY() * backgroundImg.getHeight(),
                            watermarkPaint);
                }

                canvasBitmap = newBitmap;
                outputImage = newBitmap;
            }
        }
    }

    /**
     * Creating the composite image with {@link WatermarkText}.
     * The input of the method is a set of {@link WatermarkText}s.
     */

    private void createWatermarkTexts(List<WatermarkText> watermarkTexts) {
        if (watermarkTexts != null) {
            for (int i = 0; i < watermarkTexts.size(); i++) {
                createWatermarkText(watermarkTexts.get(i));
            }
        }
    }

    /**
     * The interface for getting the output image.
     *
     * @return {@link Bitmap} out bitmap.
     */
    public Bitmap getOutputImage() {
        return outputImage;
    }

    /**
     * Save output png image to local.
     *
     * @param path the output path of image.
     */
    public void saveToLocalPng(String path) {
        BitmapUtils.saveAsPNG(outputImage, path, true);
    }

    /**
     * You can use this function to set the composite
     * image into an ImageView.
     *
     * @param target the target {@link ImageView}.
     */
    public void setToImageView(ImageView target) {
        target.setImageBitmap(outputImage);
    }

    /**
     * Adjust the rotation of a bitmap.
     *
     * @param bitmap           input bitmap.
     * @param orientationAngle the orientation angle.
     * @return {@link Bitmap} the new bitmap.
     */
    private Bitmap adjustPhotoRotation(Bitmap bitmap, final int orientationAngle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(orientationAngle,
                (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap,
                0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
