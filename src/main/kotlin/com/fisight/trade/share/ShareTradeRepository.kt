package com.fisight.trade.share

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ShareTradeRepository : CrudRepository<ShareTrade, Int> {
    fun findByLocationId(locationId: Int): List<ShareTrade>
}