package com.branch.server.service

import com.branch.server.data.request.LoginRequest
import com.branch.server.data.request.RegisterRequest
import com.branch.server.data.response.LoginResponse
import com.branch.server.data.user.User
import com.branch.server.data.user.UserRepository
import com.branch.server.error.exception.ConflictException
import com.branch.server.error.exception.ForbiddenException
import com.branch.server.security.JWTTokenProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncryptorService: PasswordEncryptorService,
    private val jwtTokenProvider: JWTTokenProvider
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun registerUser(registerRequest: RegisterRequest) {
        logger.info("Register requested for user: ${registerRequest.userId}, name: ${registerRequest.userName}")
        checkUserNotExistsOrThrow(registerRequest.userId)

        userRepository.save(
            User(
                userId = registerRequest.userId,
                userPassword = passwordEncryptorService.encodePlainText(registerRequest.userPassword),
                userName = registerRequest.userName,
                userAddress = registerRequest.userAddress,
                userPhoneNumber = registerRequest.userPhoneNumber,
                roles = setOf("ROLE_USER")
            )
        )
    }

    fun loginUser(loginRequest: LoginRequest): LoginResponse {
        logger.info("Login Requested for user: ${loginRequest.userId}")
        val user: User = userRepository.findByUserId(loginRequest.userId)
        if (!passwordEncryptorService.isMatching(loginRequest.userPassword, user.userPassword)) {
            logger.error("Login failed for user: ${loginRequest.userId}, password is not correct!")
            throw ForbiddenException("Password for User ID ${loginRequest.userId} is wrong!")
        }

        return LoginResponse(
            userToken = jwtTokenProvider.createToken(user.userId, user.roles.toList())
        )
    }

    private fun checkUserNotExistsOrThrow(userId: String) {
        runCatching {
            userRepository.findByUserId(userId)
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