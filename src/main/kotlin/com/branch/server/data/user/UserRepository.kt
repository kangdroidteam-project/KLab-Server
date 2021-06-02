package com.branch.server.data.user

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val mongoTemplate: MongoTemplate
) {
    fun addUser(user: User): User = mongoTemplate.save(user)
}