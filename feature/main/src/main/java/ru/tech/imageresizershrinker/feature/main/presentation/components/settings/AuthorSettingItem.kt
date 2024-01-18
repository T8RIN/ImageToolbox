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

@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.AUTHOR_AVATAR
import ru.tech.imageresizershrinker.core.domain.FOSS_LINK
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.feature.main.presentation.components.AuthorLinksSheet

@Composable
fun AuthorSettingItem(
    shape: Shape = ContainerShapeDefaults.topShape
) {
    val showAuthorSheet = rememberSaveable { mutableStateOf(false) }
    PreferenceRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        applyHorPadding = false,
        color = MaterialTheme.colorScheme.secondaryContainer,
        title = stringResource(R.string.app_developer),
        subtitle = stringResource(R.string.app_developer_nick),
        shape = shape,
        startContent = {
            Picture(
                model = if (BuildConfig.FLAVOR == "foss") {
                    FOSS_LINK
                } else AUTHOR_AVATAR,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(48.dp)
                    .container(
                        shape = CloverShape,
                        resultPadding = 0.dp
                    ),
                showTransparencyChecker = false,
                shape = RectangleShape
            )
        },
        endContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        onClick = { showAuthorSheet.value = true }
    )
    AuthorLinksSheet(showAuthorSheet)
}