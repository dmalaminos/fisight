package com.fisight

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FisightApplication

fun main(args: Array<String>) {
    runApplication<FisightApplication>(*args)
}