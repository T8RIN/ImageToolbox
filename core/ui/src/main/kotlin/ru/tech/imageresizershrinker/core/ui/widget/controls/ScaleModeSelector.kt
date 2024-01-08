package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.buttons.SupportingButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScaleModeSelector(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    shape: Shape = RoundedCornerShape(24.dp),
    enableItemsCardBackground: Boolean = true,
    value: ImageScaleMode,
    titlePadding: PaddingValues = PaddingValues(top = 8.dp),
    titleArrangement: Arrangement.Horizontal = Arrangement.Center,
    onValueChange: (ImageScaleMode) -> Unit,
    title: @Composable RowScope.() -> Unit = {
        Text(
            text = stringResource(R.string.scale_mode),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
) {
    val state = rememberSaveable { mutableStateOf(false) }
    val items = remember {
        ImageScaleMode.entries
    }
    val settingsState = LocalSettingsState.current

    LaunchedEffect(settingsState) {
        if (value != settingsState.defaultImageScaleMode) {
            onValueChange(settingsState.defaultImageScaleMode)
        }
    }

    Column(
        modifier = modifier
            .container(
                shape = shape,
                color = backgroundColor
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(titlePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = titleArrangement
        ) {
            title()
            Spacer(modifier = Modifier.width(8.dp))
            SupportingButton(
                onClick = {
                    state.value = true
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterVertically
            ),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .then(
                    if (enableItemsCardBackground) {
                        Modifier
                            .container(color = MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    } else Modifier
                )
        ) {
            items.forEach {
                EnhancedChip(
                    onClick = {
                        onValueChange(it)
                    },
                    selected = it == value,
                    label = {
                        Text(text = it.title)
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    selectedColor = MaterialTheme.colorScheme.outlineVariant(0.2f, MaterialTheme.colorScheme.tertiaryContainer),
                    selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    unselectedColor = MaterialTheme.colorScheme.outlineVariant(0.01f),
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    SimpleSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ContainerShapeDefaults.shapeForIndex(
                                    index,
                                    items.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(text = item.title)
                        Text(
                            text = item.subtitle,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        visible = state,
        title = {
            TitleItem(text = stringResource(R.string.scale_mode))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { state.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

private val ImageScaleMode.title: String
    @Composable
    get() = when (this) {
        ImageScaleMode.Bilinear -> stringResource(id = R.string.bilinear)
        ImageScaleMode.Catmull -> stringResource(id = R.string.catmull)
        ImageScaleMode.Bicubic -> stringResource(id = R.string.bicubic)
        ImageScaleMode.Hann -> stringResource(id = R.string.hann)
        ImageScaleMode.Hermite -> stringResource(id = R.string.hermite)
        ImageScaleMode.Lanczos -> stringResource(id = R.string.lanczos)
        ImageScaleMode.Mitchell -> stringResource(id = R.string.mitchell)
        ImageScaleMode.Nearest -> stringResource(id = R.string.nearest)
        ImageScaleMode.Spline -> stringResource(id = R.string.spline)
        else -> stringResource(id = R.string.basic)
    }

private val ImageScaleMode.subtitle: String
    @Composable
    get() = when (this) {
        ImageScaleMode.Bilinear -> stringResource(id = R.string.bilinear_sub)
        ImageScaleMode.Catmull -> stringResource(id = R.string.catmull_sub)
        ImageScaleMode.Bicubic -> stringResource(id = R.string.bicubic_sub)
        ImageScaleMode.Hann -> stringResource(id = R.string.hann_sub)
        ImageScaleMode.Hermite -> stringResource(id = R.string.hermite_sub)
        ImageScaleMode.Lanczos -> stringResource(id = R.string.lanczos_sub)
        ImageScaleMode.Mitchell -> stringResource(id = R.string.mitchell_sub)
        ImageScaleMode.Nearest -> stringResource(id = R.string.nearest_sub)
        ImageScaleMode.Spline -> stringResource(id = R.string.spline_sub)
        else -> stringResource(id = R.string.basic_sub)
    }
