package com.fisight.account

import java.io.Serializable
import java.util.UUID
import javax.persistence.Embeddable

@Embeddable
data class AccountId(val id: String = UUID.randomUUID().toString()) : Serializable
