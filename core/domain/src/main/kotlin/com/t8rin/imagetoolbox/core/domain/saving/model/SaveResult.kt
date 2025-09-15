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

package com.t8rin.imagetoolbox.core.domain.saving.model

sealed class SaveResult(
    open val savingPath: String
) {

    data class Success(
        val message: String? = null,
        override val savingPath: String,
        val isOverwritten: Boolean = false,
    ) : SaveResult(savingPath)

    sealed class Error(open val throwable: Throwable) : SaveResult("") {
        data object MissingPermissions : Error(IllegalAccessException("MissingPermissions"))

        data class Exception(
            override val throwable: Throwable
        ) : Error(throwable)
    }

    data object Skipped : SaveResult("")

    fun onSuccess(
        action: () -> Unit
    ): SaveResult = apply {
        if (this is Success) action()
    }
}

fun List<SaveResult>.onSuccess(
    action: () -> Unit
): List<SaveResult> = apply {
    if (this.any { it is SaveResult.Success }) action()
}