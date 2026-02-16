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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField

@Composable
internal fun PageInputField(
    selectedPages: List<Int>,
    onPagesChanged: (List<Int>) -> Unit
) {
    var text by remember {
        mutableStateOf(PagesSelectionParser.formatPageOutput(selectedPages))
    }

    RoundedTextField(
        value = text,
        onValueChange = {
            text = it
            val parsedPages = PagesSelectionParser.parsePageInput(it)
            onPagesChanged(parsedPages)
        },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Start
        ),
        label = stringResource(R.string.custom_pages),
        modifier = Modifier
            .container(
                shape = MaterialTheme.shapes.large,
                resultPadding = 8.dp
            ),
        singleLine = false
    )
}

