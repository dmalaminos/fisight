package com.fisight.comment

import com.fisight.location.Location
import com.fisight.trade.currency.CurrencyTrade
import com.fisight.transfer.Transfer
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import org.hibernate.annotations.AnyMetaDef
import org.hibernate.annotations.MetaValue
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