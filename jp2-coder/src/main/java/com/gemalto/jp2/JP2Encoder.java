package com.gemalto.jp2;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.IntDef;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * JPEG-2000 bitmap encoder. Output properties:
 * <ul>
 *     <li>file format: JP2 (standard JPEG-2000 file format) or J2K (JPEG-2000 codestream)</li>
 *     <li>colorspace: RGB or RGBA (depending on the {@link Bitmap#hasAlpha() hasAlpha()} value of the input bitmap)</li>
 *     <li>precision: 8 bits per channel</li>
 *     <li>image quality: can be set by visual quality or compression ratio; or lossless</li>
 * </ul>
 */
public class JP2Encoder {
    /**
     * JPEG 2000 codestream format
     */
    public static final int FORMAT_J2K = 0;
    /**
     * The standard JPEG-2000 file format
     */
    public static final int FORMAT_JP2 = 1;
    private static final String TAG = "JP2Encoder";
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_FAILURE = 1;
    private static final int DEFAULT_NUM_RESOLUTIONS = 6;
    //TODO in case of update to a newer version of OpenJPEG, check if it still throws error in case of too high resolution number
    //minimum resolutions supported by OpenJPEG 2.3.0
    private static final int MIN_RESOLUTIONS = 1;
    //maximum resolutions supported by OpenJPEG 2.3.0
    private static final int MAX_RESOLUTIONS_GLOBAL = 32;

    static {
        System.loadLibrary("openjpeg");
    }

    private final Bitmap bmp;
    //maximum resolutions possible to create with the given image dimensions [ = floor(log2(min_image_dimension)) + 1]
    private final int maxResolutions;
    private int numResolutions = DEFAULT_NUM_RESOLUTIONS;
    private float[] compressionRatios = null;
    private float[] qualityValues = null;
    private int outputFormat = FORMAT_JP2;

    /**
     * Creates a new instance of the JPEG-2000 encoder.
     *
     * @param bmp the bitmap to encode
     */
    public JP2Encoder(final Bitmap bmp) {
        if (bmp == null) throw new IllegalArgumentException("Bitmap must not be null!");
        this.bmp = bmp;
        maxResolutions = Math.min(log2RoundedDown(Math.min(bmp.getWidth(), bmp.getHeight())) + 1, MAX_RESOLUTIONS_GLOBAL);
        if (numResolutions > maxResolutions) numResolutions = maxResolutions;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("openjpeg encode: image size = %d x %d, maxResolutions = %d", bmp.getWidth(), bmp.getHeight(), maxResolutions));
        }
    }

    private static int log2RoundedDown(int n) {
        //returns log2(n) rounded down to the nearest integer.
        //naive implementation, but should be fast enough for our purposes.
        //only test until MAX_RESOLUTIONS_GLOBAL, we don't care about higher results anyway
        for (int i = 0; i < MAX_RESOLUTIONS_GLOBAL; i++) {
            if ((1 << i) > n) return i - 1;
        }
        return MAX_RESOLUTIONS_GLOBAL;
    }

    private static native int encodeJP2File(String filename, int[] pixels, boolean hasAlpha, int width, int height, int fileFormat, int numResolutions, float[] compressionRatios, float[] qualityValues);

    private static native byte[] encodeJP2ByteArray(int[] pixels, boolean hasAlpha, int width, int height, int fileFormat, int numResolutions, float[] compressionRatios, float[] qualityValues);

    /**
     * Set the number of resolutions. It corresponds to the number of DWT decompositions +1.
     * Minimum number of resolutions is 1. Maximum is floor(log2(min_image_dimension)) + 1.<br><br>
     * <p>
     * Some software might be able to take advantage of this and decode only smaller resolution
     * when appropriate. (This library is one such software. See {@link JP2Decoder#setSkipResolutions(int)}).<br><br>
     * <p>
     * Default value: 6 if the image dimensions are at least 32x32. Otherwise the maximum supported
     * number of resolutions.
     *
     * @param numResolutions number of resolutions
     * @return this {@code JP2Encoder} instance
     */
    public JP2Encoder setNumResolutions(int numResolutions) {
        if (numResolutions < MIN_RESOLUTIONS || numResolutions > maxResolutions) {
            throw new IllegalArgumentException("Maximum number of resolutions for this image is between " + MIN_RESOLUTIONS + " and " + maxResolutions);
        }
        this.numResolutions = numResolutions;
        return this;
    }

    /**
     * Set compression ratio. The value is a factor of compression, thus 20 means 20 times compressed
     * (measured against the raw, uncompressed image size). 1 indicates lossless compression<br><br>
     * <p>
     * This option produces a predictable image size, but the visual image quality will depend on how
     * "compressible" the original image is. If you want to get predictable visual quality (but
     * unpredictable size), use {@link #setVisualQuality(float...)}.<br><br>
     * <p>
     * You can set multiple compression ratios - this will produce an image with multiple quality layers.
     * Some software might be able to take advantage of this and decode only lesser quality layer
     * when appropriate. (This library is one such software. See {@link JP2Decoder#setLayersToDecode(int)}.)<br><br>
     * <p>
     * Default value: a single lossless quality layer.<br><br>
     *
     * <strong>Note: {@link #setCompressionRatio(float...)} and {@link #setVisualQuality(float...)}
     * cannot be used together.</strong>
     *
     * @param compressionRatios compression ratios
     * @return this {@code JP2Encoder} instance
     */
    public JP2Encoder setCompressionRatio(float... compressionRatios) {
        if (compressionRatios == null || compressionRatios.length == 0) {
            this.compressionRatios = null;
            return this;
        }

        //check for invalid values
        for (final float compressionRatio : compressionRatios) {
            if (compressionRatio < 1) {
                throw new IllegalArgumentException("compression ratio must be at least 1");
            }
        }

        //check for conflicting settings
        if (qualityValues != null)
            throw new IllegalArgumentException("setCompressionRatios and setQualityValues must not be used together!");

        //sort the values and filter out duplicates
        compressionRatios = sort(compressionRatios, false, 1);

        //store the values
        this.compressionRatios = compressionRatios;
        return this;
    }

    /**
     * Set image quality. The value is a <a href="https://en.wikipedia.org/wiki/Peak_signal-to-noise_ratio">PSNR</a>,
     * measured in dB. Higher PSNR means higher quality. A special value 0 indicates lossless quality.<br><br>
     * <p>
     * As for reasonable values: 20 is extremely aggressive compression, 60-70 is close to lossless.
     * For "normal" compression you might want to aim at 30-50, depending on your needs.<br><br>
     * <p>
     * This option produces predictable visual image quality, but the file size will depend on how
     * "compressible" the original image is. If you want to get predictable size (but
     * unpredictable visual quality), use {@link #setCompressionRatio(float...)}.<br><br>
     * <p>
     * You can set multiple quality values - this will produce an image with multiple quality layers.
     * Some software might be able to take advantage of this and decode only lesser quality layer
     * when appropriate. (This library is one such software. See {@link JP2Decoder#setLayersToDecode(int)}.)<br><br>
     * <p>
     * Default value: a single lossless quality layer.<br><br>
     *
     * <strong>Note: {@link #setVisualQuality(float...)} and {@link #setCompressionRatio(float...)} cannot be used together.</strong>
     *
     * @param qualityValues quality layer PSNR values
     * @return this {@code JP2Encoder} instance
     */
    public JP2Encoder setVisualQuality(float... qualityValues) {
        if (qualityValues == null || qualityValues.length == 0) {
            this.qualityValues = null;
            return this;
        }

        //check for invalid values
        for (final float qualityValue : qualityValues) {
            if (qualityValue < 0) {
                throw new IllegalArgumentException("quality values must not be negative");
            }
        }

        //check for conflicting settings
        if (compressionRatios != null)
            throw new IllegalArgumentException("setCompressionRatios and setQualityValues must not be used together!");

        //sort the values and filter out duplicates
        qualityValues = sort(qualityValues, true, 0);

        //store the values
        this.qualityValues = qualityValues;
        return this;
    }

    /**
     * Sets the output file format. The default value is {@link #FORMAT_JP2}.
     *
     * @param outputFormat {@link #FORMAT_J2K} or {@link #FORMAT_JP2}
     * @return this {@code JP2Encoder} instance
     */
    public JP2Encoder setOutputFormat(@OutputFormat int outputFormat) {
        if (outputFormat != FORMAT_J2K && outputFormat != FORMAT_JP2)
            throw new IllegalArgumentException("output format must be FORMAT_JP2 or FORMAT_J2K!");
        this.outputFormat = outputFormat;
        return this;
    }

    /**
     * Encode to JPEG-2000, return the result as a byte array.
     *
     * @return the JPEG-2000 encoded data
     */
    public byte[] encode() {
        return encodeInternal(bmp);
    }

    /**
     * Encode to JPEG-2000, store the result into a file.
     *
     * @param fileName the name of the output file
     * @return {@code true} if the image was successfully converted and stored; {@code false} otherwise
     */
    public boolean encode(String fileName) {
        return encodeInternal(bmp, fileName);
    }

    /**
     * Encode to JPEG-2000, write the result into an {@link OutputStream}.
     *
     * @param out the stream into which the result will be written
     * @return the number of bytes written; 0 in case of a conversion error
     * @throws IOException if there's an error writing the result into the output stream
     */
    public int encode(OutputStream out) throws IOException {
        byte[] data = encodeInternal(bmp);
        if (data == null) return 0;
        out.write(data);
        return data.length;
    }

    private byte[] encodeInternal(Bitmap bmp) {
        if (bmp == null) return null;
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        /* debug */
        long start = 0;
        /* debug */
        if (BuildConfig.DEBUG) start = System.currentTimeMillis();
        byte[] ret = encodeJP2ByteArray(pixels, bmp.hasAlpha(), bmp.getWidth(), bmp.getHeight(), outputFormat, numResolutions, compressionRatios, qualityValues);
        /* debug */
        if (BuildConfig.DEBUG)
            Log.d(TAG, "converting to JP2: " + (System.currentTimeMillis() - start) + " ms");
        return ret;
    }

    private boolean encodeInternal(Bitmap bmp, String fileName) {
        if (bmp == null) return false;
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        /* debug */
        long start = 0;
        /* debug */
        if (BuildConfig.DEBUG) start = System.currentTimeMillis();
        int ret = encodeJP2File(fileName, pixels, bmp.hasAlpha(), bmp.getWidth(), bmp.getHeight(), outputFormat, numResolutions, compressionRatios, qualityValues);
        /* debug */
        if (BuildConfig.DEBUG)
            Log.d(TAG, "converting to JP2: " + (System.currentTimeMillis() - start) + " ms");
        return ret == EXIT_SUCCESS;
    }

    private float[] sort(final float[] array, final boolean ascending, final float losslessValue) {
        if (array == null || array.length == 0) return null;
        List<Float> list = new ArrayList<>();
        for (float value : array) {
            //filter out duplicates
            if (!list.contains(value)) list.add(value);
        }
        //sort the list
        Collections.sort(list, new Comparator<Float>() {
            @Override
            public int compare(final Float o1, final Float o2) {
                //lossless value must always come last
                if (o1 == losslessValue && o2 != losslessValue) return 1;
                if (o2 == losslessValue && o1 != losslessValue) return -1;
                return (int) Math.signum(ascending ? o1 - o2 : o2 - o1);
            }
        });
        //copy from list back to array
        float[] ret = new float[list.size()];
        for (int i = 0; i < ret.length; i++) ret[i] = list.get(i);
        return ret;
    }

    @IntDef({FORMAT_J2K, FORMAT_JP2})
    public @interface OutputFormat {
    }
}
