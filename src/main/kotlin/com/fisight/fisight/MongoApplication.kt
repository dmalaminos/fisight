package com.fisight.fisight

import com.mongodb.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories
class MongoApplication : AbstractMongoConfiguration() {
    @Bean
    override fun mongoClient(): MongoClient = MongoClient()

    override fun getDatabaseName(): String = "fisight"
}