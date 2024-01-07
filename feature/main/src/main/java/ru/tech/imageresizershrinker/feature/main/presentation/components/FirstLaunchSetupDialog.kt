package ru.tech.imageresizershrinker.feature.main.presentation.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.SystemSecurityUpdate
import androidx.compose.material.icons.rounded.Webhook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.Beta
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Suppress("KotlinConstantConditions")
@Composable
fun Context.FirstLaunchSetupDialog(
    toggleAllowBetas: (Boolean) -> Unit,
    toggleShowUpdateDialog: () -> Unit
) {
    val settingsState = LocalSettingsState.current
    var updateOnFirstOpen by rememberSaveable(settingsState.appOpenCount) {
        mutableStateOf(
            true
        )
    }
    LaunchedEffect(Unit) {
        if (settingsState.showDialogOnStartup && BuildConfig.FLAVOR == "foss") {
            toggleShowUpdateDialog()
        }
    }
    if (settingsState.appOpenCount <= 1 && updateOnFirstOpen) {
        AlertDialog(
            modifier = Modifier.alertDialogBorder(),
            onDismissRequest = {},
            icon = {
                Icon(Icons.Rounded.SystemSecurityUpdate, null)
            },
            title = {
                Text(stringResource(R.string.updates))
            },
            text = {
                val state = rememberScrollState()
                ProvideTextStyle(value = LocalTextStyle.current.copy(textAlign = TextAlign.Left)) {
                    Column(
                        modifier = Modifier
                            .fadingEdges(
                                isVertical = true,
                                scrollableState = state,
                                scrollFactor = 1.1f
                            )
                            .verticalScroll(state)
                            .padding(2.dp)
                    ) {
                        if (BuildConfig.FLAVOR == "foss") {
                            PreferenceItem(
                                title = stringResource(id = R.string.attention),
                                subtitle = stringResource(R.string.foss_update_checker_warning),
                                icon = Icons.Rounded.Webhook,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        }
                        PreferenceRowSwitch(
                            shape = if (!isInstalledFromPlayStore()) {
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 6.dp,
                                    bottomEnd = 6.dp
                                )
                            } else RoundedCornerShape(16.dp),
                            modifier = Modifier,
                            applyHorPadding = false,
                            resultModifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            title = stringResource(R.string.check_updates),
                            subtitle = stringResource(R.string.check_updates_sub),
                            checked = settingsState.showDialogOnStartup,
                            onClick = {
                                toggleShowUpdateDialog()
                            },
                            startContent = {
                                Icon(
                                    Icons.Rounded.NewReleases,
                                    null,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                            }
                        )
                        if (!isInstalledFromPlayStore()) {
                            Spacer(Modifier.height(4.dp))
                            PreferenceRowSwitch(
                                modifier = Modifier,
                                applyHorPadding = false,
                                resultModifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 6.dp,
                                    topEnd = 6.dp,
                                    bottomStart = 16.dp,
                                    bottomEnd = 16.dp
                                ),
                                title = stringResource(R.string.allow_betas),
                                subtitle = stringResource(R.string.allow_betas_sub),
                                checked = settingsState.allowBetas,
                                onClick = {
                                    toggleAllowBetas(
                                        isInstalledFromPlayStore()
                                    )
                                },
                                startContent = {
                                    Icon(
                                        Icons.Rounded.Beta,
                                        null,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                EnhancedButton(
                    onClick = { updateOnFirstOpen = false }
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }
}