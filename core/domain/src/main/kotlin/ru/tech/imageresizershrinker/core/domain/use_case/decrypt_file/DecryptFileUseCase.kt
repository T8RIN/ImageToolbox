package ru.tech.imageresizershrinker.core.domain.use_case.decrypt_file

import ru.tech.imageresizershrinker.core.domain.repository.CipherRepository
import javax.inject.Inject

class DecryptFileUseCase @Inject constructor(
    private val repository: CipherRepository
) {
    suspend operator fun invoke(
        data: ByteArray,
        key: String
    ): ByteArray = repository.decrypt(data, key)
}