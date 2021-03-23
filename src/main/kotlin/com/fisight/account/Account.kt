package com.fisight.account

import com.fisight.capital.Capital
import javax.persistence.Embedded
import javax.persistence.Entity

@Entity
data class Account(val id: Int, val name: String, val bankName: String, @Embedded val capital: Capital)
