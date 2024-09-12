package ru.melowetty.apigateway.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import ru.melowetty.apigateway.service.TelegramBotAuthService

@Component
@Order(1)
class TelegramBotAuthFilter(
    private val telegramBotAuthService: TelegramBotAuthService
) : AbstractGatewayFilterFactory<TelegramBotAuthFilter.Config>(Config::class.java) {
    class Config

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val secretKey = request.headers.get(SECRET_KEY_HEADER)?.joinToString("")
                ?: return@GatewayFilter onError(exchange, "Не указан пароль в заголовках!", HttpStatus.BAD_REQUEST)
            if (telegramBotAuthService.checkSecretKey(secretKey)) {
                return@GatewayFilter chain.filter(exchange)
            }

            return@GatewayFilter onError(exchange, "Доступ запрещён!", HttpStatus.FORBIDDEN)
        }
    }

    private fun onError(exchange: ServerWebExchange, error: String, httpStatus: HttpStatus): Mono<Void> {
        val newResponse = exchange.response
        newResponse.setStatusCode(httpStatus)
        newResponse.headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        val responseBody = mutableMapOf<String, Any>()
        responseBody["message"] = error
        val bytes = jacksonObjectMapper().writeValueAsBytes(responseBody)
        val buffer = exchange.response.bufferFactory().wrap(bytes)
        return newResponse.writeWith(Mono.just(buffer))
    }

    companion object {
        private const val SECRET_KEY_HEADER = "X-Secret-Key"
    }
}