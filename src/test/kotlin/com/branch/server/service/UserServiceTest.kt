package com.branch.server.service

import com.branch.server.data.request.RegisterRequest
import com.branch.server.data.user.User
import com.branch.server.data.user.UserRepository
import com.branch.server.error.exception.ConflictException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class UserServiceTest {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    @AfterEach
    fun initTest() {
        mongoTemplate.remove(Query(), User::class.java)
    }

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
            val user: User = userRepository.findUserById(mockRequest.userId)
            assertThat(user.userId).isEqualTo(mockRequest.userId)
        }

        // Request one more
        runCatching {
            userService.registerUser(mockRequest)
        }.onSuccess {
            fail("Duplicated should exists, but it succeed?")
        }.onFailure {
            assertThat(it is ConflictException).isEqualTo(true)
        }
    }
}