package com.fisight.fisight.account

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface AccountRepository : ReactiveMongoRepository<Account, String>
