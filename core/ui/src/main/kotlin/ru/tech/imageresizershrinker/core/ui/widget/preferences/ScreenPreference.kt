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

package ru.tech.imageresizershrinker.core.ui.widget.preferences

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen

@Composable
internal fun ScreenPreference(
    screen: Screen,
    navigate: (Screen) -> Unit
) {
    val basePreference = @Composable {
        PreferenceItem(
            onClick = { navigate(screen) },
            startIcon = screen.icon,
            title = stringResource(screen.title),
            subtitle = stringResource(screen.subtitle),
            modifier = Modifier.fillMaxWidth()
        )
    }
    when (screen) {
        is Screen.GifTools -> {
            if (screen.type != null) {
                PreferenceItem(
                    onClick = { navigate(screen) },
                    startIcon = screen.type.icon,
                    title = stringResource(screen.type.title),
                    subtitle = stringResource(screen.type.subtitle),
                    modifier = Modifier.fillMaxWidth()
                )
            } else basePreference()
        }

        is Screen.PdfTools -> {
            if (screen.type != null) {
                PreferenceItem(
                    onClick = { navigate(screen) },
                    startIcon = screen.type.icon,
                    title = stringResource(screen.type.title),
                    subtitle = stringResource(screen.type.subtitle),
                    modifier = Modifier.fillMaxWidth()
                )
            } else basePreference()
        }

        is Screen.Filter -> {
            if (screen.type != null) {
                PreferenceItem(
                    onClick = { navigate(screen) },
                    startIcon = screen.type.icon,
                    title = stringResource(screen.type.title),
                    subtitle = stringResource(screen.type.subtitle),
                    modifier = Modifier.fillMaxWidth()
                )
            } else basePreference()
        }

        is Screen.ApngTools -> {
            if (screen.type != null) {
                PreferenceItem(
                    onClick = { navigate(screen) },
                    startIcon = screen.type.icon,
                    title = stringResource(screen.type.title),
                    subtitle = stringResource(screen.type.subtitle),
                    modifier = Modifier.fillMaxWidth()
                )
            } else basePreference()
        }

        is Screen.JxlTools -> {
            if (screen.type != null) {
                PreferenceItem(
                    onClick = { navigate(screen) },
                    startIcon = screen.type.icon,
                    title = stringResource(screen.type.title),
                    subtitle = stringResource(screen.type.subtitle),
                    modifier = Modifier.fillMaxWidth()
                )
            } else basePreference()
        }

        is Screen.WebpTools -> {
            if (screen.type != null) {
                PreferenceItem(
                    onClick = { navigate(screen) },
                    startIcon = screen.type.icon,
                    title = stringResource(screen.type.title),
                    subtitle = stringResource(screen.type.subtitle),
                    modifier = Modifier.fillMaxWidth()
                )
            } else basePreference()
        }

        is Screen.RecognizeText -> {
            if (screen.type != null) {
                PreferenceItem(
                    onClick = { navigate(screen) },
                    startIcon = screen.type.icon,
                    title = stringResource(screen.type.title),
                    subtitle = stringResource(screen.type.subtitle),
                    modifier = Modifier.fillMaxWidth()
                )
            } else basePreference()
        }

        else -> basePreference()
    }
}