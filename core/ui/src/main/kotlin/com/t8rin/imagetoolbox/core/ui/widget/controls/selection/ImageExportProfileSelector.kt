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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.BuiltInImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Behance
import com.t8rin.imagetoolbox.core.resources.icons.Bluesky
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Discord
import com.t8rin.imagetoolbox.core.resources.icons.Facebook
import com.t8rin.imagetoolbox.core.resources.icons.Instagram
import com.t8rin.imagetoolbox.core.resources.icons.LinkedIn
import com.t8rin.imagetoolbox.core.resources.icons.Loyalty
import com.t8rin.imagetoolbox.core.resources.icons.Pinterest
import com.t8rin.imagetoolbox.core.resources.icons.Public
import com.t8rin.imagetoolbox.core.resources.icons.Reddit
import com.t8rin.imagetoolbox.core.resources.icons.Snapchat
import com.t8rin.imagetoolbox.core.resources.icons.Telegram
import com.t8rin.imagetoolbox.core.resources.icons.Threads
import com.t8rin.imagetoolbox.core.resources.icons.TikTok
import com.t8rin.imagetoolbox.core.resources.icons.Twitch
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.resources.icons.Vk
import com.t8rin.imagetoolbox.core.resources.icons.X
import com.t8rin.imagetoolbox.core.resources.icons.YouTube
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.ImageExportProfilesHolder
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.delay

@Composable
fun ImageExportProfileSelector(
    profiles: List<ImageExportProfile>,
    selectedProfile: ImageExportProfile?,
    imageInfo: ImageInfo,
    preset: Preset,
    imageExportProfilesHolder: ImageExportProfilesHolder,
    modifier: Modifier = Modifier
) {
    val builtInProfiles = BuiltInImageExportProfile.entries
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var profileToDelete by remember { mutableStateOf<ImageExportProfile?>(null) }
    var preferredBuiltInId by rememberSaveable { mutableStateOf<String?>(null) }
    var preferredCustomProfile by remember { mutableStateOf<ImageExportProfile?>(null) }
    var applyRevision by remember { mutableIntStateOf(0) }
    var isApplyingPreferredProfile by remember { mutableStateOf(false) }
    var appliedProfileSettings by remember { mutableStateOf<AppliedProfileSettings?>(null) }
    val backgroundColorForNoAlphaFormats = LocalSettingsState.current
        .backgroundForNoAlphaImageFormats
        .toArgb()
    val preferredBuiltIn = builtInProfiles.firstOrNull { it.id == preferredBuiltInId }
    val activeBuiltIn = preferredBuiltIn
        ?: selectedProfile
            ?.takeIf { preferredCustomProfile == null }
            ?.let { profile -> builtInProfiles.firstOrNull { it.profile == profile } }
    val displayedSelectedProfile = preferredBuiltIn?.profile
        ?: preferredCustomProfile
        ?: selectedProfile

    LaunchedEffect(
        imageInfo,
        preset,
        imageExportProfilesHolder.currentProfileKeepExif,
        backgroundColorForNoAlphaFormats,
        preferredBuiltInId,
        preferredCustomProfile,
        isApplyingPreferredProfile,
        appliedProfileSettings
    ) {
        if (isApplyingPreferredProfile) return@LaunchedEffect
        if (preferredBuiltIn == null && preferredCustomProfile == null) {
            return@LaunchedEffect
        }
        val currentSettings = AppliedProfileSettings(
            imageInfo = imageInfo.comparableFor(preset),
            preset = preset,
            keepExif = imageExportProfilesHolder.currentProfileKeepExif,
            backgroundColorForNoAlphaFormats = backgroundColorForNoAlphaFormats
        )

        if (appliedProfileSettings == null) {
            delay(PROFILE_SETTINGS_SETTLING_TIME)
            appliedProfileSettings = currentSettings
        } else if (appliedProfileSettings != currentSettings) {
            delay(PROFILE_MISMATCH_TIME)
            preferredBuiltInId = null
            preferredCustomProfile = null
            appliedProfileSettings = null
        }
    }
    val importPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = imageExportProfilesHolder::importProfile
    )

    EnhancedChip(
        selected = displayedSelectedProfile != null,
        onClick = { showSheet = true },
        selectedColor = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Icon(
            imageVector = activeBuiltIn?.platform?.icon ?: Icons.Outlined.Loyalty,
            contentDescription = stringResource(R.string.export_profiles),
            modifier = if (
                activeBuiltIn == null ||
                activeBuiltIn.platform == BuiltInImageExportProfile.Platform.Telegram ||
                activeBuiltIn.platform == BuiltInImageExportProfile.Platform.Web
            ) {
                Modifier.size(24.dp)
            } else {
                Modifier.size(20.dp)
            }
        )
    }

    EnhancedModalBottomSheet(
        visible = showSheet,
        onDismiss = {
            showSheet = it
        },
        title = {
            TitleItem(
                icon = Icons.Rounded.Loyalty,
                text = stringResource(R.string.export_profiles)
            )
        },
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedIconButton(
                    onClick = importPicker::pickFile,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = stringResource(R.string.import_word)
                    )
                }
                EnhancedButton(
                    onClick = {
                        showSheet = false
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .animateContentSizeNoClip()
                .clearFocusOnTap()
        ) {
            if (builtInProfiles.isNotEmpty()) {
                item("BuiltInExportProfiles") {
                    ExpandableItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        visibleContent = {
                            TitleItem(
                                icon = Icons.Outlined.Build,
                                text = stringResource(R.string.built_in_export_profiles),
                                subtitle = builtInProfiles.size.toString(),
                                modifier = Modifier.padding(8.dp)
                            )
                        },
                        shape = ShapeDefaults.extraLarge,
                        expandableContent = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .padding(bottom = 2.dp)
                                    .container(
                                        resultPadding = 0.dp,
                                        shape = ShapeDefaults.large,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                    .padding(
                                        top = 8.dp,
                                        end = 12.dp,
                                        start = 12.dp,
                                        bottom = 12.dp
                                    )
                            ) {
                                builtInProfiles.groupBy { it.platform }
                                    .forEach { (platform, items) ->
                                        TitleItem(
                                            icon = platform.icon,
                                            text = platform.title,
                                            modifier = Modifier.padding(
                                                horizontal = 4.dp,
                                                vertical = 8.dp
                                            ),
                                            iconPadding = if (
                                                platform == BuiltInImageExportProfile.Platform.Telegram ||
                                                platform == BuiltInImageExportProfile.Platform.Web
                                            ) {
                                                Dp.Unspecified
                                            } else 2.dp
                                        )
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            items.forEachIndexed { index, item ->
                                                BuiltInImagePresetItem(
                                                    index = index,
                                                    profilesCount = items.size,
                                                    item = item,
                                                    selected = item.id == activeBuiltIn?.id,
                                                    onApplyProfile = { profile ->
                                                        preferredBuiltInId = item.id
                                                        preferredCustomProfile = null
                                                        val revision = ++applyRevision
                                                        isApplyingPreferredProfile = true
                                                        appliedProfileSettings = null
                                                        imageExportProfilesHolder.applyProfile(
                                                            profile = profile,
                                                            onApplied = {
                                                                if (revision == applyRevision) {
                                                                    isApplyingPreferredProfile =
                                                                        false
                                                                }
                                                            }
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                            }
                        }
                    )
                }
            }
            if (displayedSelectedProfile !in profiles || profiles.isEmpty()) {
                item("AddImagePresetBlock") {
                    AddImagePresetBlock(
                        preset = preset,
                        imageInfo = imageInfo,
                        onSave = imageExportProfilesHolder::saveProfile,
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp
                            )
                            .animateItem()
                    )
                }
            }
            itemsIndexed(
                items = profiles,
                key = { _, item -> item.name }
            ) { index, item ->
                ImagePresetItem(
                    index = index,
                    profilesCount = profiles.size,
                    item = item,
                    selected = item == displayedSelectedProfile,
                    onApplyProfile = { profile ->
                        preferredBuiltInId = null
                        preferredCustomProfile = profile
                        val revision = ++applyRevision
                        isApplyingPreferredProfile = true
                        appliedProfileSettings = null
                        imageExportProfilesHolder.applyProfile(
                            profile = profile,
                            onApplied = {
                                if (revision == applyRevision) {
                                    isApplyingPreferredProfile = false
                                }
                            }
                        )
                    },
                    onExportProfile = imageExportProfilesHolder::exportProfile,
                    onShareProfile = imageExportProfilesHolder::shareProfile,
                    onWantDelete = { profileToDelete = it }
                )
            }
        }
    }

    EnhancedAlertDialog(
        visible = profileToDelete != null,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.delete_export_profile))
        },
        text = {
            Text(
                stringResource(
                    R.string.delete_export_profile_sub,
                    profileToDelete?.name ?: ""
                )
            )
        },
        onDismissRequest = { profileToDelete = null },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    profileToDelete?.let { profile ->
                        imageExportProfilesHolder.deleteProfile(profile)
                        if (preferredCustomProfile == profile) {
                            preferredCustomProfile = null
                            appliedProfileSettings = null
                        }
                    }
                    profileToDelete = null
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { profileToDelete = null }
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        placeAboveAll = true
    )
}

