package com.branch.server.service

import com.branch.server.data.request.RegisterRequest
import com.branch.server.data.user.User
import com.branch.server.data.user.UserRepository
import com.branch.server.error.exception.ConflictException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun registerUser(registerRequest: RegisterRequest) {
        logger.info("Register requested for user: ${registerRequest.userId}, name: ${registerRequest.userName}")
        checkUserNotExistsOrThrow(registerRequest.userId)

        userRepository.addUser(
            User(
                userId = registerRequest.userId,
                userPassword = registerRequest.userPassword,
                userName = registerRequest.userName,
                userAddress = registerRequest.userAddress,
                userPhoneNumber = registerRequest.userPhoneNumber,
                roles = setOf("ROLE_USER")
            )
        )
    }

    private fun checkUserNotExistsOrThrow(userId: String) {
        runCatching {
            userRepository.findUserById(userId)
        }.onFailure {
            logger.info("User $userId is not found.")
            logger.info("Confirmed to register.")
        }.onSuccess {
            logger.error("User $userId already exists!")
            logger.error("Cannot register ${userId}.")
            throw ConflictException("Cannot Register ${userId}. $userId already exists!")
        }
    }
}