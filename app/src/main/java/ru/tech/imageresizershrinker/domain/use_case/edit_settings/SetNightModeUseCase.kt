package ru.tech.imageresizershrinker.domain.use_case.edit_settings

import ru.tech.imageresizershrinker.domain.model.NightMode
import ru.tech.imageresizershrinker.domain.repository.SettingsRepository
import javax.inject.Inject

class SetNightModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(nightMode: NightMode) = settingsRepository.setNightMode(nightMode)
}