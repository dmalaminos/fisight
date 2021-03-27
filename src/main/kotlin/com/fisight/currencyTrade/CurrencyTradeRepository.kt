package com.fisight.currencyTrade

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyTradeRepository : CrudRepository<CurrencyTrade, Int>