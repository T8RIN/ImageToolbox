#include <jni.h>
#include <string>
#include "jxl/decode.h"
#include "jxl/decode_cxx.h"
#include <vector>
#include <inttypes.h>
#include "jxl/resizable_parallel_runner.h"
#include "jxl/resizable_parallel_runner_cxx.h"
#include "thread_parallel_runner.h"
#include "thread_parallel_runner_cxx.h"
#include <libyuv.h>
#include <jxl/encode.h>
#include <jxl/encode_cxx.h>
#include "android/bitmap.h"

enum jxl_colorspace {
    rgb = 1,
    rgba = 2
};

enum jxl_compression_option {
    loseless = 1,
    loosy = 2
};

/**
 * Compresses the provided pixels.
 *
 * @param pixels input pixels
 * @param xsize width of the input image
 * @param ysize height of the input image
 * @param compressed will be populated with the compressed bytes
 */
bool EncodeJxlOneshot(const std::vector<uint8_t> &pixels, const uint32_t xsize,
                      const uint32_t ysize, std::vector<uint8_t> *compressed,
                      jxl_colorspace colorspace, jxl_compression_option compression_option,
                      float compression_distance) {
    auto enc = JxlEncoderMake(/*memory_manager=*/nullptr);
    auto runner = JxlThreadParallelRunnerMake(
            /*memory_manager=*/nullptr,
                               JxlThreadParallelRunnerDefaultNumWorkerThreads());
    if (JXL_ENC_SUCCESS != JxlEncoderSetParallelRunner(enc.get(),
                                                       JxlThreadParallelRunner,
                                                       runner.get())) {
        return false;
    }

    JxlPixelFormat pixel_format;
    switch (colorspace) {
        case rgb:
            pixel_format = {3, JXL_TYPE_UINT8, JXL_LITTLE_ENDIAN, 0};
            break;
        case rgba:
            pixel_format = {4, JXL_TYPE_UINT8, JXL_LITTLE_ENDIAN, 0};
            break;
    }

    JxlBasicInfo basic_info;
    JxlEncoderInitBasicInfo(&basic_info);
    basic_info.xsize = xsize;
    basic_info.ysize = ysize;
    basic_info.bits_per_sample = 32;
    basic_info.exponent_bits_per_sample = 8;
    basic_info.uses_original_profile = compression_option == loosy ? JXL_FALSE : JXL_TRUE;
    basic_info.num_color_channels = 3;

    if (colorspace == rgba) {
        basic_info.num_extra_channels = 1;
        basic_info.alpha_bits = 8;
    }

    if (JXL_ENC_SUCCESS != JxlEncoderSetBasicInfo(enc.get(), &basic_info)) {
        return false;
    }

    switch (colorspace) {
        case rgb:
            basic_info.num_color_channels = 3;
            break;
        case rgba:
            basic_info.num_color_channels = 4;
            JxlExtraChannelInfo channelInfo;
            JxlEncoderInitExtraChannelInfo(JXL_CHANNEL_ALPHA, &channelInfo);
            channelInfo.bits_per_sample = 8;
            channelInfo.alpha_premultiplied = false;
            if (JXL_ENC_SUCCESS != JxlEncoderSetExtraChannelInfo(enc.get(), 0, &channelInfo)) {
                return false;
            }
            break;
    }

    JxlColorEncoding color_encoding = {};
    JxlColorEncodingSetToSRGB(&color_encoding, pixel_format.num_channels < 3);
    if (JXL_ENC_SUCCESS !=
        JxlEncoderSetColorEncoding(enc.get(), &color_encoding)) {
        return false;
    }

    JxlEncoderFrameSettings *frame_settings =
            JxlEncoderFrameSettingsCreate(enc.get(), nullptr);

    if (JXL_ENC_SUCCESS !=
        JxlEncoderAddImageFrame(frame_settings, &pixel_format,
                                (void *) pixels.data(),
                                sizeof(uint8_t) * pixels.size())) {
        return false;
    }

    if (compression_option == loseless &&
        JXL_ENC_SUCCESS != JxlEncoderSetFrameDistance(frame_settings, JXL_TRUE)) {
        return false;
    } else if (compression_option == loosy &&
               JXL_ENC_SUCCESS !=
               JxlEncoderSetFrameDistance(frame_settings, compression_distance)) {
        return false;
    }

    JxlEncoderCloseInput(enc.get());

    compressed->resize(64);
    uint8_t *next_out = compressed->data();
    size_t avail_out = compressed->size() - (next_out - compressed->data());
    JxlEncoderStatus process_result = JXL_ENC_NEED_MORE_OUTPUT;
    while (process_result == JXL_ENC_NEED_MORE_OUTPUT) {
        process_result = JxlEncoderProcessOutput(enc.get(), &next_out, &avail_out);
        if (process_result == JXL_ENC_NEED_MORE_OUTPUT) {
            size_t offset = next_out - compressed->data();
            compressed->resize(compressed->size() * 2);
            next_out = compressed->data() + offset;
            avail_out = compressed->size() - offset;
        }
    }
    compressed->resize(next_out - compressed->data());
    if (JXL_ENC_SUCCESS != process_result) {
        return false;
    }

    return true;
}

