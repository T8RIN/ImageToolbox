package ru.tech.imageresizershrinker.coredomain.use_case.generate_random_password

import ru.tech.imageresizershrinker.coredomain.repository.CipherRepository
import javax.inject.Inject

class GenerateRandomPasswordUseCase @Inject constructor(
    private val repository: CipherRepository
) {
    operator fun invoke(
        len: Int
    ): String = repository.generateRandomString(len)
}