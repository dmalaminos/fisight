package com.fisight.fisight.account

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate

data class CreateAccountCommand(@TargetAggregateIdentifier val accountId: AccountId, val name: String, val bankName: String)
data class AccountCreatedEvent(val accountId: AccountId, val name: String, val bankName: String)

@Aggregate
class AccountAggregate  {
    @AggregateIdentifier
    private lateinit var id: AccountId
    private lateinit var name: String
    private lateinit var bankName: String
    private var capital: Double? = 0.0

    constructor()

    @CommandHandler
    constructor(command: CreateAccountCommand) {
        AggregateLifecycle.apply(AccountCreatedEvent(command.accountId, command.name, command.bankName))
        println("CommandHandler, ${command.accountId}, ${command.name}, ${command.bankName}")
    }

    @EventSourcingHandler
    fun on(event: AccountCreatedEvent) {
        id = event.accountId
        name = event.name
        bankName = event.bankName
        capital = 0.0
        println("EventSourcingHandler, ${event.accountId}, ${event.name}, ${event.bankName}")
    }
}