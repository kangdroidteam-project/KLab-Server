package com.branch.server.data.response

data class SimplifiedCommunity( // Community Response
    var id: Long, // Class ID
    var contentTitle: String, // 제목
    var contentRecruitment: Int, // 인원 제한[총 정원]
    var currentRecruitment: Int, // 현재 정원
    var contentNeeds: String, // 무엇이 필요한가
)