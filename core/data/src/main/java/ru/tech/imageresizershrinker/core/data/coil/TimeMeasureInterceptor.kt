/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.data.coil

import coil3.intercept.Interceptor
import coil3.request.ImageResult
import coil3.request.transformations
import com.t8rin.logger.makeLog

internal object TimeMeasureInterceptor : Interceptor {

    override suspend fun intercept(
        chain: Interceptor.Chain
    ): ImageResult {
        val time = System.currentTimeMillis()
        val result = chain.proceed()
        val endTime = System.currentTimeMillis()

        val delta = endTime - time

        val transformations = chain.request.transformations.joinToString(", ") {
            it.toString()
        }
        if (transformations.isNotEmpty()) {
            "Time $delta ms for transformations = $transformations, with ${result.request.sizeResolver.size()}".makeLog(
                "RealImageLoader"
            )
        }
        "Time $delta ms for ${chain.size}".makeLog("RealImageLoader")

        return result
    }

}