/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.checksum_tools.presentation.components

import android.net.Uri

sealed interface ChecksumPage {

    data class CalculateFromUri(
        val uri: Uri?,
        val checksum: String
    ) : ChecksumPage {
        companion object {
            const val INDEX = 0
            val Empty: CalculateFromUri by lazy {
                CalculateFromUri(
                    uri = null,
                    checksum = ""
                )
            }
        }
    }

    data class CalculateFromText(
        val text: String,
        val checksum: String
    ) : ChecksumPage {
        companion object {
            const val INDEX = 1
            val Empty: CalculateFromText by lazy {
                CalculateFromText(
                    text = "",
                    checksum = ""
                )
            }
        }
    }

    data class CompareWithUri(
        val uri: Uri?,
        val checksum: String,
        val targetChecksum: String,
        val isCorrect: Boolean
    ) : ChecksumPage {
        companion object {
            const val INDEX = 2
            val Empty: CompareWithUri by lazy {
                CompareWithUri(
                    uri = null,
                    checksum = "",
                    targetChecksum = "",
                    isCorrect = false
                )
            }
        }
    }

    data class CompareWithUris(
        val uris: List<UriWithHash>,
        val targetChecksum: String
    ) : ChecksumPage {
        companion object {
            const val INDEX = 3
            val Empty: CompareWithUris by lazy {
                CompareWithUris(
                    uris = emptyList(),
                    targetChecksum = ""
                )
            }
        }
    }

    companion object {
        const val ENTRIES_COUNT: Int = 4
    }

}

data class UriWithHash(
    val uri: Uri,
    val checksum: String
)