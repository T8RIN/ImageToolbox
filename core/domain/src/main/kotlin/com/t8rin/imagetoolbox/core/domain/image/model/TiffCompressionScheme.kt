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

package com.t8rin.imagetoolbox.core.domain.image.model

enum class TiffCompressionScheme(val value: Int) {
    /**
     * No compression
     */
    NONE(1),

    /**
     * CCITT modified Huffman RLE
     */
    CCITTRLE(2),

    /**
     * CCITT Group 3 fax encoding
     */
    CCITTFAX3(3),

    /**
     * CCITT Group 4 fax encoding
     */
    CCITTFAX4(4),

    /**
     * LZW
     */
    LZW(5),

    /**
     * JPEG ('new-style' JPEG)
     */
    JPEG(7),
    PACKBITS(32773),
    DEFLATE(32946),
    ADOBE_DEFLATE(8),

    /**
     * All other compression schemes
     */
    OTHER(0);

    companion object {
        val safeEntries by lazy { TiffCompressionScheme.entries - OTHER }
    }
}