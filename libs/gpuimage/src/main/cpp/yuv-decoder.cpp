#include <jni.h>

#include <android/bitmap.h>
#include <GLES2/gl2.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <algorithm>


float SRGBToLinear(float d);

float LinearSRGBTosRGB(float r);

extern "C" JNIEXPORT void JNICALL
Java_jp_co_cyberagent_android_gpuimage_GPUImageNativeLibrary_YUVtoRBGA(JNIEnv *env, jclass obj,
                                                                       jbyteArray yuv420sp,
                                                                       jint width, jint height,
                                                                       jintArray rgbOut) {
    int sz;
    int i;
    int j;
    int Y;
    int Cr = 0;
    int Cb = 0;
    int pixPtr = 0;
    int jDiv2 = 0;
    int R = 0;
    int G = 0;
    int B = 0;
    int cOff;
    int w = width;
    int h = height;
    sz = w * h;

    jint *rgbData = (jint *) ((*env).GetPrimitiveArrayCritical(rgbOut, 0));
    jbyte *yuv = (jbyte *) (*env).GetPrimitiveArrayCritical(yuv420sp, 0);

    for (j = 0; j < h; j++) {
        pixPtr = j * w;
        jDiv2 = j >> 1;
        for (i = 0; i < w; i++) {
            Y = yuv[pixPtr];
            if (Y < 0) Y += 255;
            if ((i & 0x1) != 1) {
                cOff = sz + jDiv2 * w + (i >> 1) * 2;
                Cb = yuv[cOff];
                if (Cb < 0) Cb += 127; else Cb -= 128;
                Cr = yuv[cOff + 1];
                if (Cr < 0) Cr += 127; else Cr -= 128;
            }

            //ITU-R BT.601 conversion
            //
            //R = 1.164*(Y-16) + 2.018*(Cr-128);
            //G = 1.164*(Y-16) - 0.813*(Cb-128) - 0.391*(Cr-128);
            //B = 1.164*(Y-16) + 1.596*(Cb-128);
            //
            Y = Y + (Y >> 3) + (Y >> 5) + (Y >> 7);
            R = Y + (Cr << 1) + (Cr >> 6);
            if (R < 0) R = 0; else if (R > 255) R = 255;
            G = Y - Cb + (Cb >> 3) + (Cb >> 4) - (Cr >> 1) + (Cr >> 3);
            if (G < 0) G = 0; else if (G > 255) G = 255;
            B = Y + Cb + (Cb >> 1) + (Cb >> 4) + (Cb >> 5);
            if (B < 0) B = 0; else if (B > 255) B = 255;
            rgbData[pixPtr++] = 0xff000000 + (R << 16) + (G << 8) + B;
        }
    }

    (*env).ReleasePrimitiveArrayCritical(rgbOut, rgbData, 0);
    (*env).ReleasePrimitiveArrayCritical(yuv420sp, yuv, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_jp_co_cyberagent_android_gpuimage_GPUImageNativeLibrary_adjustBitmap(JNIEnv *jenv, jclass thiz,
                                                                          jobject src) {
    unsigned char *srcByteBuffer;
    int result = 0;
    int i, j;
    AndroidBitmapInfo srcInfo;

    result = AndroidBitmap_getInfo(jenv, src, &srcInfo);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        return;
    }

    result = AndroidBitmap_lockPixels(jenv, src, (void **) &srcByteBuffer);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        return;
    }

    int width = srcInfo.width;
    int height = srcInfo.height;
    glReadPixels(0, 0, srcInfo.width, srcInfo.height, GL_RGBA, GL_UNSIGNED_BYTE, srcByteBuffer);

    int *pIntBuffer = (int *) srcByteBuffer;

    for (i = 0; i < height / 2; i++) {
        for (j = 0; j < width; j++) {
            int temp = pIntBuffer[(height - i - 1) * width + j];
            pIntBuffer[(height - i - 1) * width + j] = pIntBuffer[i * width + j];
            pIntBuffer[i * width + j] = temp;
        }
    }
    AndroidBitmap_unlockPixels(jenv, src);
}

