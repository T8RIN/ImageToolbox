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

package ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField

@Composable
internal fun PageInputField(
    selectedPages: List<Int>,
    onPagesChanged: (List<Int>) -> Unit
) {
    var text by remember {
        mutableStateOf(formatPageOutput(selectedPages))
    }

    RoundedTextField(
        value = text,
        onValueChange = {
            text = it
            val parsedPages = parsePageInput(it)
            onPagesChanged(parsedPages)
        },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Start
        ),
        label = stringResource(R.string.custom_pages),
        modifier = Modifier
            .container(
                resultPadding = 0.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(8.dp),
        singleLine = false
    )
}

internal fun parsePageInput(input: String): List<Int> {
    val pages = mutableSetOf<Int>()
    val regex = "\\d+(-\\d+)?".toRegex()
    regex.findAll(input).forEach { match ->
        val rangeParts = match.value.split("-").mapNotNull { it.toIntOrNull() }
        when (rangeParts.size) {
            1 -> pages.add(rangeParts[0] - 1)
            2 -> if (rangeParts[0] <= rangeParts[1]) {
                pages.addAll((rangeParts[0] - 1)..(rangeParts[1] - 1))
            }
        }
    }
    return pages.sorted()
}

internal fun formatPageOutput(pages: List<Int>): String {
    if (pages.isEmpty()) return ""
    val result = mutableListOf<String>()
    var start = pages[0]
    var prev = pages[0]
    for (i in 1 until pages.size) {
        if (pages[i] != prev + 1) {
            result.add(if (start == prev) "${start + 1}" else "${start + 1}-${prev + 1}")
            start = pages[i]
        }
        prev = pages[i]
    }
    result.add(if (start == prev) "${start + 1}" else "${start + 1}-${prev + 1}")
    return result.joinToString(", ")
}