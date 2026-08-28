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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.comic_to_pdf

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Book2
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Password
import com.t8rin.imagetoolbox.core.resources.icons.Receipt
import com.t8rin.imagetoolbox.core.resources.icons.ScissorsSmall
import com.t8rin.imagetoolbox.core.resources.icons.SwapVerticalCircle
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FileSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.ComicReadingDirection
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PageSize
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.comic_to_pdf.screenLogic.ComicToPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolContent

@Composable
fun ComicToPdfToolContent(
    component: ComicToPdfToolComponent
) {
    BasePdfToolContent(
        component = component,
        contentPicker = rememberFilePicker(
            mimeType = MimeType.All,
            onSuccess = component::setUri
        ),
        isPickedAlready = component.initialUri != null,
        canShowScreenData = component.uri != null,
        title = stringResource(R.string.comic_to_pdf),
        controls = {
            val params = component.params
            val selectedFilename = component.uri?.let { rememberFilename(it) }
            val selectedFileSize = component.uri?.let { rememberHumanFileSize(it) }

            FileSelector(
                value = component.uri?.toString(),
                onValueChange = component::setUri,
                title = selectedFilename ?: stringResource(R.string.comic_to_pdf),
                subtitle = selectedFileSize ?: stringResource(R.string.comic_to_pdf_sub)
            )
            Spacer(Modifier.height(16.dp))
            PreferenceRowSwitch(
                title = stringResource(R.string.reverse_page_order),
                subtitle = stringResource(R.string.reverse_page_order_sub),
                checked = params.reversePageOrder,
                onClick = {
                    component.updateParams(params.copy(reversePageOrder = it))
                },
                startIcon = Icons.Outlined.SwapVerticalCircle,
                shape = ShapeDefaults.top,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            PreferenceRowSwitch(
                title = stringResource(R.string.split_wide_comic_pages),
                subtitle = stringResource(R.string.split_wide_comic_pages_sub),
                checked = params.splitWidePages,
                onClick = {
                    component.updateParams(params.copy(splitWidePages = it))
                },
                startIcon = Icons.Outlined.ScissorsSmall,
                shape = ShapeDefaults.center,
                modifier = Modifier.fillMaxWidth()
            )
            EnhancedButtonGroup(
                entries = ComicReadingDirection.entries,
                value = params.readingDirection,
                onValueChange = {
                    component.updateParams(params.copy(readingDirection = it))
                },
                itemContent = {
                    Text(
                        stringResource(
                            when (it) {
                                ComicReadingDirection.LeftToRight -> R.string.left_to_right
                                ComicReadingDirection.RightToLeft -> R.string.right_to_left
                            }
                        )
                    )
                },
                title = stringResource(R.string.reading_direction),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .container(ShapeDefaults.center)
            )
            Spacer(Modifier.height(4.dp))
            PreferenceRowSwitch(
                title = stringResource(R.string.skip_comic_cover),
                subtitle = stringResource(R.string.skip_comic_cover_sub),
                checked = params.skipCover,
                onClick = {
                    component.updateParams(params.copy(skipCover = it))
                },
                startIcon = Icons.Outlined.Book2,
                shape = ShapeDefaults.bottom,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            QualitySelector(
                imageFormat = ImageFormat.Jpg,
                quality = Quality.Base(params.quality),
                onQualityChange = {
                    component.updateParams(params.copy(quality = it.qualityValue))
                },
                autoCoerce = false
            )
            Spacer(Modifier.height(8.dp))
            DataSelector(
                value = params.pageSize,
                onValueChange = {
                    component.updateParams(params.copy(pageSize = it))
                },
                entries = remember { listOf(PageSize.Auto) + PageSize.entries },
                title = stringResource(R.string.page_size),
                titleIcon = Icons.Outlined.Receipt,
                itemContentText = {
                    it.name ?: stringResource(R.string.auto)
                },
                spanCount = 3,
                shape = ShapeDefaults.large,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            RoundedTextField(
                value = component.passphrase,
                onValueChange = component::setPassphrase,
                label = stringResource(R.string.password_optional),
                startIcon = Icons.Rounded.Password,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .container(
                        shape = ShapeDefaults.default,
                        resultPadding = 8.dp
                    )
            )
        }
    )
}
