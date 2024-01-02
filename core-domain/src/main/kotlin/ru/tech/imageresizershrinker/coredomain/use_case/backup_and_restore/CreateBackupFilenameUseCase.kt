package ru.tech.imageresizershrinker.coredomain.use_case.backup_and_restore

import ru.tech.imageresizershrinker.coredomain.repository.SettingsRepository
import javax.inject.Inject

class CreateBackupFilenameUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.createBackupFilename()
}