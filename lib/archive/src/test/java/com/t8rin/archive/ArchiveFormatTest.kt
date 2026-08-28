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

package com.t8rin.archive

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchiveFormatTest {

    @Test
    fun exposesCompressionLevelOnlyForCompressingFormatsAndMethods() {
        assertFalse(
            ArchiveFormat.Zip.supportsCompressionLevel(
                zipCompressionMethod = ZipCompressionMethod.Store,
                sevenZipCompressionMethod = SevenZipCompressionMethod.Lzma2
            )
        )
        assertTrue(
            ArchiveFormat.Zip.supportsCompressionLevel(
                zipCompressionMethod = ZipCompressionMethod.Deflate,
                sevenZipCompressionMethod = SevenZipCompressionMethod.Lzma2
            )
        )
        assertFalse(
            ArchiveFormat.SevenZip.supportsCompressionLevel(
                zipCompressionMethod = ZipCompressionMethod.Deflate,
                sevenZipCompressionMethod = SevenZipCompressionMethod.Copy
            )
        )
        assertTrue(
            ArchiveFormat.SevenZip.supportsCompressionLevel(
                zipCompressionMethod = ZipCompressionMethod.Deflate,
                sevenZipCompressionMethod = SevenZipCompressionMethod.Lzma2
            )
        )
        assertTrue(
            ArchiveFormat.TarGzip.supportsCompressionLevel(
                zipCompressionMethod = ZipCompressionMethod.Store,
                sevenZipCompressionMethod = SevenZipCompressionMethod.Copy
            )
        )
        assertFalse(
            ArchiveFormat.Tar.supportsCompressionLevel(
                zipCompressionMethod = ZipCompressionMethod.Deflate,
                sevenZipCompressionMethod = SevenZipCompressionMethod.Lzma2
            )
        )
    }
}