bool DecodeJpegXlOneShot(const uint8_t *jxl, size_t size,
                         std::vector<uint8_t> *pixels, size_t *xsize,
                         size_t *ysize, std::vector<uint8_t> *icc_profile) {
    // Multi-threaded parallel runner.
    auto runner = JxlResizableParallelRunnerMake(nullptr);

    auto dec = JxlDecoderMake(nullptr);
    if (JXL_DEC_SUCCESS !=
        JxlDecoderSubscribeEvents(dec.get(), JXL_DEC_BASIC_INFO |
                                             JXL_DEC_COLOR_ENCODING |
                                             JXL_DEC_FULL_IMAGE)) {
        return false;
    }

    if (JXL_DEC_SUCCESS != JxlDecoderSetParallelRunner(dec.get(),
                                                       JxlResizableParallelRunner,
                                                       runner.get())) {
        return false;
    }

    JxlBasicInfo info;
    JxlPixelFormat format = {4, JXL_TYPE_UINT8, JXL_LITTLE_ENDIAN, 0};

    JxlDecoderSetInput(dec.get(), jxl, size);
    JxlDecoderCloseInput(dec.get());

    for (;;) {
        JxlDecoderStatus status = JxlDecoderProcessInput(dec.get());

        if (status == JXL_DEC_ERROR) {
            return false;
        } else if (status == JXL_DEC_NEED_MORE_INPUT) {
            return false;
        } else if (status == JXL_DEC_BASIC_INFO) {
            if (JXL_DEC_SUCCESS != JxlDecoderGetBasicInfo(dec.get(), &info)) {
                return false;
            }
            *xsize = info.xsize;
            *ysize = info.ysize;
            JxlResizableParallelRunnerSetThreads(
                    runner.get(),
                    JxlResizableParallelRunnerSuggestThreads(info.xsize, info.ysize));
        } else if (status == JXL_DEC_COLOR_ENCODING) {
            // Get the ICC color profile of the pixel data
            size_t icc_size;
            if (JXL_DEC_SUCCESS !=
                JxlDecoderGetICCProfileSize(dec.get(), JXL_COLOR_PROFILE_TARGET_DATA,
                                            &icc_size)) {
                return false;
            }
            icc_profile->resize(icc_size);
            if (JXL_DEC_SUCCESS != JxlDecoderGetColorAsICCProfile(
                    dec.get(), JXL_COLOR_PROFILE_TARGET_DATA,
                    icc_profile->data(), icc_profile->size())) {
                return false;
            }
        } else if (status == JXL_DEC_NEED_IMAGE_OUT_BUFFER) {
            size_t buffer_size;
            if (JXL_DEC_SUCCESS !=
                JxlDecoderImageOutBufferSize(dec.get(), &format, &buffer_size)) {
                return false;
            }
            if (buffer_size != *xsize * *ysize * 4) {
                return false;
            }
            pixels->resize(*xsize * *ysize * 4);
            void *pixels_buffer = (void *) pixels->data();
            size_t pixels_buffer_size = pixels->size() * sizeof(uint8_t);
            if (JXL_DEC_SUCCESS != JxlDecoderSetImageOutBuffer(dec.get(), &format,
                                                               pixels_buffer,
                                                               pixels_buffer_size)) {
                return false;
            }
        } else if (status == JXL_DEC_FULL_IMAGE) {
            // Nothing to do. Do not yet return. If the image is an animation, more
            // full frames may be decoded. This example only keeps the last one.
        } else if (status == JXL_DEC_SUCCESS) {
            // All decoding successfully finished.
            // It's not required to call JxlDecoderReleaseInput(dec.get()) here since
            // the decoder will be destroyed.
            return true;
        } else {
            return false;
        }
    }
}

