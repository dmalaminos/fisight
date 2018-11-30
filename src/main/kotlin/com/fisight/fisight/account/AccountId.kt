package com.fisight.fisight.account

import org.axonframework.common.IdentifierFactory
import java.io.Serializable

data class AccountId(val identifier: String = IdentifierFactory.getInstance().generateIdentifier()) : Serializable {
    companion object {
        private const val serialVersionUID = -4002624465286943722L
    }
}