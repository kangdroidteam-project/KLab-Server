package com.branch.server.data.entity.median

import org.springframework.data.jpa.repository.JpaRepository

interface RawMedianRepository: JpaRepository<MedianTable, Long> {
    fun findAllByTargetUser_UserId(userId: String): List<MedianTable>
    fun findAllByTargetCommunity_Id(communityId: Long): List<MedianTable>
}