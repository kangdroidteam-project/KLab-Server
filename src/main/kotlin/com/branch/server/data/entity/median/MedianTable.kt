package com.branch.server.data.entity.median

import com.branch.server.data.entity.community.Community
import com.branch.server.data.entity.user.User
import javax.persistence.*

@Entity
class MedianTable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_med_id")
    var targetUser: User,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "community_med_id")
    var targetCommunity: Community,

    var isRequestConfirmed: Boolean = false
)