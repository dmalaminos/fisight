package com.fisight.transfer

import com.fisight.location.Location
import com.fisight.location.LocationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TransferService(
    private val transferRepository: TransferRepository,
    private val locationRepository: LocationRepository
) {
    fun findAll(): List<TransferDto> {
        return transferRepository.findAll().map { transferEntityToDto(it) }
    }

    fun findAllBySourceLocation(sourceLocationId: Int): List<TransferDto> {
        return transferRepository.findBySource_Id(sourceLocationId).map { transferEntityToDto(it) }
    }

    fun findById(id: Int): Optional<TransferDto> {
        return transferRepository.findById(id).map { transferEntityToDto(it) }
    }

    fun save(transferDto: TransferDto): TransferDto {
        val source = locationRepository.findById(transferDto.sourceLocationId)
        val target = locationRepository.findById(transferDto.targetLocationId)

        if (!source.isPresent || !target.isPresent) {
            throw IllegalArgumentException("Not all transfer locations were found")
        }

        return transferEntityToDto(
            transferRepository.save(transferDtoToEntity(transferDto, source.get(), target.get()))
        )
    }

    fun deleteById(id: Int) {
        transferRepository.deleteById(id)
    }

    private fun transferDtoToEntity(transferDto: TransferDto, source: Location, target: Location): Transfer {
        return Transfer(
            transferDto.id ?: 0,
            source,
            target,
            transferDto.amount,
            transferDto.dateTransferred
        )
    }

    private fun transferEntityToDto(transfer: Transfer): TransferDto {
        return TransferDto(
            transfer.id,
            transfer.source.id,
            transfer.target.id,
            transfer.amount,
            transfer.dateTransferred
        )
    }
}
