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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.widget.ImageView;

import static com.watermark.androidwm.utils.BitmapUtils.resizeBitmap;
import static com.watermark.androidwm.utils.Constant.MAX_IMAGE_SIZE;

/**
 * It's a wrapper of the watermark image.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 * @since 29/08/2018
 */
public class WatermarkImage {
    private Bitmap image;
    @DrawableRes
    private int imageDrawable;
    private int alpha = 50;
    private Context context;
    private double size = 0.2;
    // set the default values for the position.
    private WatermarkPosition position = new WatermarkPosition(0, 0, 0);

    /**
     * Constructors for WatermarkImage.
     * since we use the mobile to calculate the image, the image cannot be to large,
     * we set the maxsize of an image to 1024x1024.
     */
    public WatermarkImage(Bitmap image) {
        this.image = resizeBitmap(image, MAX_IMAGE_SIZE);
    }

    public WatermarkImage(Context context, @DrawableRes int imageDrawable, WatermarkPosition position) {
        this.imageDrawable = imageDrawable;
        this.position = position;
        this.context = context;
        this.image = getBitmapFromDrawable(imageDrawable);
    }

    public WatermarkImage(Context context, @DrawableRes int imageDrawable) {
        this.imageDrawable = imageDrawable;
        this.context = context;
        this.image = getBitmapFromDrawable(imageDrawable);
    }

    public WatermarkImage(Bitmap image, WatermarkPosition position) {
        this.image = resizeBitmap(image, MAX_IMAGE_SIZE);
        this.position = position;
    }

    public WatermarkImage(ImageView imageView) {
        watermarkFromImageView(imageView);
    }

    /**
     * Getters and Setters for those attrs.
     */
    public Bitmap getImage() {
        return image;
    }

    public int getAlpha() {
        return alpha;
    }

    public WatermarkPosition getPosition() {
        return position;
    }

    public WatermarkImage setPosition(WatermarkPosition position) {
        this.position = position;
        return this;
    }

    public double getSize() {
        return size;
    }

    /**
     * @param size can be set to 0-1 as the proportion of
     *             background image.
     */
    public WatermarkImage setSize(@FloatRange(from = 0, to = 1) double size) {
        this.size = size;
        return this;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public WatermarkImage setImageDrawable(@DrawableRes int imageDrawable) {
        this.imageDrawable = imageDrawable;
        return this;
    }

    public WatermarkImage setPositionX(@FloatRange(from = 0, to = 1) double x) {
        this.position.setPositionX(x);
        return this;
    }

    public WatermarkImage setPositionY(@FloatRange(from = 0, to = 1) double y) {
        this.position.setPositionY(y);
        return this;
    }

    public WatermarkImage setRotation(double rotation) {
        this.position.setRotation(rotation);
        return this;
    }

    /**
     * @param imageAlpha can be set to 0-255.
     */
    public WatermarkImage setImageAlpha(int imageAlpha) {
        this.alpha = imageAlpha;
        return this;
    }

    /**
     * load a bitmap as watermark image from a ImageView.
     *
     * @param imageView the ImageView we need to use.
     */
    private void watermarkFromImageView(ImageView imageView) {
        imageView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        // set the limitation of input bitmap.
        this.image = resizeBitmap(drawable.getBitmap(), MAX_IMAGE_SIZE);
    }

    private Bitmap getBitmapFromDrawable(@DrawableRes int imageDrawable) {
        return resizeBitmap(BitmapFactory.decodeResource(context.getResources(),
                imageDrawable), MAX_IMAGE_SIZE);
    }
}
