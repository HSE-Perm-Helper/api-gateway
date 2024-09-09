package ru.melowetty.apigateway.service.impl

import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.stereotype.Service
import ru.melowetty.apigateway.service.TelegramBotAuthService

@Service
class TelegramBotAuthServiceImpl(
    private val env: Environment
) : TelegramBotAuthService {
    private var secretKey: String = env["app.security.private-key"]
        ?: throw RuntimeException("Не указан секретный ключ для телеграмм-бота!")

    override fun checkSecretKey(key: String): Boolean {
        return key == secretKey
    }
}