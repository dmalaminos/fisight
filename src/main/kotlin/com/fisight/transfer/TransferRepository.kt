package com.fisight.transfer

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransferRepository : CrudRepository<Transfer, Int> {
    fun findBySource_Id(id: Int): List<Transfer>
}
