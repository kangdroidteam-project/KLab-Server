package com.branch.server.controller

import com.branch.server.data.request.LoginRequest
import com.branch.server.data.request.RegisterRequest
import com.branch.server.data.response.LoginResponse
import com.branch.server.data.entity.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
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
    private lateinit var userRepository: UserRepository

    private lateinit var serverBaseAddress: String

    @BeforeEach
    @AfterEach
    fun initTest() {
        userRepository.deleteAll()
        serverBaseAddress = "http://localhost:${port}"
    }

    @Test
    fun is_register_works_well() {
        val mockRegisterRequest: RegisterRequest = RegisterRequest(
            userId = "test",
            userPassword = "test",
            userAddress = "test",
            userPhoneNumber = "test",
            userName = "test"
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
            userName = "test"
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
}