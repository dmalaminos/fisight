package com.fisight.financialLocation

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FinancialLocationRepository : CrudRepository<FinancialLocation, Int>
