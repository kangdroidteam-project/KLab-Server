package com.branch.server.data.user

import com.branch.server.error.exception.ConflictException
import com.branch.server.error.exception.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface RawUserRepository: JpaRepository<User, Long> {
    fun findByUserId(userId: String): User
}

@Repository
class UserRepository(
    private val rawUserRepository: RawUserRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun save(user: User): User {
        return runCatching {
            rawUserRepository.save(user)
        }.getOrElse {
            logger.error("Error occurred while saving entity: ${it.message}")
            logger.error(it.stackTraceToString())
            throw ConflictException("Duplicated Entity[id: ${user.userId}] is found!")
        }
    }
    fun deleteAll() = rawUserRepository.deleteAll()
    fun findAll(): List<User> = rawUserRepository.findAll()
    fun findByUserId(userId: String): User {
        return runCatching {
            rawUserRepository.findByUserId(userId)
        }.getOrElse {
            logger.error("Error occurred: ${it.stackTraceToString()}")
            logger.error("Cannot get user data from DB! [UserId: $userId]")
            throw NotFoundException("Cannot get user data!")
        }
    }
}