bool DecodeBasicInfo(const uint8_t *jxl, size_t size,
                     std::vector<uint8_t> *pixels, size_t *xsize,
                     size_t *ysize) {
    // Multi-threaded parallel runner.
    auto runner = JxlResizableParallelRunnerMake(nullptr);

    auto dec = JxlDecoderMake(nullptr);
    if (JXL_DEC_SUCCESS !=
        JxlDecoderSubscribeEvents(dec.get(), JXL_DEC_BASIC_INFO |
                                             JXL_DEC_COLOR_ENCODING |
                                             JXL_DEC_FULL_IMAGE)) {
        return false;
    }

    if (JXL_DEC_SUCCESS != JxlDecoderSetParallelRunner(dec.get(),
                                                       JxlResizableParallelRunner,
                                                       runner.get())) {
        return false;
    }

    JxlBasicInfo info;
    JxlPixelFormat format = {4, JXL_TYPE_UINT8, JXL_LITTLE_ENDIAN, 0};

    JxlDecoderSetInput(dec.get(), jxl, size);
    JxlDecoderCloseInput(dec.get());

    for (;;) {
        JxlDecoderStatus status = JxlDecoderProcessInput(dec.get());

        if (status == JXL_DEC_ERROR) {
            return false;
        } else if (status == JXL_DEC_NEED_MORE_INPUT) {
            return false;
        } else if (status == JXL_DEC_BASIC_INFO) {
            if (JXL_DEC_SUCCESS != JxlDecoderGetBasicInfo(dec.get(), &info)) {
                return false;
            }
            *xsize = info.xsize;
            *ysize = info.ysize;
            return true;
        } else if (status == JXL_DEC_NEED_IMAGE_OUT_BUFFER) {
            return false;
        } else if (status == JXL_DEC_FULL_IMAGE) {
            return false;
        } else if (status == JXL_DEC_SUCCESS) {
            return false;
        } else {
            return false;
        }
    }
}


jint throwInvalidJXLException(JNIEnv *env) {
    jclass exClass;
    exClass = env->FindClass("com/awxkee/jxlcoder/InvalidJXLException");
    return env->ThrowNew(exClass, "");
}

jint throwPixelsException(JNIEnv *env) {
    jclass exClass;
    exClass = env->FindClass("com/awxkee/jxlcoder/LockPixelsException");
    return env->ThrowNew(exClass, "");
}

jint throwHardwareBitmapException(JNIEnv *env) {
    jclass exClass;
    exClass = env->FindClass("com/awxkee/jxlcoder/HardwareBitmapException");
    return env->ThrowNew(exClass, "");
}

jint throwInvalidPixelsFormat(JNIEnv *env) {
    jclass exClass;
    exClass = env->FindClass("com/awxkee/jxlcoder/InvalidPixelsFormatException");
    return env->ThrowNew(exClass, "");
}

jint throwCantCompressImage(JNIEnv *env) {
    jclass exClass;
    exClass = env->FindClass("com/awxkee/jxlcoder/JXLCoderCompressionException");
    return env->ThrowNew(exClass, "");
}

jint throwInvalidColorSpaceException(JNIEnv *env) {
    jclass exClass;
    exClass = env->FindClass("com/awxkee/jxlcoder/InvalidColorSpaceException");
    return env->ThrowNew(exClass, "");
}

jint throwInvalidCompressionOptionException(JNIEnv *env) {
    jclass exClass;
    exClass = env->FindClass("com/awxkee/jxlcoder/InvalidCompressionOptionException");
    return env->ThrowNew(exClass, "");
}

