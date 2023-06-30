package ru.tech.imageresizershrinker.domain.use_case.generate_random_password

import ru.tech.imageresizershrinker.domain.repository.CipherRepository
import javax.inject.Inject

class GenerateRandomPasswordUseCase @Inject constructor(
    private val repository: CipherRepository
) {
    operator fun invoke(
        len: Int
    ): String = repository.generateRandomPassword(len)
}