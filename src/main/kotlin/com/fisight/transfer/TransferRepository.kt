package com.fisight.transfer

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransferRepository : CrudRepository<Transfer, Int> {
    fun findBySourceIdAndDateTransferredBefore(id: Int, atDate: LocalDateTime): List<Transfer>
    fun findBySourceIdOrTargetIdAndDateTransferredBefore(
        sourceLocationId: Int,
        targetLocationId: Int,
        atDate: LocalDateTime
    ): List<Transfer>
}
