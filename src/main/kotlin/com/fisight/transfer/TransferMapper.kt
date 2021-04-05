package com.fisight.transfer

import com.fisight.location.Location
import org.springframework.stereotype.Component

@Component
class TransferMapper {
    fun toEntity(transferDto: TransferDto, source: Location, target: Location): Transfer {
        return Transfer(
            transferDto.id ?: 0,
            source,
            target,
            transferDto.amount,
            transferDto.fee,
            transferDto.dateTransferred
        )
    }

    fun toDto(transfer: Transfer): TransferDto {
        return TransferDto(
            transfer.id,
            transfer.source.id,
            transfer.target.id,
            transfer.amount,
            transfer.fee,
            transfer.dateTransferred
        )
    }
}