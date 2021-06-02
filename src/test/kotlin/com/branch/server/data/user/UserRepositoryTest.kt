package com.branch.server.data.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAll
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
internal class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

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
}