package com.fisight.fisight.account

import com.fisight.fisight.capital.Capital
import com.fisight.fisight.capital.Capitalizable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Account(@Id val id: String, val name: String, val bankName: String, override val capital: Capital) : Capitalizable
