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
package com.watermark.androidwm.utils;

/**
 * the constant pool.
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
public class Constant {
    public static final String LSB_IMG_PREFIX_FLAG = "1212";
    public static final String LSB_TEXT_PREFIX_FLAG = "2323";
    public static final String LSB_IMG_SUFFIX_FLAG = "3434";
    public static final String LSB_TEXT_SUFFIX_FLAG = "4545";

    public static final int MAX_IMAGE_SIZE = 1024;
    // use the watermark image's size
    public static final int CHUNK_SIZE = 5000;

    public static final String ERROR_NO_WATERMARKS = "No input text or image! please load an image or a text in your WatermarkBuilder!";
    public static final String ERROR_CREATE_FAILED = "created watermark failed!";
    public static final String ERROR_NO_BACKGROUND = "No background image! please load an image in your WatermarkBuilder!";
    public static final String ERROR_PIXELS_NOT_ENOUGH = "The Pixels in background are too small to put the watermark in, " +
            "the data has been lost! Please make sure the maxImageSize is bigger enough!";

    public static final String ERROR_DETECT_FAILED = "Failed to detect the watermark!";
    public static final String ERROR_NO_WATERMARK_FOUND = "No watermarks found in this image!";
    public static final String ERROR_BITMAP_NULL = "Cannot detect the watermark! markedBitmap is null object!";

    public static final String WARNING_BIG_IMAGE = "The input image may be too large to put into the memory, please be careful of the OOM!";
}
