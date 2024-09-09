package ru.melowetty.apigateway.service

interface TelegramBotAuthService {
    fun checkSecretKey(key: String): Boolean
}