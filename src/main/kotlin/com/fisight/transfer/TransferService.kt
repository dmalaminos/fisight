package com.fisight.transfer

import com.fisight.location.LocationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TransferService(
    private val transferRepository: TransferRepository,
    private val locationRepository: LocationRepository,
    private val mapper: TransferMapper
) {
    fun findAll(): List<Transfer> {
        return transferRepository.findAll().toList()
    }

    fun findAllBySourceLocation(sourceLocationId: Int, atDate: LocalDateTime = LocalDateTime.now()): List<Transfer> {
        return transferRepository.findBySourceIdAndDateTransferredBefore(sourceLocationId, atDate)
    }

    fun findAllForLocationBeforeDate(id: Int, atDate: LocalDateTime = LocalDateTime.now()): List<Transfer> {
        return transferRepository.findBySourceIdOrTargetIdAndDateTransferredBefore(id, id, atDate)
    }

    fun findById(id: Int): Optional<Transfer> {
        return transferRepository.findById(id)
    }

    fun save(transferDto: TransferDto): Transfer {
        val source = locationRepository.findById(transferDto.sourceLocationId)
        val target = locationRepository.findById(transferDto.targetLocationId)

        if (!source.isPresent || !target.isPresent) {
            throw IllegalArgumentException("Not all transfer locations were found")
        }

        return transferRepository.save(mapper.toEntity(transferDto, source.get(), target.get()))
    }

    fun deleteById(id: Int) {
        transferRepository.deleteById(id)
    }
}
