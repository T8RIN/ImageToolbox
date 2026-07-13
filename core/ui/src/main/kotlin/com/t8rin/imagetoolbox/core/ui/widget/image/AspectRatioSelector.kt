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

package com.t8rin.imagetoolbox.core.ui.widget.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.cropper.model.AspectRatio
import com.t8rin.cropper.model.CropAspectRatio
import com.t8rin.cropper.util.createRectShape
import com.t8rin.cropper.widget.AspectRatioSelectionCard
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.CropFree
import com.t8rin.imagetoolbox.core.resources.icons.DashboardCustomize
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Image
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun AspectRatioSelector(
    modifier: Modifier = Modifier,
    selectedAspectRatio: DomainAspectRatio? = DomainAspectRatio.Free,
    unselectedCardColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    contentPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        top = 4.dp,
        bottom = 16.dp,
        end = 16.dp + WindowInsets
            .navigationBars
            .asPaddingValues()
            .calculateEndPadding(LocalLayoutDirection.current)
    ),
    title: @Composable ColumnScope.() -> Unit = {
        Text(
            text = stringResource(id = R.string.aspect_ratio),
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 16.dp,
                    bottom = 8.dp
                ),
            fontWeight = FontWeight.Medium
        )
    },
    enableFadingEdges: Boolean = true,
    onAspectRatioChange: (DomainAspectRatio, AspectRatio) -> Unit,
    color: Color = Color.Unspecified,
    shape: Shape = ShapeDefaults.extraLarge,
    excludedAspectRatios: Set<DomainAspectRatio> = emptySet()
) {
    val settingsState = LocalSettingsState.current
    val settingsInteractor = LocalSimpleSettingsInteractor.current
    val scope = rememberCoroutineScope()
    val aspectRatios by remember(settingsState.aspectRatios, excludedAspectRatios) {
        derivedStateOf {
            settingsState.aspectRatios.filterNot(excludedAspectRatios::contains)
        }
    }
    val savedAspectRatios by remember(settingsState.aspectRatios) {
        derivedStateOf {
            settingsState.aspectRatios
                .filterIsInstance<DomainAspectRatio.Custom>()
                .filter(DomainAspectRatio.Custom::isSaved)
        }
    }
    val externalCustomSelected = selectedAspectRatio
        .let { it is DomainAspectRatio.Custom && !it.isSaved }
    var editingCustomAspectRatio by remember(externalCustomSelected) {
        mutableStateOf(
            (selectedAspectRatio as? DomainAspectRatio.Custom)?.takeUnless { it.isSaved }
        )
    }
    var selectedAspectRatioOverride by remember {
        mutableStateOf<DomainAspectRatio?>(null)
    }
    LaunchedEffect(selectedAspectRatio) {
        selectedAspectRatioOverride = null
        editingCustomAspectRatio?.let { editing ->
            val selectedValue = selectedAspectRatio?.value
            if (
                selectedValue == null ||
                abs(selectedValue - editing.value) >= ASPECT_RATIO_TOLERANCE
            ) {
                editingCustomAspectRatio = null
            }
        }
    }
    val actualSelectedAspectRatio = selectedAspectRatioOverride
        ?: editingCustomAspectRatio
        ?: selectedAspectRatio
    val hasExactSelection = actualSelectedAspectRatio in aspectRatios
    val editingCustomSaved = editingCustomAspectRatio?.let { editing ->
        savedAspectRatios.any {
            it.widthProportion == editing.widthProportion &&
                    it.heightProportion == editing.heightProportion
        }
    } == true

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .container(
                color = color,
                shape = shape,
                resultPadding = 0.dp
            )
            .clearFocusOnTap()
    ) {
        title()
        val listState = rememberLazyListState()
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            contentPadding = contentPadding,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fadingEdges(
                scrollableState = listState,
                enabled = enableFadingEdges
            ),
            flingBehavior = enhancedFlingBehavior()
        ) {
            itemsIndexed(
                items = aspectRatios,
                key = { _, aspectRatio ->
                    "${aspectRatio::class.simpleName}-${aspectRatio.widthProportion}-" +
                            "${aspectRatio.heightProportion}-" +
                            "${(aspectRatio as? DomainAspectRatio.Custom)?.isSaved}"
                }
            ) { index, item ->
                val isCustomEditor = item is DomainAspectRatio.Custom && !item.isSaved
                val selected = when {
                    isCustomEditor ->
                        actualSelectedAspectRatio is DomainAspectRatio.Custom &&
                                !actualSelectedAspectRatio.isSaved

                    item == actualSelectedAspectRatio -> true
                    hasExactSelection -> false
                    item == DomainAspectRatio.Free || item == DomainAspectRatio.Original -> false
                    actualSelectedAspectRatio == null -> false
                    actualSelectedAspectRatio == DomainAspectRatio.Free -> false
                    actualSelectedAspectRatio == DomainAspectRatio.Original -> false
                    else -> abs(
                        item.value - actualSelectedAspectRatio.value
                    ) < ASPECT_RATIO_TOLERANCE
                }
                val cropAspectRatio = item.toCropAspectRatio(
                    original = stringResource(R.string.original),
                    free = stringResource(R.string.free),
                    custom = stringResource(R.string.custom)
                )
                val isNumeric by remember(item) {
                    derivedStateOf {
                        item is DomainAspectRatio.Numeric ||
                                item is DomainAspectRatio.Custom && item.isSaved
                    }
                }

                val interactionSource = remember { MutableInteractionSource() }

                val cardShape = shapeByInteraction(
                    shape = ShapeDefaults.default,
                    pressedShape = ShapeDefaults.pressed,
                    interactionSource = interactionSource
                )

                if (isNumeric) {
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .animateItem()
                    ) {
                        AspectRatioSelectionCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .container(
                                    resultPadding = 0.dp,
                                    color = animateColorAsState(
                                        targetValue = if (selected) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else unselectedCardColor,
                                    ).value,
                                    borderColor = takeColorFromScheme {
                                        if (selected) onPrimaryContainer.copy(0.7f)
                                        else outlineVariant()
                                    },
                                    shape = cardShape
                                )
                                .hapticsClickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current
                                ) {
                                    selectedAspectRatioOverride = item
                                    editingCustomAspectRatio = null
                                    onAspectRatioChange(
                                        aspectRatios[index],
                                        cropAspectRatio.aspectRatio
                                    )
                                }
                                .padding(
                                    start = 12.dp,
                                    top = 12.dp,
                                    end = 12.dp,
                                    bottom = 2.dp
                                ),
                            contentColor = Color.Transparent,
                            color = MaterialTheme.colorScheme.onSurface,
                            cropAspectRatio = cropAspectRatio
                        )
                        if (item is DomainAspectRatio.Custom && item.isSaved) {
                            EnhancedIconButton(
                                onClick = {
                                    scope.launch {
                                        settingsInteractor.setAspectRatios(
                                            savedAspectRatios.filterNot { it == item }
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = -(4.dp))
                                    .size(22.dp),
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                forceMinimumInteractiveComponentSize = false
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = stringResource(R.string.delete),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .animateItem()
                            .height(104.dp)
                            .container(
                                resultPadding = 0.dp,
                                color = animateColorAsState(
                                    targetValue = when {
                                        isCustomEditor && editingCustomSaved -> MaterialTheme.colorScheme.secondaryContainer
                                        selected -> MaterialTheme.colorScheme.primaryContainer
                                        else -> unselectedCardColor
                                    },
                                ).value,
                                borderColor = takeColorFromScheme {
                                    if (selected) onPrimaryContainer.copy(0.7f)
                                    else outlineVariant()
                                },
                                shape = cardShape
                            )
                            .hapticsClickable(
                                interactionSource = interactionSource,
                                indication = LocalIndication.current
                            ) {
                                if (!selected) {
                                    if (isCustomEditor) {
                                        selectedAspectRatioOverride = null
                                        editingCustomAspectRatio = item
                                    } else {
                                        selectedAspectRatioOverride = item
                                        editingCustomAspectRatio = null
                                    }
                                    onAspectRatioChange(
                                        aspectRatios[index],
                                        cropAspectRatio.aspectRatio
                                    )
                                }
                            }
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (item) {
                                is DomainAspectRatio.Original -> {
                                    Icon(
                                        imageVector = Icons.Outlined.Image,
                                        contentDescription = null
                                    )
                                }

                                is DomainAspectRatio.Free -> {
                                    Icon(
                                        imageVector = Icons.Rounded.CropFree,
                                        contentDescription = null
                                    )
                                }

                                else -> {
                                    Icon(
                                        imageVector = Icons.Outlined.DashboardCustomize,
                                        contentDescription = null
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = cropAspectRatio.title,
                                fontSize = 14.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = editingCustomAspectRatio != null) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                var tempWidth by remember {
                    mutableStateOf(editingCustomAspectRatio?.widthProportion?.toString() ?: "1")
                }
                var tempHeight by remember {
                    mutableStateOf(editingCustomAspectRatio?.heightProportion?.toString() ?: "1")
                }
                Row(
                    modifier = Modifier
                        .padding()
                        .container(
                            shape = AutoCornersShape(
                                topStart = 24.dp,
                                topEnd = 24.dp,
                                bottomEnd = 4.dp,
                                bottomStart = 4.dp
                            ),
                            color = unselectedCardColor
                        )
                ) {
                    RoundedTextField(
                        value = tempWidth,
                        onValueChange = { value ->
                            tempWidth = value
                            val width = abs(value.toFloatOrNull() ?: 0f).coerceAtLeast(1f)

                            editingCustomAspectRatio?.let { aspect ->
                                val updatedAspectRatio = aspect.copy(widthProportion = width)
                                editingCustomAspectRatio = updatedAspectRatio
                                onAspectRatioChange(
                                    updatedAspectRatio,
                                    AspectRatio(
                                        (width / aspect.heightProportion).takeIf { !it.isNaN() }
                                            ?: 1f
                                    )
                                )
                            }
                        },
                        shape = ShapeDefaults.smallStart,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        label = {
                            Text(stringResource(R.string.width, " "))
                        },
                        supportingText = abs(tempWidth.toFloatOrNull() ?: 0f)
                            .takeIf { it < 1f }
                            ?.let {
                                {
                                    Text(stringResource(R.string.minimum_value_is, 1))
                                }
                            },
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = 8.dp,
                                top = 8.dp,
                                bottom = 8.dp,
                                end = 2.dp
                            )
                    )
                    RoundedTextField(
                        value = tempHeight,
                        onValueChange = { value ->
                            tempHeight = value
                            val height = abs(value.toFloatOrNull() ?: 1f).coerceAtLeast(1f)

                            editingCustomAspectRatio?.let { aspect ->
                                val updatedAspectRatio = aspect.copy(heightProportion = height)
                                editingCustomAspectRatio = updatedAspectRatio
                                onAspectRatioChange(
                                    updatedAspectRatio,
                                    AspectRatio(
                                        (aspect.widthProportion / height).takeIf { !it.isNaN() }
                                            ?: 1f
                                    )
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        shape = ShapeDefaults.smallEnd,
                        label = {
                            Text(stringResource(R.string.height, " "))
                        },
                        supportingText = abs(tempHeight.toFloatOrNull() ?: 0f)
                            .takeIf { it < 1f }
                            ?.let {
                                {
                                    Text(stringResource(R.string.minimum_value_is, 1))
                                }
                            },
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = 2.dp,
                                top = 8.dp,
                                bottom = 8.dp,
                                end = 8.dp
                            ),
                    )
                }
                val width = tempWidth.toFloatOrNull()?.let(::abs)
                val height = tempHeight.toFloatOrNull()?.let(::abs)
                val canSave = width != null && height != null &&
                        width.isFinite() && height.isFinite() &&
                        width >= 1f && height >= 1f &&
                        aspectRatios.none {
                            it != DomainAspectRatio.Free &&
                                    it != DomainAspectRatio.Original &&
                                    (it !is DomainAspectRatio.Custom || it.isSaved) &&
                                    abs(it.value - width / height) < ASPECT_RATIO_TOLERANCE
                        }

                EnhancedButton(
                    onClick = {
                        scope.launch {
                            settingsInteractor.setAspectRatios(
                                listOf(
                                    DomainAspectRatio.Custom(
                                        widthProportion = width ?: 1f,
                                        heightProportion = height ?: 1f,
                                        isSaved = true
                                    )
                                ) + savedAspectRatios
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = AutoCornersShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomEnd = 24.dp,
                        bottomStart = 24.dp
                    ),
                    enabled = canSave
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

private const val ASPECT_RATIO_TOLERANCE = 0.0001f

fun DomainAspectRatio.toCropAspectRatio(
    original: String,
    free: String,
    custom: String
): CropAspectRatio = when (this) {
    is DomainAspectRatio.Original -> {
        CropAspectRatio(
            title = original,
            shape = createRectShape(AspectRatio.Original),
            aspectRatio = AspectRatio.Original
        )
    }

    is DomainAspectRatio.Free -> {
        CropAspectRatio(
            title = free,
            shape = createRectShape(AspectRatio.Original),
            aspectRatio = AspectRatio.Original
        )
    }

    is DomainAspectRatio.Custom -> {
        if (isSaved) {
            val width = widthProportion.toString().trimTrailingZero()
            val height = heightProportion.toString().trimTrailingZero()
            CropAspectRatio(
                title = "$width:$height",
                shape = createRectShape(AspectRatio(value)),
                aspectRatio = AspectRatio(value)
            )
        } else {
            CropAspectRatio(
                title = custom,
                shape = createRectShape(AspectRatio(value)),
                aspectRatio = AspectRatio(value)
            )
        }
    }

    else -> {
        val width = widthProportion.toString()
            .trimTrailingZero()
        val height = heightProportion.toString()
            .trimTrailingZero()
        CropAspectRatio(
            title = "$width:$height",
            shape = createRectShape(AspectRatio(value)),
            aspectRatio = AspectRatio(value)
        )
    }
}