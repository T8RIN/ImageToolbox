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

import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Date
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Extension
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Height
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.OriginalName
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Prefix
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Rand
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Sequence
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Suffix
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Width

internal object RenamePatterns {

    val entries: List<FilenamePattern> = listOf(
        Prefix,
        OriginalName,
        Width,
        Height,
        Date,
        Rand,
        Sequence,
        Suffix,
        Extension
    )

    val Default = "${OriginalName}.${Extension}"

    private val disallowedTokens = listOf(
        FilenamePattern.PresetInfo,
        FilenamePattern.ScaleMode,
        FilenamePattern.PresetInfoUpper,
        FilenamePattern.ScaleModeUpper
    )

    fun containsDisallowed(pattern: String): Boolean = disallowedTokens.any { token ->
        pattern.contains(token.value, ignoreCase = false)
    }

    fun sanitize(pattern: String): String {
        var result = pattern
        disallowedTokens.forEach { token ->
            result = result.replace(token.value, "")
        }
        return result.cleanPatternArtifacts()
    }

    private fun String.cleanPatternArtifacts(): String = replace("[]", "")
        .replace("{}", "")
        .replace("()x()", "")
        .replace("()", "")
        .replace("___", "_")
        .replace("__", "_")
        .replace("_.", ".")
        .removePrefix("_")
        .removeSuffix(".")
}