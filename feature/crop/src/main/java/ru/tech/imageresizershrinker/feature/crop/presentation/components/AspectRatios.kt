package ru.tech.imageresizershrinker.feature.crop.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import ru.tech.imageresizershrinker.coredomain.model.DomainAspectRatio

@Composable
fun aspectRatios() = remember {
    derivedStateOf {
        DomainAspectRatio.defaultList
    }
}.value