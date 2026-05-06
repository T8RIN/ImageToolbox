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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri

data object AppSkippedImagesHost {

    val state = SkippedImagesHostState()

    fun showSkippedImages(
        uris: List<String>
    ) {
        state.showSkippedImages(uris)
    }

    fun dismiss() {
        state.dismiss()
    }

}

@Stable
@Immutable
class SkippedImagesHostState {

    private val skippedImageUrisState = mutableStateOf<List<Uri>>(emptyList())
    val skippedImageUris by skippedImageUrisState

    fun showSkippedImages(
        uris: List<String>
    ) {
        skippedImageUrisState.value = uris
            .filter(String::isNotBlank)
            .distinct()
            .map { it.toUri() }
    }

    fun dismiss() {
        skippedImageUrisState.value = emptyList()
    }

}
