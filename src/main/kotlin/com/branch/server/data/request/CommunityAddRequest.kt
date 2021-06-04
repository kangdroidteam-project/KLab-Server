package com.branch.server.data.request

import com.branch.server.data.entity.community.Community
import com.branch.server.data.entity.reservation.GardenReservation
import java.util.*

class CommunityAddRequest (
    var contentTitle: String, // 제목
    var contentAuthor: String, // 만든사람
    var contentPicture: String? = null, // 사진 [없으면 null]
    var innerContent: String, // 내용
    var contentNeeds: String, // 무엇이 필요한가
    var contentDeadline: String, // 데드라인
    var contentRecruitment: Int, // 인원 제한[총 정원]
    var gardenReservationRequest: GardenReservationRequest // 정원 예약
) {
    fun toCommunity(): Community = Community(
        contentTitle = contentTitle,
        contentAuthor = contentAuthor,
        contentPicture = contentPicture,
        innerContent = innerContent,
        contentNeeds = contentNeeds,
        contentDeadline = contentDeadline,
        contentRecruitment = contentRecruitment,
        currentRecruitment = 0,
        isCommunityExpired = false,
        firstMeeting = Date(gardenReservationRequest.reservationStartTime).toString(),
        gardenReservation = GardenReservation(
            reservationSpace = gardenReservationRequest.reservationSpace,
            reservationStartTime = gardenReservationRequest.reservationStartTime
        )
    )
}