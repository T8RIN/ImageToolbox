package com.gemalto.jp2;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * JPEG-2000 bitmap decoder. The supported data formats are: JP2 (standard JPEG-2000 file format) and J2K
 * (JPEG-2000 codestream). Only RGB(A) and grayscale(A) colorspaces are supported.
 */
@SuppressWarnings("unused")
public class JP2Decoder {
    private static final String TAG = "JP2Decoder";
    private static final byte[] JP2_RFC3745_MAGIC = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 0x6a, (byte) 0x50, (byte) 0x20, (byte) 0x20, (byte) 0x0d, (byte) 0x0a, (byte) 0x87, (byte) 0x0a};
    private static final byte[] JP2_MAGIC = new byte[]{(byte) 0x0d, (byte) 0x0a, (byte) 0x87, (byte) 0x0a};
    private static final byte[] J2K_CODESTREAM_MAGIC = new byte[]{(byte) 0xff, (byte) 0x4f, (byte) 0xff, (byte) 0x51};

    static {
        System.loadLibrary("openjpeg");
    }

    private byte[] data = null;
    private String fileName = null;
    private InputStream is = null;
    private int skipResolutions = 0;
    private int layersToDecode = 0;
    private boolean premultiplication = true;

    /**
     * Decode a JPEG-2000 image from a byte array.
     *
     * @param data the JPEG-2000 image
     */
    public JP2Decoder(byte[] data) {
        this.data = data;
    }

    /**
     * Decode a JPEG-2000 image file.
     *
     * @param fileName the name of the JPEG-2000 file
     */
    public JP2Decoder(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Decode a JPEG-2000 image from a stream.
     *
     * @param is the stream containing the JPEG-2000 image<br>
     *           <strong>Note: the whole content of the stream will be read. The end of image data is not detected.</strong>
     */
    public JP2Decoder(final InputStream is) {
        this.is = is;
    }

    /**
     * Returns true if the byte array starts with values typical for a JPEG-2000 header.
     *
     * @param data the byte array to check
     * @return {@code true} if the beginning looks like a JPEG-2000 header; {@code false} otherwise
     */
    public static boolean isJPEG2000(byte[] data) {
        if (data == null) return false;
        if (startsWith(data, JP2_RFC3745_MAGIC)) return true;
        if (startsWith(data, JP2_MAGIC)) return true;
        return startsWith(data, J2K_CODESTREAM_MAGIC);
    }

    private static byte[] readInputStream(InputStream in) {
        //sanity check
        if (in == null) {
            Log.e(TAG, "input stream is null!");
            return null;
        }

        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream(in.available());
            byte[] buffer = new byte[16 * 1024];
            int bytesRead = in.read(buffer);
            while (bytesRead >= 0) {
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        Get the header data from the native code
     */
    private static Header nativeToHeader(int[] data) {
        if (data == null || data.length < 5) return null;
        Header ret = new Header();
        ret.width = data[0];
        ret.height = data[1];
        ret.hasAlpha = data[2] != 0;
        ret.numResolutions = data[3];
        ret.numQualityLayers = data[4];
        return ret;
    }

    //does array1 start with contents of array2?
    private static boolean startsWith(@NonNull byte[] array1, @NonNull byte[] array2) {
        if (array1.length < array2.length) return false;
        for (int i = 0; i < array2.length; i++) {
            if (array1[i] != array2[i]) return false;
        }
        return true;
    }

    private static native int[] decodeJP2File(String filename, int reduce, int layers);

    private static native int[] decodeJP2ByteArray(byte[] data, int reduce, int layers);

    private static native int[] readJP2HeaderFile(String filename);

    private static native int[] readJP2HeaderByteArray(byte[] data);

    /**
     * Set the number of highest resolution levels to be discarded. The image resolution is effectively divided
     * by 2 to the power of the number of discarded levels. The reduce factor is limited by the number of stored
     * resolutions in the file. The number of existing resolutions can be detected using the {@link #readHeader()}
     * method.<br><br>
     * <p>
     * At least one (the lowest) resolution is always decoded, no matter how high a number you set here.<br><br>
     * <p>
     * Default value: 0 (the image is decoded up to the highest resolution)
     *
     * @param skipResolutions the number of resolutions to skip
     * @return this instance of {@code JP2Decoder}
     */
    public JP2Decoder setSkipResolutions(final int skipResolutions) {
        if (skipResolutions < 0)
            throw new IllegalArgumentException("skipResolutions cannot be a negative number!");
        this.skipResolutions = skipResolutions;
        return this;
    }

    /**
     * Set the maximum number of quality layers to decode. If there are less quality layers than the specified number,
     * all the quality layers are decoded. The available number of quality layers can be detected using the
     * {@link #readHeader()} method. Special value 0 indicates all layers.<br><br>
     * <p>
     * Default value: 0 (all layers are decoded)
     *
     * @param layersToDecode number of quality layers to decode
     * @return this instance of {@code JP2Decoder}
     */
    public JP2Decoder setLayersToDecode(final int layersToDecode) {
        if (layersToDecode < 0)
            throw new IllegalArgumentException("layersToDecode cannot be a negative number!");
        this.layersToDecode = layersToDecode;
        return this;
    }

    /**
     * This allows you to turn off alpha pre-multiplication in the output bitmap. Normally Android bitmaps with alpha
     * channel have their RGB component pre-multiplied by the normalized alpha channel. This improves performance when
     * displaying the bitmap, but it leads to loss of precision. This is no problem when you only want to display
     * the bitmap, but it can be a problem when you want to further process the bitmap's raw data.<br><br>
     * <p>
     * Since API 19 you can turn this pre-multiplication off. The loss of precision doesn't occur then, but the system
     * wont't be able to draw the output bitmap. On API &lt; 19 this setting is ignored.<br><br>
     * <p>
     * In most cases you should not use this.
     *
     * @return this instance of {@code JP2Decoder}
     * @see Bitmap#setPremultiplied(boolean)
     * @see BitmapFactory.Options#inPremultiplied
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public JP2Decoder disableBitmapPremultiplication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.premultiplication = false;
        } else {
            Log.e(TAG, "Pre-multiplication cannot be disabled on API < 19. Ignoring.");
        }
        return this;
    }

    /**
     * @return the decoded image; {@code null} in case of an error
     */
    public Bitmap decode() {
        int[] res = null;
        if (fileName != null) {
            res = decodeJP2File(fileName, skipResolutions, layersToDecode);
        } else {
            if (data == null && is != null) {
                data = readInputStream(is);
            }
            if (data == null) {
                Log.e(TAG, "Data is null, nothing to decode");
            } else {
                res = decodeJP2ByteArray(data, skipResolutions, layersToDecode);
            }
        }
        return nativeToBitmap(res);
    }

    /**
     * Decodes the file header information and returns it in a {@link Header} object.
     *
     * @return file header information
     */
    public Header readHeader() {
        int[] res = null;
        if (fileName != null) {
            res = readJP2HeaderFile(fileName);
        } else {
            if (data == null && is != null) {
                data = readInputStream(is);
            }
            if (data == null) {
                Log.e(TAG, "Data is null, nothing to decode");
            } else {
                res = readJP2HeaderByteArray(data);
            }
        }

        return nativeToHeader(res);
    }

    /*
        Get the decoded data from the native code and create a Bitmap object.
     */
    private Bitmap nativeToBitmap(int[] data) {
        if (data == null || data.length < 3) return null;
        int width = data[0];
        int height = data[1];
        int alpha = data[2];

        Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        if (!premultiplication && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bmp.setPremultiplied(false);
        }
        bmp.setPixels(data, 3, width, 0, 0, width, height);
        bmp.setHasAlpha(alpha != 0);

        return bmp;
    }

    public static class Header {
        public int width;
        public int height;
        public boolean hasAlpha;
        public int numResolutions;
        public int numQualityLayers;
    }
}
