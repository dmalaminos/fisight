package com.fisight.fisight.account

import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service

@Service
class AccountEventHandler(private val accountRepository: AccountRepository) {
    @EventSourcingHandler
    fun on(event: AccountCreatedEvent) {
        accountRepository.insert(Account(event.accountId, event.name, event.bankName, event.initialCapital))
        println("EventSourcingHandler, ${event.accountId}, ${event.name}, ${event.bankName}")
    }
}