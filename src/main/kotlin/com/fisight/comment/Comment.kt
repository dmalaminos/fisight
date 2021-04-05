package com.fisight.comment

import com.fisight.currencyTrade.CurrencyTrade
import com.fisight.location.Location
import com.fisight.transfer.Transfer
import org.hibernate.annotations.AnyMetaDef
import org.hibernate.annotations.MetaValue
import javax.persistence.*
import org.hibernate.annotations.Any as AnyRef

@Entity
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val text: String,
    @AnyRef(metaColumn = Column(name = "entity_type"))
    @AnyMetaDef(
        idType = "int", metaType = "string", metaValues = [
            MetaValue(targetEntity = Location::class, value = "Location"),
            MetaValue(targetEntity = Transfer::class, value = "Transfer"),
            MetaValue(targetEntity = CurrencyTrade::class, value = "CurrencyTrade")
        ]
    )
    @JoinColumn(name = "entity_id")
    var commentedEntity: Commentable,
)