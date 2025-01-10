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

package ru.tech.imageresizershrinker.core.ui.utils.provider

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ime as imeImpl
import androidx.compose.foundation.layout.imePadding as imePaddingImpl

val LocalCustomKeyboardManager =
    compositionLocalOf<CustomKeyboardManager> { error("LocalPaddingManager not present") }

interface CustomKeyboardManager {
    val keyboardHeight: Dp

    fun updateHeight(keyboardHeight: Dp)

    fun close()
}

@Composable
fun rememberCustomKeyboardManager(): CustomKeyboardManager =
    remember { CustomKeyboardManagerImpl() }

@Composable
fun PropagateCustomKeyboardPadding(
    keyboardHeight: Dp
) {
    val paddingManager = LocalCustomKeyboardManager.current

    DisposableEffect(keyboardHeight, paddingManager) {
        paddingManager.updateHeight(keyboardHeight)

        onDispose { paddingManager.close() }
    }
}

private class CustomKeyboardManagerImpl : CustomKeyboardManager {
    private val _keyboardHeight = mutableStateOf(0.dp)

    override val keyboardHeight: Dp by _keyboardHeight

    override fun updateHeight(keyboardHeight: Dp) {
        _keyboardHeight.value = keyboardHeight
    }

    override fun close() = updateHeight(0.dp)

}

fun Modifier.imePadding(): Modifier = this.composed {
    val keyboardPaddingManager = LocalCustomKeyboardManager.current

    if (keyboardPaddingManager.keyboardHeight > 0.dp) {
        Modifier.padding(bottom = keyboardPaddingManager.keyboardHeight)
    } else {
        Modifier.imePaddingImpl()
    }
}

val WindowInsets.Companion.ime: WindowInsets
    @Composable @NonRestartableComposable get() {
        val keyboardPaddingManager = LocalCustomKeyboardManager.current

        return if (keyboardPaddingManager.keyboardHeight > 0.dp) {
            WindowInsets(bottom = keyboardPaddingManager.keyboardHeight)
        } else {
            WindowInsets.imeImpl
        }
    }