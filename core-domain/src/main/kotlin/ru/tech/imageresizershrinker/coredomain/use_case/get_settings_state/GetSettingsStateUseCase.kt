package ru.tech.imageresizershrinker.coredomain.use_case.get_settings_state

import ru.tech.imageresizershrinker.coredomain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsStateUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() = repository.getSettingsState()
}