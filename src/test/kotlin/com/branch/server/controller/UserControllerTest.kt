package com.branch.server.controller

import com.branch.server.data.entity.community.Community
import com.branch.server.data.entity.community.CommunityRepository
import com.branch.server.data.entity.median.MedianTableRepository
import com.branch.server.data.entity.reservation.GardenReservation
import com.branch.server.data.entity.user.User
import com.branch.server.data.request.LoginRequest
import com.branch.server.data.request.RegisterRequest
import com.branch.server.data.response.LoginResponse
import com.branch.server.data.entity.user.UserRepository
import com.branch.server.data.request.CommunityAddRequest
import com.branch.server.data.request.GardenReservationRequest
import com.branch.server.data.response.SealedUser
import com.branch.server.data.response.SimplifiedCommunity
import com.branch.server.data.response.SimplifiedMyPageCommunity
import com.branch.server.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
internal class UserControllerTest {
    @LocalServerPort
    private var port: Int = -1

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var communityRepository: CommunityRepository

    @Autowired
    private lateinit var medianRepository: MedianTableRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var serverBaseAddress: String

    @BeforeEach
    @AfterEach
    fun initTest() {
        medianRepository.deleteAll()
        userRepository.deleteAll()
        communityRepository.deleteAll()
        serverBaseAddress = "http://localhost:${port}"
    }

    private fun createCommunityObject(reservationSpace: String): Community {
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

    @Test
    fun is_register_works_well() {
        val mockRegisterRequest: RegisterRequest = RegisterRequest(
            userId = "test",
            userPassword = "test",
            userAddress = "test",
            userPhoneNumber = "test",
            userName = "test",
            userIntroduction = ""
        )

        runCatching {
            restTemplate.postForEntity<Unit>("${serverBaseAddress}/api/v1/user", mockRegisterRequest)
        }.onSuccess {
            assertThat(it.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        }
    }

    @Test
    fun is_login_works_well() {
        val mockRegisterRequest: RegisterRequest = RegisterRequest(
            userId = "test",
            userPassword = "test",
            userAddress = "test",
            userPhoneNumber = "test",
            userName = "test",
            userIntroduction = ""
        )
        restTemplate.postForEntity<Unit>("${serverBaseAddress}/api/v1/user", mockRegisterRequest)

        val mockLoginRequest: LoginRequest = LoginRequest(
            userId = mockRegisterRequest.userId,
            userPassword = mockRegisterRequest.userPassword
        )
        runCatching {
            restTemplate.postForEntity<LoginResponse>("${serverBaseAddress}/api/v1/user/login", mockLoginRequest)
        }.onSuccess {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.hasBody()).isEqualTo(true)
            assertThat(it.body!!.userToken).isNotEqualTo("")
        }
    }

    @Test
    fun is_registerClass_works_well() {
        val savedCommunity: Community = communityRepository.save(
            createCommunityObject("A")
        )

        val httpHeaders: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", login())
        }

        runCatching {
            restTemplate.exchange<List<SimplifiedMyPageCommunity>>("${serverBaseAddress}/api/v1/user/class/${savedCommunity.id}", HttpMethod.POST, HttpEntity<Unit>(httpHeaders))
        }.onSuccess {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.hasBody()).isEqualTo(true)
            assertThat(it.body!!.size).isEqualTo(1)
        }
    }

    @Test
    fun is_getDetailedClass_works_well() {
        val savedCommunity: Community = communityRepository.save(
            createCommunityObject("A")
        )

        val httpHeaders: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", login())
        }

        runCatching {
            restTemplate.exchange<Community>("${serverBaseAddress}/api/v1/class/${savedCommunity.id}", HttpMethod.GET, HttpEntity<Unit>(httpHeaders))
        }.onSuccess {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.hasBody()).isEqualTo(true)
            assertThat(it.body!!.gardenReservation.reservationSpace).isEqualTo("A")
        }
    }

    @Test
    fun is_getClassList_works_well() {
        val savedCommunity: Community = communityRepository.save(
            createCommunityObject("A")
        )

        val httpHeaders: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", login())
        }

        runCatching {
            restTemplate.exchange<List<SimplifiedCommunity>>("${serverBaseAddress}/api/v1/class", HttpMethod.GET, HttpEntity<Unit>(httpHeaders))
        }.onSuccess {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.hasBody()).isEqualTo(true)
            assertThat(it.body!!.size).isEqualTo(1)
        }
    }

    @Test
    fun is_createClass_works_well() {
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
        val httpHeaders: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", login())
        }
        runCatching {
            restTemplate.exchange<Unit>("${serverBaseAddress}/api/v1/class", HttpMethod.POST, HttpEntity<CommunityAddRequest>(communityAddRequest, httpHeaders))
        }.onSuccess {
            assertThat(it.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        }
    }

    @Test
    fun is_getSealedUser_works_well() {
        val httpHeaders: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", login())
        }

        runCatching {
            restTemplate.exchange<SealedUser>("${serverBaseAddress}/api/v1/user", HttpMethod.GET, HttpEntity<Unit>(httpHeaders))
        }.onSuccess {
            assertThat(it.statusCode).isEqualByComparingTo(HttpStatus.OK)
            assertThat(it.hasBody()).isEqualTo(true)
        }
    }

    @Test
    fun is_getUserRelatedClass_works_well() {
        val httpHeaders: HttpHeaders = HttpHeaders().apply {
            add("X-AUTH-TOKEN", login())
        }

        runCatching {
            restTemplate.exchange<List<SimplifiedMyPageCommunity>>("${serverBaseAddress}/api/v1/user/class?isParticipant=true", HttpMethod.GET, HttpEntity<Unit>(httpHeaders))
        }.onSuccess {
            assertThat(it.statusCode).isEqualByComparingTo(HttpStatus.OK)
            assertThat(it.hasBody()).isEqualTo(true)
        }

        runCatching {
            restTemplate.exchange<List<SimplifiedMyPageCommunity>>("${serverBaseAddress}/api/v1/user/class?isParticipant=false", HttpMethod.GET, HttpEntity<Unit>(httpHeaders))
        }.onSuccess {
            assertThat(it.statusCode).isEqualByComparingTo(HttpStatus.OK)
            assertThat(it.hasBody()).isEqualTo(true)
        }
    }

}