jint throwInvalidSizeException(JNIEnv *env) {
    jclass exClass;
    exClass = env->FindClass("com/awxkee/jxlcoder/InvalidSizeParameterException");
    return env->ThrowNew(exClass, "");
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_awxkee_jxlcoder_JxlCoder_decodeImpl(JNIEnv *env, jobject thiz, jbyteArray byte_array) {
    auto totalLength = env->GetArrayLength(byte_array);
    std::shared_ptr<void> srcBuffer(static_cast<char *>(malloc(totalLength)),
                                    [](void *b) { free(b); });
    env->GetByteArrayRegion(byte_array, 0, totalLength, reinterpret_cast<jbyte *>(srcBuffer.get()));

    std::vector<uint8_t> rgbaPixels;
    std::vector<uint8_t> icc_profile;
    size_t xsize = 0, ysize = 0;
    if (!DecodeJpegXlOneShot(reinterpret_cast<uint8_t *>(srcBuffer.get()), totalLength, &rgbaPixels,
                             &xsize, &ysize,
                             &icc_profile)) {
        throwInvalidJXLException(env);
        return nullptr;
    }

    jclass bitmapConfig = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID rgba8888FieldID = env->GetStaticFieldID(bitmapConfig, "ARGB_8888",
                                                     "Landroid/graphics/Bitmap$Config;");
    jobject rgba8888Obj = env->GetStaticObjectField(bitmapConfig, rgba8888FieldID);

    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMethodID = env->GetStaticMethodID(bitmapClass, "createBitmap",
                                                            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject bitmapObj = env->CallStaticObjectMethod(bitmapClass, createBitmapMethodID,
                                                    static_cast<jint>(xsize),
                                                    static_cast<jint>(ysize), rgba8888Obj);
    auto returningLength = rgbaPixels.size() / sizeof(uint32_t);
    jintArray pixels = env->NewIntArray((jsize) returningLength);

    libyuv::ABGRToARGB(rgbaPixels.data(), static_cast<int>(xsize * 4), rgbaPixels.data(),
                       static_cast<int>(xsize * 4), (int) xsize,
                       (int) ysize);

    env->SetIntArrayRegion(pixels, 0, (jsize) returningLength,
                           reinterpret_cast<const jint *>(rgbaPixels.data()));
    rgbaPixels.clear();
    jmethodID setPixelsMid = env->GetMethodID(bitmapClass, "setPixels", "([IIIIIII)V");
    env->CallVoidMethod(bitmapObj, setPixelsMid, pixels, 0,
                        static_cast<jint >(xsize), 0, 0,
                        static_cast<jint>(xsize),
                        static_cast<jint>(ysize));
    env->DeleteLocalRef(pixels);

    return bitmapObj;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_awxkee_jxlcoder_JxlCoder_decodeSampledImpl(JNIEnv *env, jobject thiz,
                                                    jbyteArray byte_array, jint width,
                                                    jint height) {
    if (width <= 0 || height <= 0) {
        throwInvalidSizeException(env);
        return nullptr;
    }
    auto totalLength = env->GetArrayLength(byte_array);
    std::shared_ptr<void> srcBuffer(static_cast<char *>(malloc(totalLength)),
                                    [](void *b) { free(b); });
    env->GetByteArrayRegion(byte_array, 0, totalLength, reinterpret_cast<jbyte *>(srcBuffer.get()));

    std::vector<uint8_t> rgbaPixels;
    std::vector<uint8_t> icc_profile;
    size_t xsize = 0, ysize = 0;
    if (!DecodeJpegXlOneShot(reinterpret_cast<uint8_t *>(srcBuffer.get()), totalLength, &rgbaPixels,
                             &xsize, &ysize,
                             &icc_profile)) {
        throwInvalidJXLException(env);
        return nullptr;
    }

    jclass bitmapConfig = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID rgba8888FieldID = env->GetStaticFieldID(bitmapConfig, "ARGB_8888",
                                                     "Landroid/graphics/Bitmap$Config;");
    jobject rgba8888Obj = env->GetStaticObjectField(bitmapConfig, rgba8888FieldID);

    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMethodID = env->GetStaticMethodID(bitmapClass, "createBitmap",
                                                            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject bitmapObj = env->CallStaticObjectMethod(bitmapClass, createBitmapMethodID,
                                                    static_cast<jint>(width),
                                                    static_cast<jint>(height), rgba8888Obj);

    std::vector<uint8_t> newImageData;
    newImageData.resize(width * height * 4 * sizeof(uint8_t));

    libyuv::ABGRToARGB(rgbaPixels.data(), static_cast<int>(xsize * 4), rgbaPixels.data(),
                       static_cast<int>(xsize * 4), (int) xsize,
                       (int) ysize);


    libyuv::ARGBScale(rgbaPixels.data(), static_cast<int>(xsize * 4), static_cast<int>(xsize),
                      static_cast<int>(ysize),
                      newImageData.data(), width * 4, width, height, libyuv::kFilterBox);

    rgbaPixels.clear();
    rgbaPixels.resize(1);

    auto returningLength = newImageData.size() / sizeof(uint32_t);
    jintArray pixels = env->NewIntArray((jsize) returningLength);

    env->SetIntArrayRegion(pixels, 0, (jsize) (newImageData.size() / sizeof(uint32_t)),
                           reinterpret_cast<const jint *>(newImageData.data()));

    newImageData.resize(1);

    jmethodID setPixelsMid = env->GetMethodID(bitmapClass, "setPixels", "([IIIIIII)V");
    env->CallVoidMethod(bitmapObj, setPixelsMid, pixels, 0,
                        static_cast<jint >(width), 0, 0,
                        static_cast<jint>(width),
                        static_cast<jint>(height));
    env->DeleteLocalRef(pixels);

    return bitmapObj;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_awxkee_jxlcoder_JxlCoder_encodeImpl(JNIEnv *env, jobject thiz, jobject bitmap,
                                             jint javaColorSpace, jint javaCompressionOption,
                                             jfloat compression_level) {
    auto colorspace = static_cast<jxl_colorspace>(javaColorSpace);
    if (!colorspace) {
        throwInvalidColorSpaceException(env);
        return static_cast<jbyteArray>(nullptr);
    }
    auto compression_option = static_cast<jxl_compression_option>(javaCompressionOption);
    if (!compression_option) {
        throwInvalidCompressionOptionException(env);
        return static_cast<jbyteArray>(nullptr);
    }

    if (compression_option == loosy && (compression_level < 0 || compression_level > 15)) {
        throwInvalidCompressionOptionException(env);
        return static_cast<jbyteArray>(nullptr);
    }

    AndroidBitmapInfo info;
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        throwPixelsException(env);
        return static_cast<jbyteArray>(nullptr);
    }

    if (info.flags & ANDROID_BITMAP_FLAGS_IS_HARDWARE) {
        throwHardwareBitmapException(env);
        return static_cast<jbyteArray>(nullptr);
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        throwInvalidPixelsFormat(env);
        return static_cast<jbyteArray>(nullptr);
    }

    void *addr;
    if (AndroidBitmap_lockPixels(env, bitmap, &addr) != 0) {
        throwPixelsException(env);
        return static_cast<jbyteArray>(nullptr);
    }

    std::vector<uint8_t> rgbaPixels;
    rgbaPixels.resize(info.stride * info.height);
    memcpy(rgbaPixels.data(), addr, info.stride * info.height);
    AndroidBitmap_unlockPixels(env, bitmap);

    std::vector<uint8_t> rgbPixels;
    switch (colorspace) {
        case rgb:
            rgbPixels.resize(info.width * info.height * 3);
            libyuv::ARGBToRGB24(rgbaPixels.data(), static_cast<int>(info.stride), rgbPixels.data(),
                                static_cast<int>(info.width * 3), static_cast<int>(info.width),
                                static_cast<int>(info.height));
            break;
        case rgba:
            rgbPixels.resize(info.stride * info.height);
            libyuv::ARGBToABGR(rgbaPixels.data(), static_cast<int>(info.stride), rgbPixels.data(),
                               static_cast<int>(info.stride), static_cast<int>(info.width),
                               static_cast<int>(info.height));
            break;
    }
    rgbaPixels.clear();
    rgbaPixels.resize(1);

    std::vector<uint8_t> compressedVector;

    if (!EncodeJxlOneshot(rgbPixels, info.width, info.height, &compressedVector, colorspace,
                          compression_option, compression_level)) {
        throwCantCompressImage(env);
        return static_cast<jbyteArray>(nullptr);
    }

    jbyteArray byteArray = env->NewByteArray((jsize) compressedVector.size());
    char *memBuf = (char *) ((void *) compressedVector.data());
    env->SetByteArrayRegion(byteArray, 0, (jint) compressedVector.size(),
                            reinterpret_cast<const jbyte *>(memBuf));
    compressedVector.clear();
    return byteArray;
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_awxkee_jxlcoder_JxlCoder_getSizeImpl(JNIEnv *env, jobject thiz, jbyteArray byte_array) {
    auto totalLength = env->GetArrayLength(byte_array);
    std::shared_ptr<void> srcBuffer(static_cast<char *>(malloc(totalLength)),
                                    [](void *b) { free(b); });
    env->GetByteArrayRegion(byte_array, 0, totalLength, reinterpret_cast<jbyte *>(srcBuffer.get()));

    std::vector<uint8_t> rgbaPixels;
    std::vector<uint8_t> icc_profile;
    size_t xsize = 0, ysize = 0;
    if (!DecodeBasicInfo(reinterpret_cast<uint8_t *>(srcBuffer.get()), totalLength, &rgbaPixels,
                         &xsize, &ysize)) {
        return nullptr;
    }

    jclass sizeClass = env->FindClass("android/util/Size");
    jmethodID methodID = env->GetMethodID(sizeClass, "<init>", "(II)V");
    auto sizeObject = env->NewObject(sizeClass, methodID, static_cast<jint >(xsize),
                                     static_cast<jint>(ysize));
    return sizeObject;
}