package com.branch.server.data.user

import com.branch.server.error.exception.NotFoundException
import com.branch.server.error.exception.UnknownErrorException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val mockUser: User = User(
        userId = "kangdroid",
        userPassword = "test",
        userName = "KangDroid",
        userAddress = "test",
        userPhoneNumber = "test"
    )

    @BeforeEach
    @AfterEach
    fun initTest() {
        mongoTemplate.remove(Query(), User::class.java)
    }

    @Test
    fun is_addUser_works_well() {
        val mockUser: User = User(
            userId = "kangdroid",
            userPassword = "test",
            userName = "KangDroid",
            userAddress = "test",
            userPhoneNumber = "test"
        )
        // Save
        userRepository.addUser(mockUser)

        // Find
        val userList: List<User> = mongoTemplate.findAll()
        assertThat(userList.isNotEmpty()).isEqualTo(true)
        assertThat(userList.size).isEqualTo(1)
        assertThat(userList[0].userId).isEqualTo(mockUser.userId)
    }

    @Test
    fun is_findUserById_works_well() {
        // Save
        userRepository.addUser(mockUser)
        runCatching {
            userRepository.findUserById(mockUser.userId)
        }.onSuccess {
            assertThat(it.userId).isEqualTo(mockUser.userId)
        }.onFailure {
            println(it.stackTraceToString())
            fail("User data is set but it failed")
        }
    }

    @Test
    fun is_findUserById_throws_unknownError() {
        runCatching {
            userRepository.findUserById(mockUser.userId)
        }.onSuccess {
            fail("We do not have entity but it succeed?")
        }.onFailure {
            assertThat(it is UnknownErrorException).isEqualTo(true)
        }
    }

}