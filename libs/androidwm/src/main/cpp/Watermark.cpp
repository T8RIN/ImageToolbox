/*
 *    Copyright 2018 huangyz0918
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


/**
 * All the native methods in AndroidWM (https://github.com/huangyz0918/AndroidWM).
 *
 * @author huangyz0918 (huangyz0918@gmail.com)
 */

#include <jni.h>
#include <string>
#include <bitset>
#include <sstream>
#include <android/log.h>

using namespace std;

#define  LOG_TAG    "androidWM-native"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define  LOGF(...)  __android_log_print(ANDROID_LOG_FATAL, LOG_TAG, __VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

bool convolve1D(jdouble *in, jdouble *kernel, jdouble *out, jsize kernelSize, jsize dataSize);

/**
 * native method for calculating the Convolution 1D.
 */
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_watermark_androidwm_utils_StringUtils_calConv1D(JNIEnv *env, jobject instance,
                                                         jdoubleArray inputArray1_,
                                                         jdoubleArray inputArray2_) {
    jdouble *inputArray1 = env->GetDoubleArrayElements(inputArray1_, NULL);
    jdouble *inputArray2 = env->GetDoubleArrayElements(inputArray2_, NULL);

    jsize size1 = env->GetArrayLength(inputArray1_);
    jsize size2 = env->GetArrayLength(inputArray2_);
    jsize outSize = size1 + size2 - 1;
    jsize kernelSize;

    if (size1 > size2) {
        kernelSize = size1;
    } else {
        kernelSize = size2;
    }

    jdoubleArray outputArray = env->NewDoubleArray(outSize);
    jdouble *outputValues = env->GetDoubleArrayElements(outputArray, NULL);
    convolve1D(inputArray1, inputArray2, outputValues, kernelSize, outSize);

    env->SetDoubleArrayRegion(outputArray, 0, outSize, outputValues);
    env->ReleaseDoubleArrayElements(inputArray1_, inputArray1, 0);
    env->ReleaseDoubleArrayElements(inputArray2_, inputArray2, 0);
    return outputArray;
}

bool convolve1D(jdouble *in, jdouble *kernel, jdouble *out, jsize kernelSize, jsize dataSize) {
    int i, j, k;
    if (!in || !out || !kernel) return false;
    if (dataSize <= 0 || kernelSize <= 0) return false;

    for (i = kernelSize - 1; i < dataSize; ++i) {
        out[i] = 0;

        for (j = i, k = 0; k < kernelSize; --j, ++k)
            out[i] += in[j] * kernel[k];
    }

    for (i = 0; i < kernelSize - 1; ++i) {
        out[i] = 0;

        for (j = i, k = 0; j >= 0; --j, ++k)
            out[i] += in[j] * kernel[k];
    }

    return true;
}


/*
 * Convent the Jstring to the C++ style string (std::string).
 * */
string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes,
                                                                       env->NewStringUTF("UTF-8"));
    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte *pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    string ret = string((char *) pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}


/**
 * Converting a {@link String} text into a binary text.
 * <p>
 * This is the native version.
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_watermark_androidwm_utils_StringUtils_stringToBinary(JNIEnv *env, jobject instance,
                                                              jstring inputText_) {
    const char *inputText = env->GetStringUTFChars(inputText_, 0);
    if (inputText == NULL) {
        return NULL;
    }

    string input = jstring2string(env, inputText_);
    string result;

    for (int i = 0; i < input.length(); i++) {
        string temp = bitset<8>(input[i]).to_string();
        result.append(temp);
    }

    env->ReleaseStringUTFChars(inputText_, inputText);

    return env->NewStringUTF(result.c_str());
}


/**
 * String to integer array.
 * <p>
 * This is the native version.
 */
extern "C"
JNIEXPORT jintArray JNICALL
Java_com_watermark_androidwm_utils_StringUtils_stringToIntArray(JNIEnv *env, jobject instance,
                                                                jstring inputString_) {
    const char *inputString = env->GetStringUTFChars(inputString_, 0);
    string input = jstring2string(env, inputString_);

    int result[input.length()];

    jsize size = env->GetStringLength(inputString_);
    jintArray resultArray = env->NewIntArray(size);

    for (int i = 0; i < input.length(); ++i) {
        result[i] = input[i] - '0';
    }

    env->SetIntArrayRegion(resultArray, 0, size, result);
    env->ReleaseStringUTFChars(inputString_, inputString);

    return resultArray;
}


/**
 * Converting a binary string to a ASCII string.
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_watermark_androidwm_utils_StringUtils_binaryToString(JNIEnv *env, jobject instance,
                                                              jstring inputText_) {
    const char *inputText = env->GetStringUTFChars(inputText_, 0);
    string inputString = jstring2string(env, inputText_);

    stringstream stream(inputString);
    string output;

    while (stream.good()) {
        bitset<8> bits;
        stream >> bits;
        char c = char(bits.to_ulong());
        output += c;
    }

    jstring outputString = env->NewStringUTF(output.c_str());
    env->ReleaseStringUTFChars(inputText_, inputText);
    return outputString;
}

/**
 * Replace the wrong rgb number in a form of binary,
 * the only case is 0 - 1 = 9, so, we need to replace
 * all nines to zero.
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_watermark_androidwm_utils_StringUtils_replaceNines(JNIEnv *env, jobject instance,
                                                            jintArray inputArray_) {
    jint *inputArray = env->GetIntArrayElements(inputArray_, NULL);
    jsize size = env->GetArrayLength(inputArray_);

    for (int i = 0; i < size; i++) {
        if (inputArray[i] == 9) {
            inputArray[i] = 0;
        }
    }

    env->ReleaseIntArrayElements(inputArray_, inputArray, 0);
}

/**
 * Int array to string.
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_watermark_androidwm_utils_StringUtils_intArrayToString(JNIEnv *env, jobject instance,
                                                                jintArray inputArray_) {
    jint *inputArray = env->GetIntArrayElements(inputArray_, NULL);
    jsize size = env->GetArrayLength(inputArray_);
    ostringstream oss("");

    for (int i = 0; i < size; ++i) {
        oss << inputArray[i];
    }

    string output = oss.str();

    env->ReleaseIntArrayElements(inputArray_, inputArray, 0);
    return env->NewStringUTF(output.c_str());
}
