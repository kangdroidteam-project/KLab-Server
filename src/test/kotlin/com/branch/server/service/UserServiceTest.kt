package com.branch.server.service

import com.branch.server.data.entity.community.Community
import com.branch.server.data.entity.community.CommunityRepository
import com.branch.server.data.entity.median.MedianTable
import com.branch.server.data.entity.median.MedianTableRepository
import com.branch.server.data.entity.reservation.GardenReservation
import com.branch.server.data.request.LoginRequest
import com.branch.server.data.request.RegisterRequest
import com.branch.server.data.entity.user.User
import com.branch.server.data.entity.user.UserRepository
import com.branch.server.data.request.CommunityAddRequest
import com.branch.server.data.request.GardenReservationRequest
import com.branch.server.data.response.ManagerConfirmCommunity
import com.branch.server.data.response.SealedUser
import com.branch.server.data.response.SimplifiedCommunity
import com.branch.server.data.response.SimplifiedMyPageCommunity
import com.branch.server.error.exception.ConflictException
import com.branch.server.error.exception.ForbiddenException
import com.branch.server.security.JWTTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class UserServiceTest {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var communityRepository: CommunityRepository

    @Autowired
    private lateinit var medianRepository: MedianTableRepository

    @Autowired
    private lateinit var jwtTokenProvider: JWTTokenProvider

    @BeforeEach
    @AfterEach
    fun initTest() {
        medianRepository.deleteAll()
        userRepository.deleteAll()
        communityRepository.deleteAll()
    }

    private val loginUser: User = createMockUser("kangdroid")

    private fun login(): String {
        userService.registerUser(
            RegisterRequest(
                userId = loginUser.userId,
                userName = loginUser.userName,
                userPassword = loginUser.userPassword,
                userPhoneNumber = loginUser.userPhoneNumber,
                userAddress = loginUser.userAddress,
                userIntroduction = ""
            )
        )

        return userService.loginUser(
            LoginRequest(
                userId = loginUser.userId,
                userPassword = loginUser.userPassword
            )
        ).userToken
    }

    private fun createCommunityObject(reservationSpace: String, expired: Boolean = false): Community {
        val secondReservation: GardenReservation = GardenReservation(
            reservationStartTime = System.currentTimeMillis(),
            reservationSpace = reservationSpace
        )

        return Community(
            contentTitle = "Class Test",
            contentAuthor = "KangDroid",
            innerContent = "We are~",
            contentNeeds = "Pencils",
            contentDeadline = "2021.06",
            firstMeeting = "2021.somewhen",
            contentRecruitment = 10,
            currentRecruitment = 5,
            isCommunityExpired = expired,
            gardenReservation = secondReservation
        )
    }

    private fun createMockUser(userId: String): User = User(
        userId = userId,
        userPassword = userId,
        userName = userId,
        userAddress = "test",
        userPhoneNumber = "test"
    )


    @Test
    fun is_registerUser_works_well() {
        val mockRequest: RegisterRequest = RegisterRequest(
            userId = "test",
            userPassword = "test",
            userAddress = "test",
            userPhoneNumber = "test",
            userName = "test",
            userIntroduction = ""
        )

        runCatching {
            userService.registerUser(mockRequest)
        }.onFailure {
            println(it.stackTraceToString())
            fail("We are registering this user first time, but it failed?")
        }.onSuccess {
            val user: User = userRepository.findByUserId(mockRequest.userId)
            assertThat(user.userId).isEqualTo(mockRequest.userId)
            assertThat(user.userPassword).isNotEqualTo(mockRequest.userPassword)
        }

        // Request one more
        runCatching {
            userService.registerUser(mockRequest)
        }.onSuccess {
            fail("Duplicated should exists, but it succeed?")
        }.onFailure {
            println(it.stackTraceToString())
            assertThat(it is ConflictException).isEqualTo(true)
        }
    }

    @Test
    fun is_loginUser_works_well() {
        val mockRequest: RegisterRequest = RegisterRequest(
            userId = "test",
            userPassword = "test",
            userAddress = "test",
            userPhoneNumber = "test",
            userName = "test",
            userIntroduction = ""
        )
        userService.registerUser(mockRequest)

        runCatching {
            userService.loginUser(
                LoginRequest(mockRequest.userId, mockRequest.userPassword)
            )
        }.onFailure {
            println(it.stackTraceToString())
            fail("We mocked up user but failed.")
        }.onSuccess {
            assertThat(it.userToken).isNotEqualTo("")
        }

        runCatching {
            userService.loginUser(
                LoginRequest(mockRequest.userId, "mockRequest")
            )
        }.onSuccess {
            fail("Password is wrong, but it succeed?")
        }.onFailure {
            assertThat(it is ForbiddenException).isEqualTo(true)
        }
    }

    @Test
    fun is_registerClass_works_well() {
        val loginToken: String = login()
        val savedCommunity: Community = communityRepository.save(createCommunityObject("A"))

        runCatching {
            userService.registerClass(loginToken, savedCommunity.id)
        }.onFailure {
            println(it.stackTraceToString())
            fail("We've set up all of data but it failed")
        }.onSuccess {
            assertThat(it.size).isEqualTo(1)
        }
    }

    @Test
    fun is_getDetailedClassInfo_works_well() {
        val savedCommunity: Community = communityRepository.save(createCommunityObject("A"))

        val found: Community = userService.getDetailedClassInfo(savedCommunity.id)

        assertThat(found.id).isEqualTo(savedCommunity.id)
    }

    @Test
    fun is_getSimpleClassList_works_well() {
        communityRepository.save(
            createCommunityObject("B")
        )
        communityRepository.save(
            createCommunityObject("A", true)
        )

        val classList: List<SimplifiedCommunity> = userService.getSimpleClassList()

        assertThat(classList.size).isEqualTo(1)
    }

    @Test
    fun is_createClass_works_well() {
        val loginToken: String = login()
        val communityAddRequest: CommunityAddRequest = CommunityAddRequest(
            contentTitle = "Class Test",
            contentAuthor = "KangDroid",
            innerContent = "We are~",
            contentNeeds = "Pencils",
            contentDeadline = "2021.06",
            contentRecruitment = 4,
            gardenReservationRequest = GardenReservationRequest(
                reservationSpace = "A",
                reservationStartTime = System.currentTimeMillis()
            )
        )
        userService.createClass(loginToken, communityAddRequest)
    }

    @Test
    fun is_getSealedUser_works_well() {
        val loginToken: String = login()
        val sealedUser: SealedUser = userService.getSealedUser(loginToken).also {
            assertThat(it.userName).isEqualTo("kangdroid")
        }
    }

    @Test
    fun is_getParticipatedClass_works_well() {
        val loginToken: String = login()
        val listCommunity: List<SimplifiedMyPageCommunity> =
            userService.getParticipatedClass(loginToken)

        assertThat(listCommunity.size).isEqualTo(0)
    }

    @Test
    fun is_getHostedClass_works_well() {
        val loginToken: String = login()
        val listCommunity: List<SimplifiedMyPageCommunity> =
            userService.getHostedClass(loginToken)

        assertThat(listCommunity.size).isEqualTo(0)
    }

    @Test
    fun is_getClassParticipants_works_well() {
        val loginToken: String = login()
        val savedClass: Community = communityRepository.save(createCommunityObject("A"))

        val managerConfirmCommunity: ManagerConfirmCommunity = userService.getClassParticipants(loginToken, savedClass.id)

        assertThat(managerConfirmCommunity.participantsList.isEmpty()).isEqualTo(true)
    }

    @Test
    fun is_confirmClassParticipants_works_well() {
        val loginToken: String = login() // Create User

        // Second User
        val secondUser: User = userRepository.save(
            createMockUser("kdr").apply {
                roles = setOf("ROLE_USER")
            }
        )
        val secondToken: String = jwtTokenProvider.createToken(secondUser.userId, secondUser.roles.toList())

        val communityAddRequest: CommunityAddRequest = CommunityAddRequest(
            contentTitle = "Class Test",
            contentAuthor = "KangDroid",
            innerContent = "We are~",
            contentNeeds = "Pencils",
            contentDeadline = "2021.06",
            contentRecruitment = 4,
            gardenReservationRequest = GardenReservationRequest(
                reservationSpace = "A",
                reservationStartTime = System.currentTimeMillis()
            )
        )
        userService.createClass(loginToken, communityAddRequest)
        val targetCommunity: Community = communityRepository.findAll()[0]

        userService.registerClass(secondToken, targetCommunity.id)

        runCatching {
            userService.confirmClassParticipants(targetCommunity.id, secondUser.userName)
        }.onSuccess {
            val managerConfirmCommunity: ManagerConfirmCommunity = userService.getClassParticipants(loginToken, targetCommunity.id).also {
                assertThat(it.participantsList.size).isEqualTo(1)
                assertThat(it.participantsList[0].userName).isEqualTo("kdr")
            }

            val participatedClass: List<SimplifiedMyPageCommunity> = userService.getParticipatedClass(secondToken).also {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].id).isEqualTo(targetCommunity.id)
            }
        }
    }
}