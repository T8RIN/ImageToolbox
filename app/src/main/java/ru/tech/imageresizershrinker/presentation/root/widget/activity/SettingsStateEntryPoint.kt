package ru.tech.imageresizershrinker.presentation.root.widget.activity

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.tech.imageresizershrinker.domain.use_case.get_settings_state.GetSettingsStateUseCase

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SettingsStateEntryPoint {
    val getSettingsStateUseCase: GetSettingsStateUseCase
}