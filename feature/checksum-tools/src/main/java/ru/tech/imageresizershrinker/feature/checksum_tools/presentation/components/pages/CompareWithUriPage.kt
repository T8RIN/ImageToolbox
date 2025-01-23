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

package ru.tech.imageresizershrinker.feature.checksum_tools.presentation.components.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FileSelector
import ru.tech.imageresizershrinker.core.ui.widget.other.InfoContainer
import ru.tech.imageresizershrinker.feature.checksum_tools.presentation.components.ChecksumEnterField
import ru.tech.imageresizershrinker.feature.checksum_tools.presentation.components.ChecksumPreviewField
import ru.tech.imageresizershrinker.feature.checksum_tools.presentation.components.ChecksumResultCard
import ru.tech.imageresizershrinker.feature.checksum_tools.presentation.screenLogic.ChecksumToolsComponent

@Composable
internal fun ColumnScope.CompareWithUriPage(
    component: ChecksumToolsComponent,
) {
    val essentials = rememberLocalEssentials()
    val onCopyText: (String) -> Unit = essentials::copyToClipboard

    val page = component.compareWithUriPage

    FileSelector(
        value = page.uri?.toString(),
        onValueChange = component::setDataForComparison,
        subtitle = null
    )
    AnimatedContent(page.checksum) { checksum ->
        if (checksum.isNotEmpty()) {
            ChecksumPreviewField(
                value = checksum,
                onCopyText = onCopyText
            )
        } else {
            InfoContainer(
                text = stringResource(R.string.pick_file_to_checksum),
                modifier = Modifier.padding(8.dp),
            )
        }
    }

    ChecksumEnterField(
        value = page.targetChecksum,
        onValueChange = {
            component.setDataForComparison(
                targetChecksum = it
            )
        }
    )

    AnimatedVisibility(
        page.targetChecksum.isNotEmpty() && !page.uri?.toString()
            .isNullOrEmpty()
    ) {
        ChecksumResultCard(page.isCorrect)
    }
}