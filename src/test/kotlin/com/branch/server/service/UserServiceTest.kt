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
import com.branch.server.error.exception.ConflictException
import com.branch.server.error.exception.ForbiddenException
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
                userAddress = loginUser.userAddress
            )
        )

        return userService.loginUser(
            LoginRequest(
                userId = loginUser.userId,
                userPassword = loginUser.userPassword
            )
        ).userToken
    }

    private fun createCommunityObject(reservationSpace: String): Community {
        val secondReservation: GardenReservation = GardenReservation(
            reservationStartTime = System.currentTimeMillis(),
            reservationEndTime = System.currentTimeMillis() + 200,
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
            isCommunityExpired = false,
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
            userName = "test"
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
            userName = "test"
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
            val medianList: List<MedianTable> =
                medianRepository.findAllByTargetUser_UserId(loginUser.userId)

            assertThat(medianList.size).isEqualTo(1)
        }
    }

    @Test
    fun is_getDetailedClassInfo_works_well() {
        val savedCommunity: Community = communityRepository.save(createCommunityObject("A"))

        val found: Community = userService.getDetailedClassInfo(savedCommunity.id)

        assertThat(found.id).isEqualTo(savedCommunity.id)
    }
}