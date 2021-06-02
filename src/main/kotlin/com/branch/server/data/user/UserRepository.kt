package com.branch.server.data.user

import com.branch.server.error.exception.NotFoundException
import com.branch.server.error.exception.UnknownErrorException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val mongoTemplate: MongoTemplate
) {
    // Field Declare
    private val userIdField: String = "userId"

    fun addUser(user: User): User = mongoTemplate.save(user)

    fun findUserById(userId: String): User {
        val userList: List<User> = mongoTemplate.find(
            Query(Criteria.where(userIdField).`is`(userId))
        )

        // Ensure user list contains 'exactly' one.
        ensureUserListContainsOne(userList)

        return userList[0]
    }

    private fun ensureUserListContainsOne(userList: List<User>) {
        if (userList.size != 1) {
            throw UnknownErrorException("User find result is not exactly 1!")
        }
    }
}