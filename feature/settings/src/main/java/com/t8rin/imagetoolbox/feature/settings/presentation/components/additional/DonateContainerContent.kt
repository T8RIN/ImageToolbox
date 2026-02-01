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

package com.t8rin.imagetoolbox.feature.settings.presentation.components.additional

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.BitcoinWallet
import com.t8rin.imagetoolbox.core.domain.BoostyLink
import com.t8rin.imagetoolbox.core.domain.TONSpaceWallet
import com.t8rin.imagetoolbox.core.domain.TONWallet
import com.t8rin.imagetoolbox.core.domain.USDTWallet
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bitcoin
import com.t8rin.imagetoolbox.core.resources.icons.Boosty
import com.t8rin.imagetoolbox.core.resources.icons.Ton
import com.t8rin.imagetoolbox.core.resources.icons.USDT
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.BitcoinColor
import com.t8rin.imagetoolbox.core.ui.theme.BoostyColor
import com.t8rin.imagetoolbox.core.ui.theme.TONColor
import com.t8rin.imagetoolbox.core.ui.theme.TONSpaceColor
import com.t8rin.imagetoolbox.core.ui.theme.USDTColor
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults

@Composable
fun DonateContainerContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        InfoContainer(
            text = stringResource(R.string.donation_sub),
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp
                ),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val options = DonationOption.entries

        options.forEachIndexed { index, option ->
            CompositionLocalProvider(
                LocalIconShapeContainerColor provides option.containerColor().blend(
                    color = Color.White,
                    fraction = 0.1f
                )
            ) {
                PreferenceItem(
                    containerColor = option.containerColor(),
                    overrideIconShapeContentColor = true,
                    contentColor = option.contentColor(),
                    shape = ShapeDefaults.byIndex(
                        index = index,
                        size = options.size
                    ),
                    onClick = option.onClick,
                    endIcon = option.endIcon,
                    startIcon = option.startIcon,
                    title = option.title(),
                    subtitle = option.subtitle,
                    titleFontStyle = PreferenceItemDefaults.TitleFontStyle.copy(
                        textAlign = TextAlign.Start
                    )
                )
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

private data class DonationOption(
    val containerColor: @Composable () -> Color,
    val contentColor: @Composable () -> Color,
    val onClick: () -> Unit,
    val endIcon: ImageVector,
    val startIcon: ImageVector,
    val title: @Composable () -> String,
    val subtitle: String
) {
    companion object Companion {
        val entries: List<DonationOption>
            @Composable
            get() {
                val essentials = rememberLocalEssentials()
                val linkHandler = LocalUriHandler.current
                val darkMode = !LocalSettingsState.current.isNightMode

                return remember(essentials, linkHandler, darkMode) {
                    listOf(
                        DonationOption(
                            containerColor = { BoostyColor },
                            contentColor = {
                                BoostyColor.inverse(
                                    fraction = { 1f },
                                    darkMode = true
                                )
                            },
                            onClick = { linkHandler.openUri(BoostyLink) },
                            endIcon = Icons.Rounded.Link,
                            startIcon = Icons.Rounded.Boosty,
                            title = { stringResource(R.string.boosty) },
                            subtitle = BoostyLink
                        ),
                        DonationOption(
                            containerColor = { BitcoinColor },
                            contentColor = {
                                BitcoinColor.inverse(
                                    fraction = { 1f },
                                    darkMode = darkMode
                                )
                            },
                            onClick = {
                                essentials.copyToClipboard(BitcoinWallet)
                            },
                            endIcon = Icons.Rounded.ContentCopy,
                            title = { stringResource(R.string.bitcoin) },
                            startIcon = Icons.Filled.Bitcoin,
                            subtitle = BitcoinWallet
                        ),
                        DonationOption(
                            containerColor = { USDTColor },
                            contentColor = {
                                USDTColor.inverse(
                                    fraction = { 1f },
                                    darkMode = darkMode
                                )
                            },
                            onClick = {
                                essentials.copyToClipboard(BitcoinWallet)
                            },
                            endIcon = Icons.Rounded.ContentCopy,
                            title = { stringResource(R.string.usdt) },
                            startIcon = Icons.Filled.USDT,
                            subtitle = USDTWallet
                        ),
                        DonationOption(
                            containerColor = { TONColor },
                            contentColor = {
                                TONColor.inverse(
                                    fraction = { 1f },
                                    darkMode = darkMode
                                )
                            },
                            onClick = {
                                essentials.copyToClipboard(TONWallet)
                            },
                            endIcon = Icons.Rounded.ContentCopy,
                            startIcon = Icons.Rounded.Ton,
                            title = { stringResource(R.string.ton) },
                            subtitle = TONWallet
                        ),
                        DonationOption(
                            containerColor = { TONSpaceColor },
                            contentColor = {
                                TONSpaceColor.inverse(
                                    fraction = { 1f },
                                    darkMode = true
                                )
                            },
                            onClick = {
                                essentials.copyToClipboard(TONSpaceWallet)
                            },
                            endIcon = Icons.Rounded.ContentCopy,
                            startIcon = Icons.Rounded.Ton,
                            title = { stringResource(R.string.ton_space) },
                            subtitle = TONSpaceWallet
                        )
                    )
                }
            }
    }
}