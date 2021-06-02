package com.branch.server.data.user

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("user")
data class User(
    @Id
    var id: ObjectId = ObjectId(),
    var userId: String,
    var userPassword: String,
    var userName: String,
    var userAddress: String,
    var userPhoneNumber: String
)