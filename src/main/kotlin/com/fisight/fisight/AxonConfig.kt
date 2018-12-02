package com.fisight.fisight

import com.mongodb.MongoClient
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.axonframework.mongo.DefaultMongoTemplate
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfig {
    @Bean
    fun eventStorageEngine(client: MongoClient) = MongoEventStorageEngine(DefaultMongoTemplate(client))

    @Bean
    fun snapshotterFactory() = SpringAggregateSnapshotterFactoryBean()

    @Bean
    fun eventCountSnapshot(snapshotter: Snapshotter) = EventCountSnapshotTriggerDefinition(snapshotter, 50)
}