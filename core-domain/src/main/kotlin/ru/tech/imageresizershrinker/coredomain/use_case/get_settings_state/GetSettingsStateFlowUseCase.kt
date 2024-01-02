package ru.tech.imageresizershrinker.coredomain.use_case.get_settings_state

import ru.tech.imageresizershrinker.coredomain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsStateFlowUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.getSettingsStateFlow()
}