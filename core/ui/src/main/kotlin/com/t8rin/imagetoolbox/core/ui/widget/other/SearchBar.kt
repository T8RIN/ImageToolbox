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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedHorizontalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.text.isKeyboardVisibleAsState
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    searchString: String,
    onValueChange: (String) -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val focusRequester = remember { FocusRequester() }
    val localFocusManager = LocalFocusManager.current
    val state = rememberScrollState()

    val isKeyboardVisible by isKeyboardVisibleAsState()
    var isKeyboardWasVisible by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            delay(600)
            if (isKeyboardWasVisible == true) {
                isKeyboardWasVisible = false
            }
        } else {
            isKeyboardWasVisible = true
        }
    }

    LaunchedEffect(windowInfo) {
        snapshotFlow {
            windowInfo.isWindowFocused
        }.collect { isWindowFocused ->
            if (isWindowFocused && isKeyboardWasVisible != false) {
                delay(500)
                focusRequester.requestFocus()
                isKeyboardWasVisible = true
            }
        }
    }
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .fadingEdges(state)
            .enhancedHorizontalScroll(state)
            .focusRequester(focusRequester),
        value = searchString,
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
        keyboardActions = KeyboardActions(
            onDone = { localFocusManager.clearFocus() }
        ),
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        onValueChange = onValueChange
    )
    if (searchString.isEmpty()) {
        Text(
            text = stringResource(R.string.search_here),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            style = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(
                    0.5f
                )
            )
        )
    }
}