package com.fisight.financialLocation

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class FinancialLocation(@Id val id: Int, val name: String, val entityName: String)
