package ru.tech.imageresizershrinker.domain.use_case.backup_and_restore

import ru.tech.imageresizershrinker.domain.repository.SettingsRepository
import javax.inject.Inject

class CreateBackupFilenameUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.createBackupFilename()
}