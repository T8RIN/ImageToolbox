package ru.tech.imageresizershrinker.domain.use_case.decrypt_file

import ru.tech.imageresizershrinker.domain.repository.CipherRepository
import javax.inject.Inject

class DecryptFileUseCase @Inject constructor(
    private val repository: CipherRepository
) {
    suspend operator fun invoke(
        data: ByteArray,
        key: String
    ): ByteArray = repository.decrypt(data, key)
}