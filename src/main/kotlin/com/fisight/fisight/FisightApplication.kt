package com.fisight.fisight

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
class FisightApplication

fun main(args: Array<String>) {
    runApplication<FisightApplication>(*args)
}
