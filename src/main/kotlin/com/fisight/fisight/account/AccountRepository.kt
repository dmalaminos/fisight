package com.fisight.fisight.account

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository("accountQueryRepository")
interface AccountRepository : MongoRepository<Account, String>