extern "C" JNIEXPORT void JNICALL
Java_jp_co_cyberagent_android_gpuimage_GPUImageNativeLibrary_noise(
        JNIEnv *jenv, jclass clazz,
        jobject src, int threshold
) {
    srand(time(NULL));
    unsigned char *srcByteBuffer;
    int result = 0;
    int i, j;
    AndroidBitmapInfo srcInfo;

    result = AndroidBitmap_getInfo(jenv, src, &srcInfo);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        return;
    }

    result = AndroidBitmap_lockPixels(jenv, src, (void **) &srcByteBuffer);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        return;
    }

    int width = srcInfo.width;
    int height = srcInfo.height;

    for (int y = 0; y < height; ++y) {
        auto pixels = reinterpret_cast<uint8_t *>(reinterpret_cast<uint8_t *>(srcByteBuffer) +
                                                  y * srcInfo.stride);
        int x = 0;

        for (; x < width; ++x) {
            pixels[0] = pixels[0] | rand() % threshold;
            pixels[1] = pixels[1] | rand() % threshold;
            pixels[2] = pixels[2] | rand() % threshold;

            pixels += 4;
//            srcByteBuffer[x * y] = srcByteBuffer[x * y]
//                                   | (((rand() % threshold & 0xff) << 24) +
//                                      ((rand() % threshold & 0xff) << 16) +
//                                      ((rand() % threshold & 0xff) << (8 + (255 & 0xff))));
        }
    }
    AndroidBitmap_unlockPixels(jenv, src);
}
extern "C"
JNIEXPORT void JNICALL
Java_jp_co_cyberagent_android_gpuimage_GPUImageNativeLibrary_monochrome(
        JNIEnv *jenv,
        jclass clazz,
        jobject src,
        jfloat intensity,
        jfloat red,
        jfloat green,
        jfloat blue
) {
    srand(time(NULL));
    unsigned char *srcByteBuffer;
    int result = 0;
    int i, j;
    AndroidBitmapInfo srcInfo;

    result = AndroidBitmap_getInfo(jenv, src, &srcInfo);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        return;
    }

    result = AndroidBitmap_lockPixels(jenv, src, (void **) &srcByteBuffer);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        return;
    }

    int width = srcInfo.width;
    int height = srcInfo.height;

    for (int y = 0; y < height; ++y) {
        auto pixels = reinterpret_cast<uint8_t *>(reinterpret_cast<uint8_t *>(srcByteBuffer) +
                                                  y * srcInfo.stride);
        int x = 0;

        for (; x < width; ++x) {
            float r = SRGBToLinear(pixels[0] / 255.0f);
            float g = SRGBToLinear(pixels[1] / 255.0f);
            float b = SRGBToLinear(pixels[2] / 255.0f);
            float luma = 0.2125f * r + 0.7154f * g + 0.0721 * b;
            r = r * (1.0f - intensity) +
                (luma < 0.5f ? (2.0f * luma * red) : (1.0f - 2.0f * (1.0f - luma) * (1.0f - red))) *
                intensity;
            g = g * (1.0f - intensity) +
                (luma < 0.5f ? (2.0f * luma * green) : (1.0f -
                                                        2.0f * (1.0f - luma) * (1.0f - green))) *
                intensity;
            b = b * (1.0f - intensity) +
                (luma < 0.5f ? (2.0f * luma * blue) : (1.0f -
                                                       2.0f * (1.0f - luma) * (1.0f - blue))) *
                intensity;

            pixels[0] = std::clamp(LinearSRGBTosRGB(r) * 255.0f, 0.0f, 255.0f);
            pixels[1] = std::clamp(LinearSRGBTosRGB(g) * 255.0f, 0.0f, 255.0f);
            pixels[2] = std::clamp(LinearSRGBTosRGB(b) * 255.0f, 0.0f, 255.0f);

            pixels += 4;
        }
    }
    AndroidBitmap_unlockPixels(jenv, src);
}

float LinearSRGBTosRGB(float linear) {
    if (linear <= 0.0031308f) {
        return 12.92f * linear;
    } else {
        return 1.055f * pow(linear, 1.0f / 2.4f) - 0.055f;
    }
}

float SRGBToLinear(float v) {
    if (v <= 0.045f) {
        return v / 12.92f;
    } else {
        return pow((v + 0.055f) / 1.055f, 2.4f);
    }
}

float luminance(float red, float green, float blue) {
    float r = SRGBToLinear(red / 255.0f);
    float g = SRGBToLinear(green / 255.0f);
    float b = SRGBToLinear(blue / 255.0f);

    return 0.2125f * r + 0.7154f * g + 0.0721 * b;
}

extern "C"
JNIEXPORT void JNICALL
Java_jp_co_cyberagent_android_gpuimage_GPUImageNativeLibrary_shuffle(
        JNIEnv *jenv,
        jclass clazz,
        jobject src,
        jfloat threshold,
        jfloat strength
) {
    srand(std::chrono::system_clock::now().time_since_epoch().count());
    unsigned char *srcByteBuffer;
    int result;
    AndroidBitmapInfo srcInfo;

    result = AndroidBitmap_getInfo(jenv, src, &srcInfo);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) return;

    result = AndroidBitmap_lockPixels(jenv, src, (void **) &srcByteBuffer);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) return;

    int width = srcInfo.width;
    int height = srcInfo.height;

    for (int y = 0; y < height; ++y) {
        auto pixels = reinterpret_cast<uint8_t *>(reinterpret_cast<uint8_t *>(srcByteBuffer) +
                                                  y * srcInfo.stride);

        for (int x = 0; x < width; ++x) {
            float luma = luminance(pixels[0], pixels[1], pixels[2]);

            bool overflows = (threshold >= 0) ? (luma <= threshold) : (luma > abs(threshold));

            if (overflows) {
                int startY = y - (rand() % (y + 1)) * strength;
                int endY = y + (rand() % (y + 1)) * strength;
                int startX = x - (rand() % (x + 1)) * strength;
                int endX = x + (rand() % (x + 1)) * strength;

                int ranY = startY + (rand() % (endY - startY + 1));
                int ranX = startX + (rand() % (endX - startX + 1));

                int newX = std::clamp(ranX, 0, width - 1);
                int newY = std::clamp(ranY, 0, height - 1);
                auto newPixels = reinterpret_cast<uint8_t *>(
                        reinterpret_cast<uint8_t *>(srcByteBuffer) +
                        newY * srcInfo.stride);
                newPixels += 4 * newX;

                long newR = newPixels[0];
                long newG = newPixels[1];
                long newB = newPixels[2];

                pixels[0] = std::clamp(newR, 0l, 255l);
                pixels[1] = std::clamp(newG, 0l, 255l);
                pixels[2] = std::clamp(newB, 0l, 255l);
            }

            pixels += 4;
        }
    }
    AndroidBitmap_unlockPixels(jenv, src);
}