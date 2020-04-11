package com.fisight.account

import com.fisight.capital.Capital
import javax.persistence.Embedded
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class Account(@EmbeddedId val id: AccountId, val name: String, val bankName: String, @Embedded val capital: Capital)
