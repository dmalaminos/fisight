package com.fisight.fisight.account

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Account(@Id val id: String, val name: String, val bankName: String, val amount: Long)
