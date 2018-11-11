package com.fisight.fisight.financialasset

import com.fisight.fisight.capital.Capital
import com.fisight.fisight.capital.Capitalizable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class FinancialAsset(@Id val id: String, val name: String, override val capital: Capital) : Capitalizable