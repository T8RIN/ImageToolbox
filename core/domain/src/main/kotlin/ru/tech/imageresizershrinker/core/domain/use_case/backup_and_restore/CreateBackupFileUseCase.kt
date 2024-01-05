package ru.tech.imageresizershrinker.core.domain.use_case.backup_and_restore

import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import javax.inject.Inject

class CreateBackupFileUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() = repository.createBackupFile()
}