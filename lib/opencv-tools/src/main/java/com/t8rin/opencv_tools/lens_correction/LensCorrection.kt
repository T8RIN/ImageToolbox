/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("LocalVariableName", "unused")

package com.t8rin.opencv_tools.lens_correction

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.t8rin.opencv_tools.lens_correction.model.LCException
import com.t8rin.opencv_tools.lens_correction.model.LCException.InvalidCalibDimensions
import com.t8rin.opencv_tools.lens_correction.model.LCException.InvalidDistortionCoeffs
import com.t8rin.opencv_tools.lens_correction.model.LCException.InvalidMatrixSize
import com.t8rin.opencv_tools.lens_correction.model.LCException.MissingFisheyeParams
import com.t8rin.opencv_tools.lens_correction.model.LensProfile
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import org.json.JSONArray
import org.json.JSONObject
import org.opencv.calib3d.Calib3d
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.InputStream
import java.io.Reader
import kotlin.contracts.contract

object LensCorrection : OpenCV() {

    fun undistort(
        bitmap: Bitmap,
        lensDataUri: Uri,
        intensity: Double = 1.0
    ): Bitmap = undistort(
        bitmap = bitmap,
        lensData = context.contentResolver.openInputStream(lensDataUri)!!,
        intensity = intensity
    )

    fun undistort(
        bitmap: Bitmap,
        lensData: InputStream,
        intensity: Double = 1.0
    ): Bitmap = undistort(
        bitmap = bitmap,
        lensDataJson = lensData.bufferedReader().use(Reader::readText),
        intensity = intensity
    )

    fun undistort(
        bitmap: Bitmap,
        lensDataJson: String,
        intensity: Double = 1.0
    ): Bitmap = undistort(
        bitmap = bitmap,
        lensProfile = LensProfile.fromJson(lensDataJson).withIntensity(intensity)
    )

    fun undistort(
        bitmap: Bitmap,
        lensProfile: LensProfile
    ): Bitmap = undistortImpl(
        bitmap = bitmap,
        cameraMatrix = lensProfile.cameraMatrix,
        distortionCoeffs = lensProfile.distortionCoeffs,
        calibWidth = lensProfile.calibWidth,
        calibHeight = lensProfile.calibHeight
    )


    private fun undistortImpl(
        bitmap: Bitmap,
        cameraMatrix: List<List<Double>>,
        distortionCoeffs: List<Double>,
        calibWidth: Int,
        calibHeight: Int
    ): Bitmap {
        lcCheck(
            value = distortionCoeffs.size == 4,
            message = InvalidDistortionCoeffs()
        )

        val rgbaMat = bitmap.toMat()
        val K = Mat(3, 3, CvType.CV_64F)
        val D = Mat(4, 1, CvType.CV_64F)
        val undistorted = Mat()

        try {
            Imgproc.cvtColor(rgbaMat, rgbaMat, Imgproc.COLOR_RGBA2RGB)

            cameraMatrix.forEachIndexed { i, row ->
                lcCheck(
                    value = row.size == 3,
                    message = InvalidMatrixSize()
                )

                row.forEachIndexed { j, value ->
                    K.put(i, j, value)
                }
            }

            distortionCoeffs.forEachIndexed { i, v -> D.put(i, 0, -v) }

            val scaleX = bitmap.width.toDouble() / calibWidth
            val scaleY = bitmap.height.toDouble() / calibHeight

            K.put(0, 0, K.get(0, 0)[0] * scaleX) // fx
            K.put(0, 2, K.get(0, 2)[0] * scaleX) // cx
            K.put(1, 1, K.get(1, 1)[0] * scaleY) // fy
            K.put(1, 2, K.get(1, 2)[0] * scaleY) // cy

            Calib3d.fisheye_undistortImage(rgbaMat, undistorted, K, D, K, rgbaMat.size())

            Imgproc.cvtColor(undistorted, undistorted, Imgproc.COLOR_RGB2RGBA)

            return undistorted.toBitmap()
        } finally {
            rgbaMat.release()
            K.release()
            D.release()
        }
    }

    fun LensProfile.Companion.fromJson(json: String): LensProfile = JSONObject(json).run {
        if (has("name")) Log.d("LensCorrection", "name detected: ${get("name")}")

        lcCheck(
            value = has("fisheye_params"),
            message = MissingFisheyeParams()
        )

        val fisheyeParams = getJSONObject("fisheye_params")

        lcCheck(
            value = fisheyeParams.has("camera_matrix"),
            message = InvalidMatrixSize()
        )

        lcCheck(
            value = fisheyeParams.has("distortion_coeffs"),
            message = InvalidDistortionCoeffs()
        )

        val calibDim = lcCheck(
            value = safeJSONObject("calib_dimension")
                ?: safeJSONObject("orig_dimension")
                ?: safeJSONObject("output_dimension"),
            message = InvalidCalibDimensions()
        )

        val calibW = calibDim.getInt("w")
        val calibH = calibDim.getInt("h")

        lcCheck(
            value = calibW > 0 && calibH > 0,
            message = InvalidCalibDimensions()
        )

        return LensProfile(
            cameraMatrix = fisheyeParams.getCameraMatrix(),
            distortionCoeffs = fisheyeParams.getDistortionCoeffs(),
            calibWidth = calibW,
            calibHeight = calibH
        )
    }

    private fun JSONObject.getDistortionCoeffs(): List<Double> {
        val distCoeffsArray = safeJSONArray("distortion_coeffs")

        lcCheck(
            value = distCoeffsArray?.length() == 4,
            message = InvalidDistortionCoeffs()
        )

        return List(size = 4, init = distCoeffsArray::getDouble)
    }

    private fun JSONObject.getCameraMatrix(): List<List<Double>> {
        val cameraMatrixArray = safeJSONArray("camera_matrix")

        lcCheck(
            value = cameraMatrixArray?.length() == 3,
            message = InvalidMatrixSize()
        )

        return List(3) { i ->
            List(3) { j ->
                cameraMatrixArray.getJSONArray(i).getDouble(j)
            }
        }
    }

    private fun lcCheck(value: Boolean, message: LCException) {
        contract { returns() implies value }
        if (!value) throw message
    }

    private fun <T> lcCheck(value: T?, message: LCException): T {
        contract { returns() implies (value != null) }
        if (value == null) throw message else return value
    }

    private fun JSONObject.safeJSONObject(key: String): JSONObject? =
        runCatching { getJSONObject(key) }.getOrNull()

    private fun JSONObject.safeJSONArray(key: String): JSONArray? =
        runCatching { getJSONArray(key) }.getOrNull()

}