package com.branch.server.data.entity.community

import com.branch.server.data.entity.reservation.GardenReservationRepository
import com.branch.server.error.exception.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface RawCommunityRepository: JpaRepository<Community, Long> {
    fun findAllByContentAuthor(userName: String): List<Community>
}

@Repository
class CommunityRepository(
    private val rawCommunityRepository: RawCommunityRepository,
    private val gardenReservationRepository: GardenReservationRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun findAll(): List<Community> = rawCommunityRepository.findAll()

    fun deleteAll() {
        logger.warn("Removing All entities related to Community!!")
        rawCommunityRepository.deleteAll()
    }

    fun save(community: Community): Community = rawCommunityRepository.save(community)

    fun findById(id: Long): Community {
        return runCatching {
            rawCommunityRepository.findById(id).get()
        }.getOrElse {
            logger.error("Cannot find community object from id: ${id}!")
            logger.error("StackTrace: ${it.stackTraceToString()}")
            throw NotFoundException("Cannot find community object from id: ${id}!")
        }
    }

    fun findAllByContentAuthor(userName: String): List<Community> {
        return rawCommunityRepository.findAllByContentAuthor(userName)
    }
}