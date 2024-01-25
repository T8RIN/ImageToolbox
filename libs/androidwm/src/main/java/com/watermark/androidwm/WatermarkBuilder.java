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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.watermark.androidwm.bean.WatermarkImage;
import com.watermark.androidwm.bean.WatermarkPosition;
import com.watermark.androidwm.bean.WatermarkText;
import com.watermark.androidwm.listener.BuildFinishListener;

import java.util.ArrayList;
import java.util.List;

import static com.watermark.androidwm.utils.BitmapUtils.resizeBitmap;
import static com.watermark.androidwm.utils.Constant.MAX_IMAGE_SIZE;

/**
 * A builder class for setting default structural classes for watermark to use.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
public final class WatermarkBuilder {
    private Context context;
    private Bitmap backgroundImg;
    private boolean isTileMode = false;
    private boolean isLSB = false;
    private boolean resizeBackgroundImg;
    private BuildFinishListener<Bitmap> buildFinishListener = null;

    private WatermarkImage watermarkImage;
    private WatermarkText watermarkText;
    private List<WatermarkText> watermarkTexts = new ArrayList<>();
    private List<WatermarkImage> watermarkBitmaps = new ArrayList<>();

    /**
     * Constructors for WatermarkBuilder
     */
    private WatermarkBuilder(@NonNull Context context, @NonNull Bitmap backgroundImg, boolean resizeBackgroundImg) {
        this.context = context;
        this.resizeBackgroundImg = resizeBackgroundImg;
        if (resizeBackgroundImg) {
            this.backgroundImg = resizeBitmap(backgroundImg, MAX_IMAGE_SIZE);
        } else {
            this.backgroundImg = backgroundImg;
        }
    }

    private WatermarkBuilder(@NonNull Context context, @NonNull ImageView backgroundImageView, boolean resizeBackgroundImg) {
        this.context = context;
        this.resizeBackgroundImg = resizeBackgroundImg;
        backgroundFromImageView(backgroundImageView);
    }

    private WatermarkBuilder(@NonNull Context context, @DrawableRes int backgroundDrawable, boolean resizeBackgroundImg) {
        this.context = context;
        this.resizeBackgroundImg = resizeBackgroundImg;
        if (resizeBackgroundImg) {
            this.backgroundImg = resizeBitmap(BitmapFactory.decodeResource(context.getResources(),
                    backgroundDrawable), MAX_IMAGE_SIZE);
        } else {
            this.backgroundImg = BitmapFactory.decodeResource(context.getResources(),
                    backgroundDrawable);
        }

    }

    private WatermarkBuilder(@NonNull Context context, @NonNull Bitmap backgroundImg) {
        this(context, backgroundImg, true);
    }

    private WatermarkBuilder(@NonNull Context context, @NonNull ImageView backgroundImageView) {
        this(context, backgroundImageView, true);
    }

    private WatermarkBuilder(@NonNull Context context, @DrawableRes int backgroundDrawable) {
        this(context, backgroundDrawable, true);
    }


    /**
     * to get an instance form class.
     *
     * @return instance of {@link WatermarkBuilder}
     */
    @SuppressWarnings("PMD")
    public static WatermarkBuilder create(Context context, Bitmap backgroundImg) {
        return new WatermarkBuilder(context, backgroundImg);
    }

    /**
     * to get an instance form class.
     * Load the background image from a {@link ImageView}。
     *
     * @return instance of {@link WatermarkBuilder}
     */
    @SuppressWarnings("PMD")
    public static WatermarkBuilder create(Context context, ImageView imageView) {
        return new WatermarkBuilder(context, imageView);
    }

    /**
     * to get an instance form class.
     * Load the background image from a DrawableRes。
     *
     * @return instance of {@link WatermarkBuilder}
     */
    @SuppressWarnings("PMD")
    public static WatermarkBuilder create(Context context, @DrawableRes int backgroundDrawable) {
        return new WatermarkBuilder(context, backgroundDrawable);
    }

    /**
     * to get an instance form class.
     * with background image resize option
     *
     * @return instance of {@link WatermarkBuilder}
     */
    @SuppressWarnings("PMD")
    public static WatermarkBuilder create(Context context, Bitmap backgroundImg, boolean resizeBackgroundImg) {
        return new WatermarkBuilder(context, backgroundImg, resizeBackgroundImg);
    }

    /**
     * to get an instance form class.
     * Load the background image from a {@link ImageView}。
     * with background image resize option
     *
     * @return instance of {@link WatermarkBuilder}
     */
    @SuppressWarnings("PMD")
    public static WatermarkBuilder create(Context context, ImageView imageView, boolean resizeBackgroundImg) {
        return new WatermarkBuilder(context, imageView, resizeBackgroundImg);
    }

    /**
     * to get an instance form class.
     * Load the background image from a DrawableRes。
     * with background image resize option
     *
     * @return instance of {@link WatermarkBuilder}
     */
    @SuppressWarnings("PMD")
    public static WatermarkBuilder create(Context context, @DrawableRes int backgroundDrawable, boolean resizeBackgroundImg) {
        return new WatermarkBuilder(context, backgroundDrawable, resizeBackgroundImg);
    }

    /**
     * Sets the {@link String} as the args
     * which ready for adding to a watermark.
     * Using the default position.
     *
     * @param inputText The text to add.
     * @return This {@link WatermarkBuilder}.
     */
    public WatermarkBuilder loadWatermarkText(@NonNull String inputText) {
        watermarkText = new WatermarkText(inputText);
        return this;
    }

    /**
     * Sets the {@link String} as the args
     * which ready for adding to a watermark.
     * Using the new position.
     *
     * @param inputText The text to add.
     * @param position  The position in the background image.
     * @return This {@link WatermarkBuilder}.
     */
    public WatermarkBuilder loadWatermarkText(@NonNull String inputText,
                                              @NonNull WatermarkPosition position) {
        watermarkText = new WatermarkText(inputText, position);
        return this;
    }

    /**
     * Sets the {@link String} as the args
     * which ready for adding to a watermark.
     *
     * @param watermarkString The {@link WatermarkText} object.
     * @return This {@link WatermarkBuilder}.
     */
    public WatermarkBuilder loadWatermarkText(@NonNull WatermarkText watermarkString) {
        watermarkText = watermarkString;
        return this;
    }

    /**
     * Sets the {@link String} as the args
     * which ready for adding to a watermark.
     * And, this is a set of Strings.
     *
     * @param watermarkTexts The texts to add.
     * @return This {@link WatermarkBuilder}.
     */
    public WatermarkBuilder loadWatermarkTexts(@NonNull List<WatermarkText> watermarkTexts) {
        this.watermarkTexts = watermarkTexts;
        return this;
    }

    /**
     * Sets the {@link Bitmap} as the args
     * which ready for adding to a background.
     * Using the default position.
     *
     * @param wmImg The image to add.
     * @return This {@link WatermarkBuilder}.
     */
    public WatermarkBuilder loadWatermarkImage(@NonNull Bitmap wmImg) {
        watermarkImage = new WatermarkImage(wmImg);
        return this;
    }

    /**
     * Sets the {@link Bitmap} as the args
     * which ready for adding to a background.
     * Using the new position.
     *
     * @param position The position in the background image.
     * @param wmImg    The bitmap to add into.
     * @return This {@link WatermarkBuilder}.
     */
    public WatermarkBuilder loadWatermarkImage(@NonNull Bitmap wmImg,
                                               @NonNull WatermarkPosition position) {
        watermarkImage = new WatermarkImage(wmImg, position);
        return this;
    }

    /**
     * Sets the {@link Bitmap} as the args
     * which ready for adding to a background.
     *
     * @param watermarkImg The {@link WatermarkImage} object.
     * @return This {@link WatermarkBuilder}.
     */
    public WatermarkBuilder loadWatermarkImage(@NonNull WatermarkImage watermarkImg) {
        watermarkImage = watermarkImg;
        return this;
    }

    /**
     * Sets the {@link Bitmap} as the args
     * which ready for adding into the background.
     * And, this is a set of bitmaps.
     *
     * @param bitmapList The bitmaps to add.
     * @return This {@link WatermarkBuilder}.
     */
    public WatermarkBuilder loadWatermarkImages(@NonNull List<WatermarkImage> bitmapList) {
        this.watermarkBitmaps = bitmapList;
        return this;
    }

    /**
     * Set mode to tile. We need to draw watermark over the
     * whole background.
     */
    public WatermarkBuilder setTileMode(boolean tileMode) {
        this.isTileMode = tileMode;
        return this;
    }

    /**
     * set a listener for building progress.
     */
    public void setInvisibleWMListener(
            boolean isLSB,
            BuildFinishListener<Bitmap> listener
    ) {
        this.buildFinishListener = listener;
        this.isLSB = isLSB;
        new Watermark(
                context,
                backgroundImg,
                watermarkImage,
                watermarkBitmaps,
                watermarkText,
                watermarkTexts,
                isTileMode,
                true,
                isLSB,
                buildFinishListener
        );
    }


    /**
     * load a bitmap as background image from a ImageView.
     *
     * @param imageView the {@link ImageView} we need to use.
     */
    private void backgroundFromImageView(ImageView imageView) {
        imageView.invalidate();
        if (imageView.getDrawable() != null) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            if (resizeBackgroundImg) {
                backgroundImg = resizeBitmap(drawable.getBitmap(), MAX_IMAGE_SIZE);
            } else {
                backgroundImg = drawable.getBitmap();
            }
        }
    }

    /**
     * let the watermark builder to build a new watermark object
     *
     * @return a new {@link Watermark} object
     */
    public Watermark getWatermark() {
        return new Watermark(
                context,
                backgroundImg,
                watermarkImage,
                watermarkBitmaps,
                watermarkText,
                watermarkTexts,
                isTileMode,
                false,
                isLSB,
                buildFinishListener
        );
    }
}
