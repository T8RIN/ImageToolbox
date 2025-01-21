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

package ru.tech.imageresizershrinker.feature.settings.presentation.components.additional

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.BitcoinWallet
import ru.tech.imageresizershrinker.core.domain.TONSpaceWallet
import ru.tech.imageresizershrinker.core.domain.TONWallet
import ru.tech.imageresizershrinker.core.domain.USDTWallet
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Bitcoin
import ru.tech.imageresizershrinker.core.resources.icons.Ton
import ru.tech.imageresizershrinker.core.resources.icons.USDT
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.BitcoinColor
import ru.tech.imageresizershrinker.core.ui.theme.TONColor
import ru.tech.imageresizershrinker.core.ui.theme.TONSpaceColor
import ru.tech.imageresizershrinker.core.ui.theme.USDTColor
import ru.tech.imageresizershrinker.core.ui.theme.inverse
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem

@Composable
fun DonateContainerContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    val darkMode = !LocalSettingsState.current.isNightMode

    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .container(color = MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.donation_sub),
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 14.sp,
                color = LocalContentColor.current.copy(alpha = 0.5f)
            )
        }
        PreferenceItem(
            color = TONSpaceColor,
            overrideIconShapeContentColor = true,
            contentColor = TONSpaceColor.inverse(
                fraction = { 1f },
                darkMode = true
            ),
            shape = ContainerShapeDefaults.topShape,
            onClick = {
                context.apply {
                    copyToClipboard(TONSpaceWallet)
                    scope.launch {
                        toastHostState.showToast(
                            icon = Icons.Rounded.ContentCopy,
                            message = getString(R.string.copied),
                        )
                    }
                }
            },
            endIcon = Icons.Rounded.ContentCopy,
            startIcon = Icons.Rounded.Ton,
            title = stringResource(R.string.ton_space),
            subtitle = TONSpaceWallet
        )
        Spacer(Modifier.height(4.dp))
        PreferenceItem(
            color = TONColor,
            overrideIconShapeContentColor = true,
            contentColor = TONColor.inverse(
                fraction = { 1f },
                darkMode = darkMode
            ),
            shape = ContainerShapeDefaults.centerShape,
            onClick = {
                context.apply {
                    copyToClipboard(TONWallet)
                    scope.launch {
                        toastHostState.showToast(
                            icon = Icons.Rounded.ContentCopy,
                            message = getString(R.string.copied),
                        )
                    }
                }
            },
            endIcon = Icons.Rounded.ContentCopy,
            startIcon = Icons.Rounded.Ton,
            title = stringResource(R.string.ton),
            subtitle = TONWallet
        )
        Spacer(Modifier.height(4.dp))
        PreferenceItem(
            color = BitcoinColor,
            overrideIconShapeContentColor = true,
            contentColor = BitcoinColor.inverse(
                fraction = { 1f },
                darkMode = darkMode
            ),
            shape = ContainerShapeDefaults.centerShape,
            onClick = {
                context.apply {
                    copyToClipboard(BitcoinWallet)
                    scope.launch {
                        toastHostState.showToast(
                            icon = Icons.Rounded.ContentCopy,
                            message = getString(R.string.copied),
                        )
                    }
                }
            },
            endIcon = Icons.Rounded.ContentCopy,
            title = stringResource(R.string.bitcoin),
            startIcon = Icons.Filled.Bitcoin,
            subtitle = BitcoinWallet
        )
        Spacer(Modifier.height(4.dp))
        PreferenceItem(
            color = USDTColor,
            overrideIconShapeContentColor = true,
            contentColor = USDTColor.inverse(
                fraction = { 1f },
                darkMode = darkMode
            ),
            shape = ContainerShapeDefaults.bottomShape,
            onClick = {
                context.apply {
                    copyToClipboard(USDTWallet)
                    scope.launch {
                        toastHostState.showToast(
                            icon = Icons.Rounded.ContentCopy,
                            message = getString(R.string.copied),
                        )
                    }
                }
            },
            endIcon = Icons.Rounded.ContentCopy,
            title = stringResource(R.string.usdt),
            startIcon = Icons.Filled.USDT,
            subtitle = USDTWallet
        )
        Spacer(Modifier.height(16.dp))
    }
}