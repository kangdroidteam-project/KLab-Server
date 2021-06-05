package com.branch.server.data.entity.community

import com.branch.server.data.entity.reservation.GardenReservation
import javax.persistence.*

@Entity
class Community(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1,
    var contentTitle: String, // 제목
    var contentAuthor: String, // 만든사람
    @Column(columnDefinition = "TEXT")
    var contentPicture: String? = null, // 사진 [없으면 null]
    var innerContent: String, // 내용
    var contentNeeds: String, // 무엇이 필요한가
    var contentDeadline: String, // 데드라인
    var firstMeeting: String, // 첫 만남
    var contentRecruitment: Int, // 인원 제한[총 정원]
    var currentRecruitment: Int, // 현재 정원
    var isCommunityExpired: Boolean, // true인 경우 모집 마감, false 인 경우 모집 진행

    // Always fetch, when calling community, cascade to ALL
    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name="garden_id")
    var gardenReservation: GardenReservation
)