private val BuiltInImageExportProfile.Platform.icon: ImageVector
    get() = when (this) {
        BuiltInImageExportProfile.Platform.Web -> Icons.Rounded.Public
        BuiltInImageExportProfile.Platform.Instagram -> Icons.Rounded.Instagram
        BuiltInImageExportProfile.Platform.Facebook -> Icons.Rounded.Facebook
        BuiltInImageExportProfile.Platform.X -> Icons.Rounded.X
        BuiltInImageExportProfile.Platform.YouTube -> Icons.Rounded.YouTube
        BuiltInImageExportProfile.Platform.TikTok -> Icons.Rounded.TikTok
        BuiltInImageExportProfile.Platform.Threads -> Icons.Rounded.Threads
        BuiltInImageExportProfile.Platform.Bluesky -> Icons.Rounded.Bluesky
        BuiltInImageExportProfile.Platform.LinkedIn -> Icons.Rounded.LinkedIn
        BuiltInImageExportProfile.Platform.Pinterest -> Icons.Rounded.Pinterest
        BuiltInImageExportProfile.Platform.VK -> Icons.Rounded.Vk
        BuiltInImageExportProfile.Platform.Reddit -> Icons.Rounded.Reddit
        BuiltInImageExportProfile.Platform.Snapchat -> Icons.Rounded.Snapchat
        BuiltInImageExportProfile.Platform.Behance -> Icons.Rounded.Behance
        BuiltInImageExportProfile.Platform.Telegram -> Icons.Rounded.Telegram
        BuiltInImageExportProfile.Platform.Discord -> Icons.Rounded.Discord
        BuiltInImageExportProfile.Platform.Twitch -> Icons.Rounded.Twitch
    }

private data class AppliedProfileSettings(
    private val imageInfo: ImageInfo,
    private val preset: Preset,
    private val keepExif: Boolean?,
    private val backgroundColorForNoAlphaFormats: Int
)

private const val PROFILE_SETTINGS_SETTLING_TIME = 300L
private const val PROFILE_MISMATCH_TIME = 150L
