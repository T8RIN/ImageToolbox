package ru.tech.imageresizershrinker.domain.use_case.edit_settings

import ru.tech.imageresizershrinker.domain.repository.SettingsRepository
import javax.inject.Inject

class SetAlignmentUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(align: Int) = settingsRepository.setAlignment(align)
}