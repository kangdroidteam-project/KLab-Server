package com.branch.server.controller

import com.branch.server.data.entity.community.Community
import com.branch.server.data.request.CommunityAddRequest
import com.branch.server.data.request.LoginRequest
import com.branch.server.data.request.RegisterRequest
import com.branch.server.data.response.LoginResponse
import com.branch.server.data.response.SimplifiedCommunity
import com.branch.server.data.response.SimplifiedMyPageCommunity
import com.branch.server.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService
) {
    private fun getTokenFromHeader(httpHeaders: HttpHeaders): String {
        return httpHeaders["X-AUTH-TOKEN"]!![0]
    }
    @PostMapping("/api/v1/user")
    fun registerUser(@RequestBody registerRequest: RegisterRequest): ResponseEntity<Unit> {
        userService.registerUser(registerRequest)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/v1/user/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.loginUser(loginRequest))
    }

    @PostMapping("/api/v1/user/class/{id}")
    fun registerClass(@RequestHeader httpHeaders: HttpHeaders, @PathVariable("id") id: Long): ResponseEntity<List<SimplifiedMyPageCommunity>> {
        val userToken: String = getTokenFromHeader(httpHeaders)
        return ResponseEntity.ok(
            userService.registerClass(userToken, id)
        )
    }

    @GetMapping("/api/v1/class/{id}")
    fun getDetailedClass(@RequestHeader httpHeaders: HttpHeaders, @PathVariable("id") id: Long): ResponseEntity<Community> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                userService.getDetailedClassInfo(id)
            )
    }

    @GetMapping("/api/v1/class")
    fun getClassList(@RequestHeader httpHeaders: HttpHeaders): ResponseEntity<List<SimplifiedCommunity>> {
        return ResponseEntity
            .ok(
                userService.getSimpleClassList()
            )
    }

    @PostMapping("/api/v1/class")
    fun addClass(@RequestHeader httpHeaders: HttpHeaders, @RequestBody communityAddRequest: CommunityAddRequest): ResponseEntity<Unit> {
        userService.createClass(getTokenFromHeader(httpHeaders), communityAddRequest)
        return ResponseEntity.noContent().build()
    }
}