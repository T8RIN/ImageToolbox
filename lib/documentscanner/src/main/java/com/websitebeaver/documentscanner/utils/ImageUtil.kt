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

package com.websitebeaver.documentscanner.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.t8rin.exif.ExifInterface
import com.websitebeaver.documentscanner.extensions.distance
import com.websitebeaver.documentscanner.extensions.toOpenCVPoint
import com.websitebeaver.documentscanner.models.Quad
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import kotlin.math.min

/**
 * This class contains helper functions for processing images
 *
 * @constructor creates image util
 */
class ImageUtil {
    /**
     * get image matrix from file path
     *
     * @param filePath image is saved here
     * @return image matrix
     */
    private fun getImageMatrixFromFilePath(filePath: String): Mat {
        // read image as matrix using OpenCV
        val image: Mat = Imgcodecs.imread(filePath)

        // if OpenCV fails to read the image then it's empty
        if (!image.empty()) {
            // convert image to RGB color space since OpenCV reads it using BGR color space
            Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB)
            return image
        }

        if (!File(filePath).exists()) {
            throw Exception("File doesn't exist - $filePath")
        }

        if (!File(filePath).canRead()) {
            throw Exception("You don't have permission to read $filePath")
        }

        // try reading image without OpenCV
        var imageBitmap = BitmapFactory.decodeFile(filePath)
        val rotation = when (ExifInterface(filePath).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        imageBitmap = Bitmap.createBitmap(
            imageBitmap,
            0,
            0,
            imageBitmap.width,
            imageBitmap.height,
            Matrix().apply { postRotate(rotation.toFloat()) },
            true
        )
        Utils.bitmapToMat(imageBitmap, image)

        return image
    }

    /**
     * get bitmap image from file path
     *
     * @param filePath image is saved here
     * @return image bitmap
     */
    fun getImageFromFilePath(filePath: String): Bitmap {
        // read image as matrix using OpenCV
        val image: Mat = this.getImageMatrixFromFilePath(filePath)

        // convert image matrix to bitmap
        val bitmap = createBitmap(image.cols(), image.rows())
        Utils.matToBitmap(image, bitmap)
        return bitmap
    }

    /**
     * take a photo with a document, crop everything out but document, and force it to display
     * as a rectangle
     *
     * @param photoFilePath original image is saved here
     * @param corners the 4 document corners
     * @return bitmap with cropped and warped document
     */
    fun crop(photoFilePath: String, corners: Quad): Bitmap {
        // read image with OpenCV
        val image = this.getImageMatrixFromFilePath(photoFilePath)

        // convert top left, top right, bottom right, and bottom left document corners from
        // Android points to OpenCV points
        val tLC = corners.topLeftCorner.toOpenCVPoint()
        val tRC = corners.topRightCorner.toOpenCVPoint()
        val bRC = corners.bottomRightCorner.toOpenCVPoint()
        val bLC = corners.bottomLeftCorner.toOpenCVPoint()

        // Calculate the document edge distances. The user might take a skewed photo of the
        // document, so the top left corner to top right corner distance might not be the same
        // as the bottom left to bottom right corner. We could take an average of the 2, but
        // this takes the smaller of the 2. It does the same for height.
        val width = min(tLC.distance(tRC), bLC.distance(bRC))
        val height = min(tLC.distance(bLC), tRC.distance(bRC))

        // create empty image matrix with cropped and warped document width and height
        val croppedImage = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(width, 0.0),
            Point(width, height),
            Point(0.0, height),
        )

        // This crops the document out of the rest of the photo. Since the user might take a
        // skewed photo instead of a straight on photo, the document might be rotated and
        // skewed. This corrects that problem. output is an image matrix that contains the
        // corrected image after this fix.
        val output = Mat()
        Imgproc.warpPerspective(
            image,
            output,
            Imgproc.getPerspectiveTransform(
                MatOfPoint2f(tLC, tRC, bRC, bLC),
                croppedImage
            ),
            Size(width, height)
        )

        // convert output image matrix to bitmap
        val croppedBitmap = createBitmap(output.cols(), output.rows())
        Utils.matToBitmap(output, croppedBitmap)

        return croppedBitmap
    }

    /**
     * get bitmap image from file uri
     *
     * @param fileUriString image is saved here and starts with file:///
     * @return bitmap image
     */
    fun readBitmapFromFileUriString(
        fileUriString: String,
        contentResolver: ContentResolver
    ): Bitmap {
        return BitmapFactory.decodeStream(
            contentResolver.openInputStream(fileUriString.toUri())
        )
    }
}