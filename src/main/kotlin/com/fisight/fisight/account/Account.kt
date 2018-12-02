package com.fisight.fisight.account

import com.fisight.fisight.capital.Capital
import com.fisight.fisight.capital.Capitalizable
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

data class CreateAccountCommand(@TargetAggregateIdentifier val accountId: AccountId, val name: String, val bankName: String, val initialCapital: Capital)
data class AccountCreatedEvent(val accountId: AccountId, val name: String, val bankName: String, val initialCapital: Capital)

@Aggregate
class Account(@AggregateIdentifier var id: AccountId = AccountId(), var name: String, var bankName: String, override var capital: Capital) : Capitalizable {

    constructor() : this(AccountId("0"), "", "", Capital(0.0))

    @CommandHandler
    constructor(command: CreateAccountCommand) : this(command.accountId, command.name, command.bankName, command.initialCapital) {
        AggregateLifecycle.apply(AccountCreatedEvent(command.accountId, command.name, command.bankName, command.initialCapital))
        println("CommandHandler, ${command.accountId}, ${command.name}, ${command.bankName}")
    }
}