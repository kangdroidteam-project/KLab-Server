package com.branch.server.service

import com.branch.server.data.entity.community.Community
import com.branch.server.data.entity.community.CommunityRepository
import com.branch.server.data.entity.median.MedianTable
import com.branch.server.data.entity.median.MedianTableRepository
import com.branch.server.data.request.LoginRequest
import com.branch.server.data.request.RegisterRequest
import com.branch.server.data.response.LoginResponse
import com.branch.server.data.entity.user.User
import com.branch.server.data.entity.user.UserRepository
import com.branch.server.data.response.SimplifiedCommunity
import com.branch.server.data.response.SimplifiedMyPageCommunity
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
    private val jwtTokenProvider: JWTTokenProvider,
    private val medianTableRepository: MedianTableRepository,
    private val communityRepository: CommunityRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun registerUser(registerRequest: RegisterRequest) {
        logger.info("Register requested for user: ${registerRequest.userId}, name: ${registerRequest.userName}")

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

    fun registerClass(userToken: String, communityId: Long): List<SimplifiedMyPageCommunity> {
        val user: User = userRepository.findByUserId(jwtTokenProvider.getUserPk(userToken))
        val medianTable: MedianTable = MedianTable(
            targetUser = user,
            targetCommunity = communityRepository.findById(communityId)
        )
        medianTableRepository.save(medianTable)

        return getUserRegisteredCommunity(user.userId)
    }

    fun getDetailedClassInfo(communityId: Long): Community {
        return communityRepository.findById(communityId)
    }

    fun getSimpleClassList(): List<SimplifiedCommunity> {
        return communityRepository.findAll().filter {
            !it.isCommunityExpired
        }.map {
            SimplifiedCommunity(
                id = it.id,
                contentTitle = it.contentTitle,
                contentRecruitment = it.contentRecruitment,
                currentRecruitment = it.currentRecruitment,
                contentNeeds = it.contentNeeds
            )
        }
    }

    private fun getUserRegisteredCommunity(userId: String): List<SimplifiedMyPageCommunity> = medianTableRepository.findAllByTargetUser_UserId(userId).map {
        SimplifiedMyPageCommunity(
            id = it.targetCommunity.id, // Class ID
            contentTitle = it.targetCommunity.contentTitle, // Class Title
            startTime = it.targetCommunity.gardenReservation.reservationStartTime, // Class Reservation Time
            contentNeeds = it.targetCommunity.contentNeeds, // Class Contents
            isRequestConfirmed = it.isRequestConfirmed // Is Request Confirmed
        )
    }
}