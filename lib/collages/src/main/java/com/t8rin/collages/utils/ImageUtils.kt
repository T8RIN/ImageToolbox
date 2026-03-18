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

package com.t8rin.collages.utils

import android.app.ActivityManager
import android.content.Context
import android.graphics.Matrix

internal object ImageUtils {

    class MemoryInfo {
        var availMem: Long = 0
        var totalMem: Long = 0
    }

    fun getMemoryInfo(context: Context): MemoryInfo {
        val info = MemoryInfo()
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        info.availMem = memInfo.availMem
        info.totalMem = memInfo.totalMem
        return info
    }

    fun createMatrixToDrawImageInCenterView(
        viewWidth: Float,
        viewHeight: Float,
        imageWidth: Float,
        imageHeight: Float
    ): Matrix {
        val ratioWidth = viewWidth / imageWidth
        val ratioHeight = viewHeight / imageHeight
        val ratio = Math.max(ratioWidth, ratioHeight)
        val dx = (viewWidth - imageWidth) / 2.0f
        val dy = (viewHeight - imageHeight) / 2.0f
        val result = Matrix()
        result.postTranslate(dx, dy)
        result.postScale(ratio, ratio, viewWidth / 2, viewHeight / 2)
        return result
    }

}
