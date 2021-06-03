package com.branch.server.security

import com.branch.server.data.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(userId: String): UserDetails {
        return userRepository.findByUserId(userId)
    }
}