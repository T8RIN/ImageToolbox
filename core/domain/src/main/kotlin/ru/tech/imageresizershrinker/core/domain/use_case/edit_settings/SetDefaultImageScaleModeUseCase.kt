package ru.tech.imageresizershrinker.core.domain.use_case.edit_settings

import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import javax.inject.Inject


class SetDefaultImageScaleModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        imageScaleMode: ImageScaleMode
    ) = settingsRepository.setDefaultImageScaleMode(imageScaleMode)
}