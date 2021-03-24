package com.fisight.account

import javax.persistence.Entity

@Entity
data class FinancialLocation(val id: Int, val name: String, val entityName: String)
