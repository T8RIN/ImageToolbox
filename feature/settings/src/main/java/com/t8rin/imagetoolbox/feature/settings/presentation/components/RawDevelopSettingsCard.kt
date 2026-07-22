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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Raw
import com.t8rin.imagetoolbox.core.settings.domain.SimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRow
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.raw_coder.isRawUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RawDevelopSettingsCard(
    uri: Uri?,
    modifier: Modifier = Modifier,
    onSettingsChanged: () -> Unit = {},
) {
    val rawDevelopSettings = LocalSettingsState.current.rawDevelopSettings
    var isRaw by remember(uri) { mutableStateOf(false) }
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var previousRawDevelopSettings by remember { mutableStateOf(rawDevelopSettings) }

    LaunchedEffect(uri) {
        isRaw = withContext(Dispatchers.IO) {
            uri?.takeUnless { it == Uri.EMPTY }?.let(appContext::isRawUri) == true
        }
        if (!isRaw) showSheet = false
    }

    LaunchedEffect(rawDevelopSettings) {
        if (rawDevelopSettings != previousRawDevelopSettings) {
            delay(1_000)
            previousRawDevelopSettings = rawDevelopSettings
            if (isRaw) onSettingsChanged()
        }
    }

    AnimatedVisibility(
        visible = isRaw,
        modifier = modifier.fillMaxWidth()
    ) {
        PreferenceRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            title = stringResource(R.string.raw_development),
            subtitle = stringResource(R.string.raw_development_sub),
            shape = ShapeDefaults.large,
            startIcon = Icons.Rounded.Raw,
            onClick = { showSheet = true }
        )
    }

    RawDevelopSettingsSheet(
        visible = showSheet,
        onDismiss = { showSheet = it }
    )
}

@Composable
private fun RawDevelopSettingsSheet(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    interactor: SimpleSettingsInteractor = LocalSimpleSettingsInteractor.current,
) {
    val scope = rememberCoroutineScope()

    fun updateSettings(block: suspend SimpleSettingsInteractor.() -> Unit) {
        scope.launch {
            interactor.block()
        }
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = {
            TitleItem(
                icon = Icons.Rounded.Raw,
                text = stringResource(R.string.raw_development)
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { onDismiss(false) }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .enhancedVerticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SettingsGroup.RawDevelopment.settingsList.forEach { setting ->
                when (setting) {
                    Setting.RawUseEmbeddedPreview -> RawUseEmbeddedPreviewSettingItem(
                        onClick = { updateSettings { toggleRawUseEmbeddedPreview() } }
                    )

                    Setting.RawWhiteBalance -> RawWhiteBalanceSettingItem(
                        onValueChange = { updateSettings { setRawWhiteBalance(it) } }
                    )

                    Setting.RawOutputColorSpace -> RawOutputColorSpaceSettingItem(
                        onValueChange = { updateSettings { setRawOutputColorSpace(it) } }
                    )

                    Setting.RawHighlightRecovery -> RawHighlightRecoverySettingItem(
                        onValueChange = { updateSettings { setRawHighlightRecovery(it) } }
                    )

                    Setting.RawExposureCompensation -> RawExposureCompensationSettingItem(
                        onValueChange = { updateSettings { setRawExposureCompensation(it) } }
                    )

                    Setting.RawHighlightPreservation -> RawHighlightPreservationSettingItem(
                        onValueChange = { updateSettings { setRawHighlightPreservation(it) } }
                    )

                    Setting.RawAutoBrightness -> RawAutoBrightnessSettingItem(
                        onClick = { updateSettings { toggleRawAutoBrightness() } },
                        onValueChange = { updateSettings { setRawBrightness(it) } }
                    )

                    Setting.RawDemosaicQuality -> RawDemosaicQualitySettingItem(
                        onValueChange = { updateSettings { setRawDemosaicQuality(it) } }
                    )

                    Setting.RawHalfSize -> RawHalfSizeSettingItem(
                        onClick = { updateSettings { toggleRawHalfSize() } }
                    )

                    Setting.RawApplyOrientation -> RawApplyOrientationSettingItem(
                        onClick = { updateSettings { toggleRawApplyOrientation() } }
                    )

                    else -> Unit
                }
            }
        }
    }
}
