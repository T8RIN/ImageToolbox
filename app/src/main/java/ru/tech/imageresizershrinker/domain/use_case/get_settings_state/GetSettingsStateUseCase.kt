package ru.tech.imageresizershrinker.domain.use_case.get_settings_state

import ru.tech.imageresizershrinker.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsStateUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() = repository.getSettingsState()
}