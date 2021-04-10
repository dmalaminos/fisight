package com.fisight.movement

import com.fisight.comment.Commentable
import com.fisight.location.Location
import com.fisight.money.Money
import com.fisight.movement.MovementDirection.Inbound
import com.fisight.movement.MovementDirection.Outbound
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Movement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Int,
    val amount: Money,
    @Enumerated(EnumType.STRING)
    val direction: MovementDirection,
    @ManyToOne(fetch = FetchType.LAZY)
    val location: Location,
    @Enumerated(EnumType.STRING)
    val type: MovementType,
    val dateMoved: LocalDateTime
) : Commentable

data class MovementDto(
    val id: Int?,
    val amount: Money,
    val direction: MovementDirection,
    val locationId: Int,
    val type: MovementType,
    val dateMoved: LocalDateTime
)

enum class MovementType(val direction: MovementDirection) {
    Seed(Inbound),
    Wage(Inbound),
    Salary(Inbound),
    Pension(Inbound),
    Interest(Inbound),
    Dividend(Inbound),
    Rental(Inbound),
    Repayment(Inbound),
    Expense(Outbound),
}

enum class MovementDirection {
    Inbound,
    Outbound
}
