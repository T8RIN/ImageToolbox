package ru.tech.imageresizershrinker.domain.use_case.edit_settings

import ru.tech.imageresizershrinker.domain.model.FontFam
import ru.tech.imageresizershrinker.domain.repository.SettingsRepository
import javax.inject.Inject

class SetFontUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(font: FontFam) = settingsRepository.setFont(font)
}