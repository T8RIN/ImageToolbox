package ru.tech.imageresizershrinker.core.domain.use_case.edit_settings

import ru.tech.imageresizershrinker.core.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import javax.inject.Inject

class SetCopyToClipboardModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        copyToClipboardMode: CopyToClipboardMode
    ) = settingsRepository.setCopyToClipboardMode(copyToClipboardMode)
}