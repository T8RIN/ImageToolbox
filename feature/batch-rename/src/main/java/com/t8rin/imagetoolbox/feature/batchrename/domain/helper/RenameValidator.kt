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

package com.t8rin.imagetoolbox.feature.batchrename.domain.helper

import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameValidationError
import com.t8rin.imagetoolbox.feature.batchrename.presentation.components.RenamePreview

internal object RenameValidator {

    private const val MAX_FILENAME_BYTES = 255

    fun validate(
        pattern: String,
        previews: List<RenamePreview>
    ): RenameValidationError? {
        if (pattern.isBlank()) return RenameValidationError.EmptyPattern
        if (RenamePatterns.containsDisallowed(pattern)) {
            return RenameValidationError.UnsupportedPattern
        }

        val names = previews.map(RenamePreview::newName)
        if (names.any(::isInvalidFilename)) return RenameValidationError.InvalidName
        if (names.groupingBy { it.lowercase() }.eachCount().any { it.value > 1 }) {
            return RenameValidationError.DuplicateName
        }
        if (previews.none(RenamePreview::isChanged)) {
            return RenameValidationError.NoChanges
        }
        return null
    }

    private fun isInvalidFilename(name: String): Boolean = name.isBlank() ||
            name == "." ||
            name == ".." ||
            '/' in name ||
            '\u0000' in name ||
            name.toByteArray().size > MAX_FILENAME_BYTES
}