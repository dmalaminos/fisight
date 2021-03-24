package com.fisight.account

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FinancialLocationRepository : CrudRepository<FinancialLocation, Int>
