package com.branch.server.data.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors
import javax.persistence.*

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1,

    @Column(unique = true)
    var userId: String,
    var userPassword: String,
    var userName: String,
    var userAddress: String,
    var userPhoneNumber: String,
    var userIntroduction: String? = null,

    @ElementCollection(fetch = FetchType.EAGER) // Always Fetch all together
    var roles: Set<String> = setOf()
): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return roles.stream()
            .map { role: String? ->
                SimpleGrantedAuthority(
                    role
                )
            }
            .collect(Collectors.toList())
    }

    override fun getPassword() = userPassword
    override fun getUsername(): String = userName
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}