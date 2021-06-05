package com.branch.server.data.response

data class ManagerConfirmCommunity(
    var communityTitle: String,
    var communityTotalRecruitment: Int,
    var communityCurrentRecruitment: Int,
    var participantsList: List<SealedUser>
)