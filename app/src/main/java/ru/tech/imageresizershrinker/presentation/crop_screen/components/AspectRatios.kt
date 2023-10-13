package ru.tech.imageresizershrinker.presentation.crop_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.AspectRatio

@Composable
fun aspectRatios(
    original: String = stringResource(R.string.original)
) = remember(original) {
    derivedStateOf {
        AspectRatio.defaultList.map { it.toCropAspectRatio(original) }
    }
}.value