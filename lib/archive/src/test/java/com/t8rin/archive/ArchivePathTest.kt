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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArchivePathTest {

    @Test
    fun acceptsNestedRelativePath() {
        assertEquals(
            listOf("pages", "001.jpg"),
            ArchivePath.safeSegments("pages/001.jpg")
        )
    }

    @Test
    fun normalizesWindowsSeparators() {
        assertEquals(
            listOf("pages", "001.jpg"),
            ArchivePath.safeSegments("pages\\001.jpg")
        )
    }

    @Test
    fun rejectsTraversalAndAbsolutePaths() {
        assertNull(ArchivePath.safeSegments("../secret"))
        assertNull(ArchivePath.safeSegments("pages/../../secret"))
        assertNull(ArchivePath.safeSegments("/etc/passwd"))
        assertNull(ArchivePath.safeSegments("C:/Windows/file"))
    }

    @Test
    fun boundsArchivePaths() {
        assertEquals(240, ArchivePath.safeSegments("a".repeat(500))?.single()?.length)
        assertNull(ArchivePath.safeSegments("a".repeat(4097)))
        assertNull(ArchivePath.safeSegments(List(257) { "a" }.joinToString("/")))
    }
}
