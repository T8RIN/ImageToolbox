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

package com.t8rin.imagetoolbox.feature.pdf_tools.domain.model

data class PrintPdfParams(
    val orientation: PageOrientation = PageOrientation.ORIGINAL,
    val pageSize: PageSize = PageSize.Auto,
    val pagesPerSheet: Int = 1,
    val marginPercent: Float = 0f
) {
    val pageSizeFinal = if (pageSize == PageSize.Auto) {
        null
    } else {
        when (orientation) {
            PageOrientation.ORIGINAL -> null
            PageOrientation.VERTICAL -> pageSize
            PageOrientation.HORIZONTAL -> pageSize.run { copy(width = height, height = width) }
        }
    }

    val gridSize = pagesMapping.getOrDefault(pagesPerSheet, 1 to 1).let {
        when (orientation) {
            PageOrientation.ORIGINAL,
            PageOrientation.VERTICAL -> it

            PageOrientation.HORIZONTAL -> it.second to it.first
        }
    }

    companion object {
        val pagesMapping by lazy {
            mapOf(
                1 to (1 to 1),
                2 to (2 to 1),
                4 to (2 to 2),
                6 to (3 to 2),
                8 to (4 to 2),
                9 to (3 to 3),
                12 to (4 to 3),
                16 to (4 to 4)
            )
        }

        val pageRange by lazy {
            pagesMapping.keys.sorted().run { first()..last() }
        }
    }
}