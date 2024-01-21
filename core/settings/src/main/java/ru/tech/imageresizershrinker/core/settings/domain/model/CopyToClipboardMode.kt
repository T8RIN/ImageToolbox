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

package ru.tech.imageresizershrinker.core.settings.domain.model

sealed class CopyToClipboardMode(
    open val value: Int
) {

    data object Disabled : CopyToClipboardMode(0)

    sealed class Enabled(
        override val value: Int
    ) : CopyToClipboardMode(value) {
        data object WithoutSaving : Enabled(1)
        data object WithSaving : Enabled(2)
    }

    companion object {
        fun fromInt(
            value: Int
        ): CopyToClipboardMode = when (value) {
            1 -> Enabled.WithoutSaving
            2 -> Enabled.WithSaving
            else -> Disabled
        }
    }

}