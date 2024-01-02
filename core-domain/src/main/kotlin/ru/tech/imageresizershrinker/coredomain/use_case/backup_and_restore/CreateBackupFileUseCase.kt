package ru.tech.imageresizershrinker.coredomain.use_case.backup_and_restore

import ru.tech.imageresizershrinker.coredomain.repository.SettingsRepository
import javax.inject.Inject

class CreateBackupFileUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() = repository.createBackupFile()
}