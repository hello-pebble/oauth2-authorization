package com.pebble.basicAuth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.redisson.spring.starter.RedissonAutoConfigurationV2
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication(
    exclude = [
        RedisAutoConfiguration::class,
        RedisRepositoriesAutoConfiguration::class,
        RedissonAutoConfigurationV2::class
    ]
)
@EnableJpaAuditing
@ConfigurationPropertiesScan
class BasicAuthApplication

fun main(args: Array<String>) {
    runApplication<BasicAuthApplication>(*args)
    println("인증 서비스 성공 API 시작")
}
