package ru.tech.imageresizershrinker.coreui.utils.helper

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues {
    val ld = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(ld) + paddingValues.calculateStartPadding(ld),
        top = calculateTopPadding() + paddingValues.calculateTopPadding(),
        end = calculateEndPadding(ld) + paddingValues.calculateEndPadding(ld),
        bottom = calculateBottomPadding() + paddingValues.calculateBottomPadding(),
    )
}