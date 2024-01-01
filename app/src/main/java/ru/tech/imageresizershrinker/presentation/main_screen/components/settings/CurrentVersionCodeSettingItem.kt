@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.pulsate
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun CurrentVersionCodeSettingItem(
    updateAvailable: Boolean,
    onTryGetUpdate: () -> Unit,
    shape: Shape = ContainerShapeDefaults.topShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRow(
        applyHorPadding = false,
        shape = shape,
        modifier = Modifier
            .pulsate(
                enabled = updateAvailable,
                range = 0.98f..1.02f
            )
            .then(modifier),
        title = stringResource(R.string.version),
        subtitle = remember {
            "${BuildConfig.VERSION_NAME}${if (BuildConfig.FLAVOR == "foss") "-foss" else ""} (${BuildConfig.VERSION_CODE})"
        },
        startContent = {
            Icon(
                imageVector = Icons.Outlined.Verified,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        endContent = {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_monochrome),
                contentDescription = null,
                tint = animateColorAsState(
                    if (settingsState.isNightMode) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.blend(Color.Black)
                    }
                ).value,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(64.dp)
                    .container(
                        resultPadding = 0.dp,
                        color = animateColorAsState(
                            if (settingsState.isNightMode) {
                                MaterialTheme.colorScheme.secondaryContainer.blend(
                                    color = Color.Black,
                                    fraction = 0.3f
                                )
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            }
                        ).value,
                        borderColor = MaterialTheme.colorScheme.outlineVariant(),
                        shape = MaterialStarShape
                    )
                    .scale(1.25f)
            )
        },
        onClick = onTryGetUpdate
    )
}