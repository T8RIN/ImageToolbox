package ru.tech.imageresizershrinker.presentation.crop_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import ru.tech.imageresizershrinker.domain.model.DomainAspectRatio

@Composable
fun aspectRatios() = remember {
    derivedStateOf {
        DomainAspectRatio.defaultList
    }
}.value