package com.branch.server.data.entity.median

import org.springframework.stereotype.Repository

@Repository
class MedianTableRepository(
    private val rawMedianRepository: RawMedianRepository
) {
    fun deleteAll() = rawMedianRepository.deleteAll()
    fun save(table: MedianTable): MedianTable = rawMedianRepository.save(table)
    fun findAllByTargetUser_UserId(userId: String): List<MedianTable> = rawMedianRepository.findAllByTargetUser_UserId(userId)
    fun findAllByTargetCommunity_Id(communityId: Long): List<MedianTable> = rawMedianRepository.findAllByTargetCommunity_Id(communityId)
    fun findByTargetUser_UserNameAndTargetCommunity_Id(userName: String, communityId: Long): MedianTable = rawMedianRepository.findByTargetUser_UserNameAndTargetCommunity_Id(userName, communityId)
}