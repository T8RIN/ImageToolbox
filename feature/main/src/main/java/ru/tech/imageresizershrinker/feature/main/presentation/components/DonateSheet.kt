package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.BitcoinWallet
import ru.tech.imageresizershrinker.coredomain.TONSpaceWallet
import ru.tech.imageresizershrinker.coredomain.TONWallet
import ru.tech.imageresizershrinker.coredomain.USDTWallet
import ru.tech.imageresizershrinker.coreui.icons.material.Bitcoin
import ru.tech.imageresizershrinker.coreui.icons.material.Ton
import ru.tech.imageresizershrinker.coreui.icons.material.USDT
import ru.tech.imageresizershrinker.coreui.theme.BitcoinColor
import ru.tech.imageresizershrinker.coreui.theme.TONColor
import ru.tech.imageresizershrinker.coreui.theme.TONSpaceColor
import ru.tech.imageresizershrinker.coreui.theme.USDTColor
import ru.tech.imageresizershrinker.coreui.theme.inverse
import ru.tech.imageresizershrinker.coreui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.coreui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.coreui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.coreui.widget.text.TitleItem
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState


private val topShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 6.dp,
    bottomEnd = 6.dp
)

private val centerShape = RoundedCornerShape(6.dp)

private val bottomShape = RoundedCornerShape(
    topStart = 6.dp,
    topEnd = 6.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

@Composable
fun DonateSheet(
    visible: MutableState<Boolean>
) {
    val context = LocalContext.current
    val toastHostState = LocalToastHost.current
    val scope = rememberCoroutineScope()

    SimpleSheet(
        visible = visible,
        title = {
            TitleItem(
                text = stringResource(R.string.donation),
                icon = Icons.Rounded.Payments
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = Color.Transparent,
                onClick = { visible.value = false },
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            val darkMode = !LocalSettingsState.current.isNightMode
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
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
                    PreferenceItemOverload(
                        color = TONSpaceColor,
                        contentColor = TONSpaceColor.inverse(
                            fraction = { 1f },
                            darkMode = true
                        ),
                        shape = topShape,
                        onClick = {
                            context.apply {
                                copyToClipboard(
                                    label = getString(R.string.ton_space),
                                    value = TONSpaceWallet
                                )
                                scope.launch {
                                    toastHostState.showToast(
                                        icon = Icons.Rounded.ContentCopy,
                                        message = getString(R.string.copied),
                                    )
                                }
                            }
                        },
                        endIcon = {
                            Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = null)
                        },
                        title = stringResource(R.string.ton_space),
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Ton,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(1.5f)
                            )
                        },
                        subtitle = TONSpaceWallet
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItemOverload(
                        color = TONColor,
                        contentColor = TONColor.inverse(
                            fraction = { 1f },
                            darkMode = darkMode
                        ),
                        shape = centerShape,
                        onClick = {
                            context.apply {
                                copyToClipboard(
                                    label = getString(R.string.ton),
                                    value = TONWallet
                                )
                                scope.launch {
                                    toastHostState.showToast(
                                        icon = Icons.Rounded.ContentCopy,
                                        message = getString(R.string.copied),
                                    )
                                }
                            }
                        },
                        endIcon = {
                            Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = null)
                        },
                        title = stringResource(R.string.ton),
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Ton,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(1.5f)
                            )
                        },
                        subtitle = TONWallet
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        color = BitcoinColor,
                        contentColor = BitcoinColor.inverse(
                            fraction = { 1f },
                            darkMode = darkMode
                        ),
                        shape = centerShape,
                        onClick = {
                            context.apply {
                                copyToClipboard(
                                    label = getString(R.string.bitcoin),
                                    value = BitcoinWallet
                                )
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
                        icon = Icons.Filled.Bitcoin,
                        subtitle = BitcoinWallet
                    )
                    Spacer(Modifier.height(4.dp))
                    PreferenceItem(
                        color = USDTColor,
                        shape = bottomShape,
                        contentColor = USDTColor.inverse(
                            fraction = { 1f },
                            darkMode = darkMode
                        ),
                        onClick = {
                            context.apply {
                                copyToClipboard(
                                    label = getString(R.string.usdt),
                                    value = USDTWallet
                                )
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
                        icon = Icons.Filled.USDT,
                        subtitle = USDTWallet
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    )
}